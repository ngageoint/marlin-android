package mil.nga.msi.datasource.light

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import java.util.regex.Matcher
import java.util.regex.Pattern

enum class LightColor(val color: Color) {
   WHITE(Color(0xFFFFFE00)),
   YELLOW(Color(0xFFFFFE00)),
   GREEN(Color(0xFF0DE319)),
   RED(Color(0xFFFA0000)),
   BLUE(Color(0xFF0000FF)),
   VIOLET(Color(0xFFAF52DE)),
   ORANGE(Color(0xFFFF9500)),
   BUOY(Color(0xFF87978b)),
   RACON(Color(0xFFB52BB5))
}

@Serializable
@Entity(
   tableName = "lights",
   primaryKeys = ["volume_number", "feature_number", "characteristic_number"],
   indices = [Index(value = ["id"], unique = true)]
)
data class Light(
   @ColumnInfo(name = "id")
   val id: String,

   @ColumnInfo(name = "volume_number")
   val volumeNumber: String,

   @ColumnInfo(name = "feature_number")
   val featureNumber: String,

   @ColumnInfo(name = "characteristic_number")
   val characteristicNumber: Int,

   @ColumnInfo(name = "notice_week")
   var noticeWeek: String,

   @ColumnInfo(name = "notice_year")
   var noticeYear: String,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "international_feature")
   var internationalFeature: String? = null

   @ColumnInfo(name = "aid_type")
   var aidType: String? = null

   @ColumnInfo(name = "geopolitical_heading")
   var geopoliticalHeading: String? = null

   @ColumnInfo(name = "region_heading")
   var regionHeading: String? = null

   @ColumnInfo(name = "subregion_heading")
   var subregionHeading: String? = null

   @ColumnInfo(name = "local_heading")
   var localHeading: String? = null

   @ColumnInfo(name = "preceding_note")
   var precedingNote: String? = null

   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "position")
   var position: String? = null

   @ColumnInfo(name = "characteristic")
   var characteristic: String? = null

   @ColumnInfo(name = "height_feet")
   var heightFeet: Float? = null

   @ColumnInfo(name = "height_meters")
   var heightMeters: Float? = null

   @ColumnInfo(name = "range")
   var range: String? = null

   @ColumnInfo(name = "structure")
   var structure: String? = null

   @ColumnInfo(name = "remarks")
   var remarks: String? = null

   @ColumnInfo(name = "post_note")
   var postNote: String? = null

   @ColumnInfo(name = "notice_number")
   var noticeNumber: Int? = null

   @ColumnInfo(name = "remove_from_list")
   var removeFromList: String? = null

   @ColumnInfo(name = "delete_flag")
   var deleteFlag: String? = null

   @ColumnInfo(name = "section_header")
   var sectionHeader: String = ""

   @kotlinx.serialization.Transient
   @Transient
   val latLng = LatLng(latitude, longitude)

   @kotlinx.serialization.Transient
   @Transient
   val isFogSignal = {
      remarks?.contains("bl.", ignoreCase = true) ?: false
   }

   @kotlinx.serialization.Transient
   @Transient
   val isBuoy = {
      structure?.contains("pillar", ignoreCase = true) == true ||
      structure?.contains("spar", ignoreCase = true) == true ||
      structure?.contains("conical", ignoreCase = true) == true ||
      structure?.contains("can", ignoreCase = true) == true
   }

   @kotlinx.serialization.Transient
   @Transient
   val isRacon = {
      name?.let {
         it.contains("RACON") || remarks?.contains("(3 & 10cm)") == true
      } ?: false
   }

   @kotlinx.serialization.Transient
   @Transient
   val morseCode = {
      val firstIndex = characteristic?.indexOfFirst { it == '(' }?.takeIf { it >= 0 }
      val lastIndex = characteristic?.indexOfLast { it == ')' }?.takeIf { it >= 0 }
      if (firstIndex != null && lastIndex != null) {
         characteristic?.substring(IntRange(firstIndex + 1, lastIndex - 1))
      } else null
   }

   @delegate:Transient
   val lightColors by lazy {
      val lightColors = mutableListOf<Color>()

      characteristic?.let { characteristic ->
         if (characteristic.contains("W.")) {
            lightColors.add(LightColor.WHITE.color)
         }
         if (characteristic.contains("R.")) {
            lightColors.add(LightColor.RED.color)
         }
         // why does green have so many variants without a .?
         if (characteristic.contains("G.")
               || characteristic.contains("Oc.G")
               || characteristic.contains("G\n")
               || characteristic.contains("F.G")
               || characteristic.contains("Fl.G")
               || characteristic.contains("(G)")) {
            lightColors.add(LightColor.GREEN.color)
         }

         if (characteristic.contains("Y.")) {
            lightColors.add(LightColor.YELLOW.color)
         }

         if (characteristic.contains("Bu.")) {
            lightColors.add(LightColor.BLUE.color)
         }

         if (characteristic.contains("Vi.")) {
            lightColors.add(LightColor.VIOLET.color)
         }

         if (characteristic.contains("Or.")) {
            lightColors.add(LightColor.ORANGE.color)
         }

         if (lightColors.isEmpty() && characteristic.lowercase().contains("lit")) {
            lightColors.add(LightColor.WHITE.color)
         }
      }

      lightColors
   }

   @delegate:Transient
   val lightSectors by lazy {
      val sectors = mutableListOf<LightSector>()

      // Use Java RegEx here instead of Kotlin.  Kotlin 1.7 has native issues that was causing this to be slow and a memory hog
      val pattern = Pattern.compile("(?<visible>(Visible)?)(?<fullLightObscured>(bscured)?)((?<color>[A-Z]+)?)\\.?(?<unintensified>(\\(unintensified\\))?)(?<obscured>(\\(bscured\\))?)( (?<startdeg>(\\d*))°)?((?<startminutes>[0-9]*)[\\`'])?(-(?<enddeg>(\\d*))°)(?<endminutes>[0-9]*)[\\`']?")
      remarks?.let { remarks ->
         var previousEnd = 0.0
         val matcher: Matcher = pattern.matcher(remarks)
         while (matcher.find()) {
            var end = 0.0
            var start: Double? = null
            var color = ""
            var visibleColor: Color? = null
            var obscured = false
            var fullLightObscured = false
            arrayOf("visible", "fullLightObscured", "color", "unintensified", "obscured", "startdeg", "startminutes", "enddeg", "endminutes").forEach { name ->
               val component = matcher.group(name)
               if (component?.isNotEmpty() == true) {
                  when(name) {
                     "visible" ->  visibleColor = lightColors.firstOrNull()
                     "fullLightObscured" -> {
                        visibleColor = lightColors.firstOrNull()
                        fullLightObscured = true
                     }
                     "color" ->  color = component
                     "obscured" ->  obscured = true
                     "startdeg" -> start = (start ?: 0.0) + (component.toDoubleOrNull() ?: 0.0)
                     "startminutes" ->  start = (start ?: 0.0) + ((component.toDoubleOrNull() ?: 0.0)  / 60)
                     "enddeg" ->  end = component.toDoubleOrNull() ?: 0.0
                     "endminutes" ->  end += (component.toDoubleOrNull() ?: 0.0) / 60

                  }
               }
            }

            val uiColor = when {
               obscured || fullLightObscured -> {
                  visibleColor ?: (lightColors.getOrNull(0) ?: Color.Black)
               }
               color == "W" -> LightColor.WHITE.color
               color == "R" -> LightColor.RED.color
               color == "G" -> LightColor.GREEN.color
               else -> visibleColor ?: Color.Transparent
            }

            var sectorRange: Double? = null
            range?.split(";","/n")?.forEach { split ->
               val rangePart = split.trim().filterNot { it.isWhitespace() }
               if (rangePart.startsWith(color)) {
                  val rangeRegex = Regex("[0-9]+$")
                  val match = rangeRegex.find(rangePart)
                  match?.range?.let { matchRange ->
                     val colorRange = rangePart.substring(matchRange)
                     if (colorRange.isNotEmpty()) {
                        sectorRange = colorRange.toDoubleOrNull()
                     }
                  }
               }
            }

            val startDegrees = start
            if (startDegrees != null) {
               sectors.add(LightSector(
                  startDegrees = startDegrees,
                  endDegrees = end,
                  range = sectorRange,
                  color = uiColor,
                  text = color,
                  obscured = obscured || fullLightObscured, characteristicNumber)
               )
            } else {
               if (end < previousEnd) {
                  end += 360.0
               }
               sectors.add(LightSector(
                  startDegrees = previousEnd,
                  endDegrees = end,
                  range = sectorRange,
                  color = uiColor,
                  text = color,
                  obscured = obscured || fullLightObscured, characteristicNumber)
               )
            }

            if (fullLightObscured) {
               // add the sector for the part of the light which is not obscured
               sectors.add(
                  LightSector(
                     startDegrees = end,
                     endDegrees = (start ?: 0.0) + 360.0,
                     range = sectorRange,
                     color = visibleColor ?: (lightColors.getOrNull(0) ?: Color.Transparent),
                     characteristicNumber = characteristicNumber
                  )
               )
            }

            previousEnd = end
         }
      }

      sectors
   }

   @kotlinx.serialization.Transient
   @Transient
   val expandedCharacteristic = {
      var expanded = characteristic
      expanded = expanded?.replace("Al.", "Alternating ")
      expanded = expanded?.replace("lt.","Lit ")
      expanded = expanded?.replace("bl.","Blast ")
      expanded = expanded?.replace("Mo.","Morse code ")
      expanded = expanded?.replace("Bu.","Blue ")
      expanded = expanded?.replace("min.","Minute ")
      expanded = expanded?.replace("Dir.","Directional ")
      expanded = expanded?.replace("obsc.","Obscured ")
      expanded = expanded?.replace("ec.","Eclipsed ")
      expanded = expanded?.replace("Oc.","Occulting ")
      expanded = expanded?.replace("ev.","Every ")
      expanded = expanded?.replace("Or.","Orange ")
      expanded = expanded?.replace("F.","Fixed ")
      expanded = expanded?.replace("Q.","Quick Flashing ")
      expanded = expanded?.replace("L.Fl.","Long Flashing ")
      expanded = expanded?.replace("Fl.","Flashing ")
      expanded = expanded?.replace("R.","Red ")
      expanded = expanded?.replace("fl.","Flash ")
      expanded = expanded?.replace("s.","Seconds ")
      expanded = expanded?.replace("G.","Green ")
      expanded = expanded?.replace("si.","Silent ")
      expanded = expanded?.replace("horiz.","Horizontal ")
      expanded = expanded?.replace("U.Q.","Ultra Quick ")
      expanded = expanded?.replace("flashing intes.","Intensified ")
      expanded = expanded?.replace("I.Q.","Interrupted Quick ")
      expanded = expanded?.replace("flashing unintens.","Unintensified ")
      expanded = expanded?.replace("vert.","Vertical ")
      expanded = expanded?.replace("Iso.","Isophase ")
      expanded = expanded?.replace("Vi.", "Violet ")
      expanded = expanded?.replace("I.V.Q.","Interrupted Very Quick Flashing ")
      expanded = expanded?.replace("vis.","Visible ")
      expanded = expanded?.replace("V.Q.","Very Quick ")
      expanded = expanded?.replace("Km.","Kilometer ")
      expanded = expanded?.replace("W.","White ")
      expanded = expanded?.replace("Y.","Yellow ")

      expanded
   }

   fun compositeKey(): String {
      return compositeKey(volumeNumber, featureNumber, characteristicNumber)
   }

   override fun toString(): String {
      return "LIGHT\n\n" +
         "aidType: ${aidType.orEmpty()}\n" +
         "characteristic: ${characteristic.orEmpty()}\n" +
         "characteristicNumber: ${characteristicNumber}\n" +
         "deleteFlag: ${deleteFlag.orEmpty()}\n" +
         "featureNumber: ${featureNumber}\n" +
         "geopoliticalHeading: ${geopoliticalHeading.orEmpty()}\n" +
         "heightFeet: ${heightFeet?.toString().orEmpty()}\n" +
         "heightMeters: ${heightMeters?.toString().orEmpty()}\n" +
         "internationalFeature: ${internationalFeature.orEmpty()}\n" +
         "localHeading: ${localHeading.orEmpty()}\n" +
         "name: ${name.orEmpty()}\n" +
         "noticeNumber: ${noticeNumber?.toString().orEmpty()}\n" +
         "noticeWeek: ${noticeWeek}\n" +
         "noticeYear: ${noticeYear}\n" +
         "position: ${position.orEmpty()}\n" +
         "postNote: ${postNote.orEmpty()}\n" +
         "precedingNote: ${precedingNote.orEmpty()}\n" +
         "range: ${range.orEmpty()}\n" +
         "regionHeading: ${regionHeading.orEmpty()}\n" +
         "remarks: ${remarks.orEmpty()}\n" +
         "removeFromList: ${removeFromList.orEmpty()}\n" +
         "structure: ${structure.orEmpty()}\n" +
         "subregionHeading: ${subregionHeading.orEmpty()}\n" +
         "volumeNumber: $volumeNumber"
   }

   companion object {
      fun compositeKey(volumeNumber: String, featureNumber: String, characteristicNumber: Int): String {
         return "$volumeNumber--$featureNumber--${characteristicNumber}"
      }
   }
}