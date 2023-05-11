package mil.nga.msi.ui.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.sf.Geometry
import mil.nga.sf.wkt.GeometryReader

data class GeometryState(
   val geometry: Geometry,
   val distance: Double? = null
)

data class NavigationalWarningState(
   val warning: NavigationalWarning,
   val location: List<GeometryState>? = null
) {
   companion object {
      fun fromWarning(warning: NavigationalWarning): NavigationalWarningState {
         val geometries = warning.position?.locations?.map { location ->
            val geometry = GeometryReader.readGeometry(location.wkt)
            GeometryState(geometry, location.distance)
         }

         return NavigationalWarningState(warning, geometries)
      }
   }
}