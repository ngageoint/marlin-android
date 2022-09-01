package mil.nga.msi.datasource.dgpsstation

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

data class DgpsStationListItem(
   @ColumnInfo(name = "feature_number") val featureNumber: Int,
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "station_remark") val stationRemark: String?
) {
   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "section_header") var sectionHeader: String = ""

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}