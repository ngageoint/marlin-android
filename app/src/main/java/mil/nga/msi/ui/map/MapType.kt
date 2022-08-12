package mil.nga.msi.ui.map

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.compose.MapType

@Immutable
enum class BaseMapType(val title: String, val value: Int) {
   NORMAL("Standard", GoogleMap.MAP_TYPE_NORMAL),
   SATELLITE("Satellite", GoogleMap.MAP_TYPE_SATELLITE),
   HYBRID("Hybrid", GoogleMap.MAP_TYPE_HYBRID),
   TERRAIN("Terrain", GoogleMap.MAP_TYPE_TERRAIN),
   OSM("Open Street Map", GoogleMap.MAP_TYPE_NONE);

   fun asMapType(): MapType {
      return when(this) {
         NORMAL -> MapType.NORMAL
         SATELLITE -> MapType.SATELLITE
         HYBRID -> MapType.HYBRID
         TERRAIN -> MapType.TERRAIN
         OSM -> MapType.NONE
      }
   }

   companion object {
      fun fromValue(value: Int?): BaseMapType {
         return values().find { it.value == value } ?: BaseMapType.NORMAL
      }
   }
}