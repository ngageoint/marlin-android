package mil.nga.msi.datasource.dgpsstation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng

@Entity(
   tableName = "dgps_stations",
   primaryKeys = ["volume_number", "feature_number"],
   indices = [Index(value = ["id"], unique = true)]
)
data class DgpsStation(
   @ColumnInfo(name = "id")
   val id: String,

   @ColumnInfo(name = "volume_number")
   val volumeNumber: String,

   @ColumnInfo(name = "feature_number")
   val featureNumber: Float,

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

   @ColumnInfo(name = "station_id")
   var stationId: String? = null

   @ColumnInfo(name = "range")
   var range: Int? = null

   @ColumnInfo(name = "frequency")
   var frequency: Float? = null

   @ColumnInfo(name = "transfer_rate")
   var transferRate: Int? = null

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

   @Transient
   val latLng = LatLng(latitude, longitude)

   fun information() = mapOf(
      "Number" to featureNumber,
      "Name and Location" to name,
      "Geopolitical Heading" to geopoliticalHeading,
      "Position" to position.orEmpty(),
      "Range (nmi)" to range.toString(),
      "Frequency" to frequency,
      "Transfer Rate" to transferRate,
      "Notice Number" to noticeNumber,
      "Remarks" to remarks,
   )

   override fun toString(): String {
      return "DGPS\n\n" +
         "Aid Type: ${aidType.orEmpty()}\n" +
         "Delete Flag: ${deleteFlag.orEmpty()}\n" +
         "Feature Number: ${featureNumber}\n" +
         "Geopolitical Heading: ${geopoliticalHeading.orEmpty()}\n" +
         "Region Heading: ${regionHeading.orEmpty()}\n" +
         "Name: ${name.orEmpty()}\n" +
         "Station Id: ${stationId.toString()}\n" +
         "Transfer Rate: ${transferRate.toString()}\n" +
         "Notice Number: ${noticeNumber?.toString().orEmpty()}\n" +
         "Notice Week: ${noticeWeek}\n" +
         "Notice Year: ${noticeYear}\n" +
         "Position: ${position.orEmpty()}\n" +
         "Post Note: ${postNote.orEmpty()}\n" +
         "Preceding Note: ${precedingNote.orEmpty()}\n" +
         "Frequency: ${frequency.toString()}\n" +
         "Station Remark: ${remarks.orEmpty()}\n" +
         "Remove From List: ${removeFromList.orEmpty()}\n" +
         "Volume Number: ${volumeNumber}"
   }

   fun compositeKey(): String {
      return compositeKey(volumeNumber, featureNumber)
   }

   companion object {
      fun compositeKey(volumeNumber: String, featureNumber: Float): String {
         return "$volumeNumber--$featureNumber"
      }
   }
}