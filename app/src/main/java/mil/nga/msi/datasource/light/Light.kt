package mil.nga.msi.datasource.light

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

@Entity(
   tableName = "lights",
   primaryKeys = ["volume_number", "feature_number", "characteristic_number"]
)
data class Light(
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
   var sectionHeader: String? = null

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}