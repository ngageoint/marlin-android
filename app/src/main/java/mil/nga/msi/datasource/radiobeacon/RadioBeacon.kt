package mil.nga.msi.datasource.radiobeacon

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.LightSector

@Entity(
   tableName = "radio_beacons",
   primaryKeys = ["volume_number", "feature_number"]
)
data class RadioBeacon(
   @ColumnInfo(name = "id")
   val id: String,

   @ColumnInfo(name = "volume_number")
   val volumeNumber: String,

   @ColumnInfo(name = "feature_number")
   val featureNumber: String,

   @ColumnInfo(name = "notice_week")
   var noticeWeek: String,

   @ColumnInfo(name = "notice_year")
   var noticeYear: String,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "aid_type")
   var aidType: String? = null

   @ColumnInfo(name = "geopolitical_heading")
   var geopoliticalHeading: String? = null

   @ColumnInfo(name = "region_heading")
   var regionHeading: String? = null

   @ColumnInfo(name = "preceding_note")
   var precedingNote: String? = null

   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "position")
   var position: String? = null

   @ColumnInfo(name = "characteristic")
   var characteristic: String? = null

   @ColumnInfo(name = "range")
   var range: String? = null

   @ColumnInfo(name = "sequence_text")
   var sequenceText: String? = null

   @ColumnInfo(name = "frequency")
   var frequency: String? = null

   @ColumnInfo(name = "station_remark")
   var stationRemark: String? = null

   @ColumnInfo(name = "post_note")
   var postNote: String? = null

   @ColumnInfo(name = "notice_number")
   var noticeNumber: String? = null

   @ColumnInfo(name = "remove_from_list")
   var removeFromList: String? = null

   @ColumnInfo(name = "delete_flag")
   var deleteFlag: String? = null

   @ColumnInfo(name = "section_header")
   var sectionHeader: String = ""

   @Transient
   val latLng = LatLng(latitude, longitude)

   private fun expandedCharacteristic(): String? {
      var expanded = characteristic
      expanded = expanded?.replace("aero", "aeronautical")
      expanded = expanded?.replace( "si", "silence")
      expanded = expanded?.replace("tr", "transmission")
      return expanded
   }

   fun expandedCharacteristicWithoutCode(): String? {
      return characteristic?.substringAfter(").\n")
         .takeIf { it?.isNotEmpty() == true }
         ?.replace("aero", "aeronautical")
         ?.replace( "si", "silence")
         ?.replace("tr", "transmission")
   }

   fun morseCode(): String? {
      return characteristic?.substringAfter("(")?.substringBefore(")") ?: return null
   }

   fun morseLetter(): String {
      return characteristic?.substringBefore("\n").orEmpty()
   }

   fun information() = mapOf(
      "Number" to featureNumber,
      "Name and Location" to name,
      "Geopolitical Heading" to geopoliticalHeading,
      "Position" to position.orEmpty(),
      "Characteristic" to expandedCharacteristic(),
      "Range (nmi)" to range.toString(),
      "Sequence" to sequenceText,
      "Frequency" to frequency,
      "Remarks" to stationRemark,
   )

   fun azimuthCoverage(): List<LightSector> {
      val sectors = mutableListOf<LightSector>()

      val regex = Regex("(?<azimuth>(Azimuth coverage)?).?((?<startdeg>(\\d*))\\^)?((?<startminutes>[0-9]*)[\\`'])?(-(?<enddeg>(\\d*))\\^)?(?<endminutes>[0-9]*)[\\`']?\\.")
      stationRemark?.let { remark ->
         var previousEnd = 0.0

         regex.findAll(remark).forEach {  matchResult ->
            val groups =  matchResult.groups as? MatchNamedGroupCollection

            if (groups != null) {
               var end = 0.0
               var start: Double? = null

               arrayOf("startdeg", "startminutes", "enddeg", "endminutes").forEach { name ->
                  val component = groups[name]
                  if (component != null) {
                     if (name == "startdeg") {
                        start = (start ?: 0.0) + (remark.substring(component.range).toDoubleOrNull() ?: 0.0) - 90
                     } else if (name == "startminutes") {
                        start = (start ?: 0.0) + ((remark.substring(component.range).toDoubleOrNull() ?: 0.0)  / 60)
                     } else if (name == "enddeg") {
                        end = (remark.substring(component.range).toDoubleOrNull() ?: 0.0) - 90
                     } else if (name == "endminutes") {
                        end += (remark.substring(component.range).toDoubleOrNull() ?: 0.0) / 60
                     }
                  }
               }

               val startDegrees = start
               if (startDegrees != null) {
                  sectors.add(LightSector(
                     startDegrees = startDegrees,
                     endDegrees = end,
                     color = DataSource.RADIO_BEACON.color)
                  )
               } else {
                  if (end < previousEnd) {
                     end += 360.0
                  }
                  sectors.add(LightSector(
                     startDegrees = previousEnd,
                     endDegrees = end,
                     color = DataSource.RADIO_BEACON.color)
                  )
               }

               previousEnd = end
            }
         }
      } ?: run {
         sectors.add(
            LightSector(
               startDegrees = 0.0,
               endDegrees = 0.0,
               color = DataSource.RADIO_BEACON.color
            )
         )
      }

      return sectors
   }

   fun compositeKey(): String {
      return compositeKey(volumeNumber, featureNumber)
   }

   override fun toString(): String {
      return "Radio Beacon\n\n" +
         "Aid Type: ${aidType.orEmpty()}\n" +
         "Characteristic: ${characteristic.orEmpty()}\n" +
         "Delete Flag: ${deleteFlag.orEmpty()}\n" +
         "Feature Number: ${featureNumber}\n" +
         "Geopolitical Heading: ${geopoliticalHeading.orEmpty()}\n" +
         "Region Heading: ${regionHeading.orEmpty()}\n" +
         "Name: ${name.orEmpty()}\n" +
         "Notice Number: ${noticeNumber.orEmpty()}\n" +
         "Notice Week: ${noticeWeek}\n" +
         "Notice Year: ${noticeYear}\n" +
         "Position: ${position.orEmpty()}\n" +
         "Post Note: ${postNote.orEmpty()}\n" +
         "Preceding Note: ${precedingNote.orEmpty()}\n" +
         "Range: ${range.orEmpty()}\n" +
         "Sequence Text: ${sequenceText.orEmpty()}\n" +
         "Frequency: ${frequency.orEmpty()}\n" +
         "Station Remark: ${stationRemark.orEmpty()}\n" +
         "Remove From List: ${removeFromList.orEmpty()}\n" +
         "Volume Number: $volumeNumber"
   }

   companion object {
      fun compositeKey(volumeNumber: String, featureNumber: String): String {
         return "$volumeNumber--$featureNumber"
      }
   }
}