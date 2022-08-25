package mil.nga.msi.datasource.radiobeacon

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

@Entity(
   tableName = "radio_beacons",
   primaryKeys = ["volume_number", "feature_number"]
)
data class RadioBeacon(
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

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))

   override fun toString(): String {
      return "LIGHT\n\n" +
         "aidType: ${aidType.orEmpty()}\n" +
         "characteristic: ${characteristic.orEmpty()}\n" +
         "deleteFlag: ${deleteFlag.orEmpty()}\n" +
         "featureNumber: ${featureNumber}\n" +
         "geopoliticalHeading: ${geopoliticalHeading.orEmpty()}\n" +
         "regionHeading: ${regionHeading.orEmpty()}\n" +
         "name: ${name.orEmpty()}\n" +
         "noticeNumber: ${noticeNumber?.toString().orEmpty()}\n" +
         "noticeWeek: ${noticeWeek}\n" +
         "noticeYear: ${noticeYear}\n" +
         "position: ${position.orEmpty()}\n" +
         "postNote: ${postNote.orEmpty()}\n" +
         "precedingNote: ${precedingNote.orEmpty()}\n" +
         "range: ${range.orEmpty()}\n" +
         "sequenceText: ${sequenceText.orEmpty()}\n" +
         "frequency: ${frequency.orEmpty()}\n" +
         "stationRemark: ${stationRemark.orEmpty()}\n" +
         "removeFromList: ${removeFromList.orEmpty()}\n" +
         "volumeNumber: ${volumeNumber.orEmpty()}"
   }
}