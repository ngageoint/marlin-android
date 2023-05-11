package mil.nga.msi.datasource

class LocationWithDistance(
   val wkt: String,
   val distance: Double? = null
)

data class Position(
   val locations: List<LocationWithDistance>
)