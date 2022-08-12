package mil.nga.msi.datasource.light

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

data class LightListItem(
   @ColumnInfo(name = "feature_number") val featureNumber: String,
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "characteristic_number") val characteristicNumber: Int,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
) {
   @ColumnInfo(name = "international_feature")
   var internationalFeature: String? = null

   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "structure")
   var structure: String? = null

   @ColumnInfo(name = "section_header") var sectionHeader: String = ""

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}