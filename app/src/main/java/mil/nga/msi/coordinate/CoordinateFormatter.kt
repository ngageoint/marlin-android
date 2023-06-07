package mil.nga.msi.coordinate

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mil.nga.gars.GARS
import mil.nga.mgrs.MGRS
import mil.nga.msi.repository.preferences.MapRepository
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton

class CoordinateFormatter(
   private val coordinateSystem: CoordinateSystem
) {
   private val latLngFormat = DecimalFormat("###.00000")

   fun format(latLng: LatLng): String {
      return when (coordinateSystem) {
         CoordinateSystem.WGS84 -> {
            latLngFormat.format(latLng.latitude) + ", " + latLngFormat.format(latLng.longitude)
         }
         CoordinateSystem.MGRS -> {
            val mgrs = MGRS.from(mil.nga.grid.features.Point.point(latLng.longitude, latLng.latitude))
            mgrs.coordinate()
         }
         CoordinateSystem.DMS -> {
            DMS.from(latLng).format()
         }
         CoordinateSystem.GARS -> {
            val gars = GARS.from(mil.nga.grid.features.Point.point(latLng.longitude, latLng.latitude))
            gars.coordinate()
         }
      }
   }
}