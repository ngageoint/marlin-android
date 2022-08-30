package mil.nga.msi.datasource.radiobeacon

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

data class RadioBeaconListItem(
   @ColumnInfo(name = "feature_number") val featureNumber: String,
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "characteristic") val characteristic: String?,
   @ColumnInfo(name = "station_remark") val stationRemark: String?
) {
   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "section_header") var sectionHeader: String = ""

   fun morseCode(): String? {
      return characteristic?.substringAfter("(")?.substringBefore(")") ?: return null
   }

   fun morseLetter(): String {
      return characteristic?.substringBefore("\n").orEmpty()
   }

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))


   fun expandedCharacteristicWithoutCode(): String? {
      return characteristic?.substringAfter(").\n")
         .takeIf { it?.isNotEmpty() == true }
         ?.replace("aero", "aeronautical")
         ?.replace( "si", "silence")
         ?.replace("tr", "transmission")
   }

   fun expandedCharacteristic(): String? {
      return characteristic?.replace("aero", "aeronautical")
         ?.replace( "si", "silence")
         ?.replace("tr", "transmission")
   }
}