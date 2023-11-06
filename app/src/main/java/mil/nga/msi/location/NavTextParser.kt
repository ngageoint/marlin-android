package mil.nga.msi.location

import mil.nga.msi.coordinate.DMS
import mil.nga.msi.coordinate.WGS84
import mil.nga.msi.nlp.NumberNormalizer
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.FeatureCollection
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Polygon
import mil.nga.sf.geojson.Position

private const val METERS_IN_NAUTICAL_MILE = 1852

data class LocationWithType(
   val location: List<String> = emptyList(),
   val locationType: String? = null,
   val locationDescription: String? = null,
   val distanceFromLocation: String? = null
) {
   private fun asPoint(text: String): Point? {
      val latLng = WGS84.from(text)
      return if (latLng != null) {
         Point(Position(latLng.longitude, latLng.latitude))
      } else {
         DMS.from(text)?.toLatLng()?.let {
            Point(Position(it.longitude, it.latitude))
         }
      }
   }

   fun asFeature(): Feature? {
      return when (locationType) {
         "Point" -> {
            location.firstOrNull()?.let { location ->
               asPoint(location)?.let {
                  Feature(it)
               }
            }
         }
         "Circle" -> {
            location.firstOrNull()?.let { location ->
               asPoint(location)?.let {
                  Feature(it).apply {
                     properties = mapOf("radius" to metersDistance())
                  }
               }
            }
         }
         "LineString" -> {
            val points = location.mapNotNull { asPoint(it) }
            Feature(LineString(points))
         }
         "Polygon" -> {
            val points = location.mapNotNull { asPoint(it) }
            Feature(Polygon(listOf(LineString(points))))
         }
         else -> null
      }
   }

   fun metersDistance(): Double? {
      var distance: Double? = null
      if (distanceFromLocation != null) {
         val range = distanceFromLocation.ranges("(MILE)|(METER)")
         val first = range.firstOrNull()
         if (first != null && first.first != 0) {
            val beginningText = distanceFromLocation.substring(0, first.first).trim()
            val wordSplit = beginningText.split(" ").reversed()
            val lastParts = mutableListOf<String>()
            var tempParsedNumber: Double? = null
            wordSplit.forEach { split ->
               lastParts.add(0, split)
               val parsed = lastParts.joinToString(" ").toDoubleOrNull()
               tempParsedNumber = parsed ?: NumberNormalizer.wordToNumberOrNull(lastParts.joinToString(" "))?.toDouble()
            }

            tempParsedNumber?.let { number ->
               distance = if (distanceFromLocation.contains("MILE")) {
                  number * METERS_IN_NAUTICAL_MILE
               } else number
            }
         }
      }

      return distance
   }
}

data class MappedLocation(
   val locationName: String? = null,
   val locationType: String? = null,
   val specificArea: String? = null,
   val subject: String? = null,
   val cancelTime: String? = null,
   val location: List<LocationWithType> = emptyList(),
   val whenTime: String? = null,
   val what: String? = null,
   val extra: String? = null,
   val dnc: String? = null,
   val chart: String? = null
) {
   fun featureCollection(): FeatureCollection? {
      val features = location.mapNotNull { it.asFeature() }
      return if (features.isNotEmpty()) {
         FeatureCollection(features)
      } else null
   }
}

class NavTextParser {

   private var areaName: String? = null
   private var specificArea: String? = null
   private var subject: String? = null
   private val extras = mutableListOf<String>()
   private var chart: String? = null
   private var dnc: String? = null
   private var currentLocationType = "Point"
   private var firstDistance: String? = null
   private var numberDistance: String? = null
   private var locations = mutableListOf<LocationWithType>()

   fun parseToMappedLocation(text: String): MappedLocation {
      val (heading, sections) = getHeadingAndSections(text)

      if (heading != null) {
         val sentences = splitSentences(heading)
         parseHeading(sentences)
      }

      if (sections != null) {
         if (sections.startsWith("1.")) {
            splitNumbers(sections).forEach { parseNumber(it) }
         } else {
            if (sections.startsWith("A.")) {
               splitLetters(sections).forEach { parseLetter(it) }
            }
         }
      }

      return MappedLocation(
         locationName = areaName,
         specificArea = specificArea,
         subject = subject,
         location = locations,
         dnc = dnc,
         chart = chart,
         extra = extras.joinToString("\n"),
      )
   }

   fun getHeadingAndSections(text: String): Pair<String?, String?> {
      var heading: String? = null
      var sections: String? = null

      val match = Regex("\\b[1A]\\. ").find(text)
      if (match != null) {
         val range = match.range
         val lowerBound = range.first
         if (lowerBound != 0) {
            heading = text.substring(0, lowerBound - 1).trim()
         }
         sections = text.substring(lowerBound).trim()
      } else {
         heading = text.trim()
      }

      return Pair(heading, sections)
   }

   private fun parseDNCAndChart(line: String): Boolean {
      var found = false

      if (dnc == null) {
         val dncRange = line.ranges("(DNC ){1}[0-9]*")
         dnc = dncRange.map { line.substring(it) }.firstOrNull()
         found = dnc?.isNotEmpty() == true
      }

      return found
   }

   fun splitChartFromLine(
      chart: String? = null,
      dnc: String? = null,
      line: String
   ): String {
      return if (chart != null) {
         line.deletingPrefix(chart).deletingPrefix(".").trim()
      } else if (dnc != null) {
         line.deletingPrefix(dnc).deletingPrefix(".").trim()
      } else line
   }

   private fun parseCurrentLocationType(line: String) {
      if (line.ranges("AREA[S]? BOUND").isNotEmpty()) {
         currentLocationType = "Polygon"
      } else if (line.ranges("AREA[S]? WITHIN").isNotEmpty()) {
         currentLocationType = "Circle"
      } else if (line.contains("TRACKLINE")) {
         currentLocationType = "LineString"
      } else if (line.contains("POSITION") || line.contains("VICINITY")) {
         currentLocationType = "Point"
      } else if (line.contains("IN VICINITY")) {
         currentLocationType = "Point"
      }
   }

   private fun parseHeading(heading: List<String>) {
      firstDistance = parseDistance(heading.joinToString(" "))

      var foundChart: Boolean
      val stringLocations = mutableListOf<String>()
      heading.forEach { line ->
         var toParse = line
         parseCurrentLocationType(toParse)
         foundChart = parseDNCAndChart(toParse)

         if (foundChart) {
            toParse = splitChartFromLine(chart, dnc, line)
         }

         if (toParse.isNotEmpty()) {
            if (areaName == null) {
               areaName = toParse
            } else if (specificArea == null) {
               specificArea = toParse
            } else if (subject == null) {
               subject = toParse
            } else {
               extras.add(toParse)
            }
         }

         val locationRanges = toParse.ranges("[0-9]{1,3}-{1}[0-9]{2}(-[0-9]{2})?(\\.{1}[0-9]+)?[NS]{1} {1}[0-9]{1,3}-{1}[0-9]{2}(-[0-9]{2})?(\\.{1}[0-9]+)?[EW]")
         stringLocations.addAll(locationRanges.map { toParse.substring(it) })
      }

      if (stringLocations.isNotEmpty()) {
         locations.add(
            LocationWithType(
               location = stringLocations,
               locationType = currentLocationType,
               locationDescription = subject,
               distanceFromLocation = firstDistance
            )
         )
      }
   }

   fun parseDistance(line: String): String? {
      var distance: String? = null

      if (line.ranges("(?!=\\.)[^\\.]*? BERTH").isNotEmpty()) {
         val range = line.ranges("(?!=\\.)[^\\.]*? BERTH")
         distance = range.map { line.substring(it) }.firstOrNull()?.trim()
      } else if (line.ranges("WITHIN").isNotEmpty()) {
         val range = line.ranges("(?<=WITHIN ).*(?= OF)")
         distance = range.map { line.substring(it) }.firstOrNull()
      }

      return distance
   }

   private fun parseNumber(numberSection: String) {
      val (heading, letters) = splitLettersFromHeading(numberSection)
      if (heading != null) {
         numberDistance = parseDistance(heading)
         val distance = numberDistance ?: firstDistance
         extras.add(heading)
         val (description, parsedLocations) = splitDescriptionFromLocation(heading)
         if (description != null) {
            parseCurrentLocationType(description)
            if (subject == null) {
               subject = description
            }
         }

         if (parsedLocations?.isNotEmpty() == true) {
            locations.add(
               LocationWithType(
                  location = parsedLocations,
                  locationType = currentLocationType,
                  locationDescription = description ?: heading,
                  distanceFromLocation = distance
               )
            )
         }
      }

      if (letters != null) {
         splitLetters(letters).forEach { letter ->
            parseLetter(letter, heading)
         }
      }
   }

   private fun parseLetter(letterSection: String, numberSectionDescription: String? = null) {
      var currentLetterDescription = if (numberSectionDescription != null) {
         mutableListOf(numberSectionDescription)
      } else mutableListOf()

      var currentLocations = mutableListOf<String>()
      val distance = parseDistance(letterSection) ?: numberDistance ?: firstDistance
      val sentences = splitSentences(letterSection)
      extras.addAll(sentences)
      sentences.forEach { sentence ->
         if (!sentence.contains(" ")) {
            if (currentLocations.isNotEmpty()) {
               locations.add(
                  LocationWithType(
                     location = currentLocations,
                     locationType = currentLocationType,
                     locationDescription = currentLetterDescription.joinToString(" ") { it.trim() },
                     distanceFromLocation = distance
                  )
               )
               currentLocations = mutableListOf()
               currentLetterDescription = mutableListOf()
            }
         } else {
            val (description, parsedLocations) = splitDescriptionFromLocation(sentence)
            if (description != null) {
               parseCurrentLocationType(description)
               currentLetterDescription.add(description)
            }
            if (parsedLocations?.isNotEmpty() == true) {
               currentLocations.addAll(parsedLocations)
            }
         }
      }

      if (currentLocations.isNotEmpty()) {
         locations.add(
            LocationWithType(
               location = currentLocations,
               locationType = currentLocationType,
               locationDescription = currentLetterDescription.joinToString(" ") { it.trim() },
               distanceFromLocation = distance
            )
         )
      }
   }

   private fun splitDescriptionFromLocation(text: String): Pair<String?, List<String>?> {
      val locationRanges = text.ranges("[0-9]{1,3}-{1}[0-9]{2}(-[0-9]{2})?(\\.{1}[0-9]+)?[NS]{1} {1}[0-9]{1,3}-{1}[0-9]{2}(-[0-9]{2})?(\\.{1}[0-9]+)?[EW]")
      return if (locationRanges.isEmpty()) {
         Pair(text, null)
      } else {
         var description: String? = null

         locationRanges.firstOrNull()?.let { first ->
            if (first.first != 0) {
               val finalIndex = if (first.first == text.length) first.first else first.first - 1
               description = text.substring(0, finalIndex).trim()
            }
         }
         val locations = locationRanges.map { text.substring(it) }

         locationRanges.lastOrNull()?.let { last ->
            if (last.last != text.length) {
               val finalIndex = last.last
               val endDescription = text.substring(finalIndex).trim()
               description = if (description != null) {
                  "$description $endDescription"
               } else {
                  endDescription
               }
            }
         }

         Pair(description, locations)
      }
   }

   private fun splitLettersFromHeading(text: String): Pair<String?, String?> {
      var heading: String? = null
      var letters: String? = null

      val regex = Regex("\\bA\\. ")
      val match = regex.find(text)
      if (match != null) {
         val range = match.range
         val lowerBound = range.first
         if (lowerBound != 0) {
            heading = text.substring(0, lowerBound - 1).split("\n").joinToString(" ") { it.trim() }
         }
         letters = text.substring(lowerBound)
      } else {
         heading = text.split("\n").joinToString(" ") { it.trim() }
      }

      return Pair(heading, letters)
   }

   fun splitSentences(text: String): List<String> {
      // split on new lines and trim the extra white space
       return text
          .lines()
          .joinToString(" ") { it.trim() }
          .split(". ")
          .filter { it.isNotEmpty() }
          .map { if (it.endsWith(".")) it else "$it.".trim() }
   }

   fun splitLetters(text: String): List<String> {
      val ranges = text.ranges("(?<letters>[\\w]+\\. (?<letterContent>[\\W\\w]*?)(?=([\\w]+\\. [\\w])|($)))")
      return ranges.map { text.substring(it).trim() }
   }

   fun splitNumbers(text: String): List<String> {
      val ranges = text.ranges("(?<numbers>[\\d]+\\. (?<numberContent>[\\W\\w]*?)(?=([\\d]+\\. [\\w])|($)))")
      return ranges.map { text.substring(it).trim() }
   }

   private fun String.deletingPrefix(prefix: String): String {
      if (!this.startsWith(prefix)) return this
      return this.drop(prefix.count())
   }
}

fun String.ranges(regex: String): List<IntRange> {
   return Regex(regex).findAll(this).map { it.range }.toList()
}