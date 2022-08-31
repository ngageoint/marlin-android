package mil.nga.msi.ui.map.cluster

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.R
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

@Serializable
@Parcelize
data class MapAnnotation(
   val key: Key,
   val latitude: Double,
   val longitude: Double
) : Parcelable, ClusterItem {
   enum class Type constructor(val route: Route, val icon : Int? = null) {
      ASAM(AsamRoute.Main, R.drawable.asam_map_marker_24dp),
      MODU(ModuRoute.Main, R.drawable.modu_map_marker_24dp),
      LIGHT(LightRoute.Main),
      PORT(PortRoute.Main),
      RADIO_BEACON(RadioBeaconRoute.Main),
   }

   @Serializable
   @Parcelize
   data class Key(
      val id: String,
      val type: Type
   ) : Parcelable

   override fun getPosition() = LatLng(latitude, longitude)
   override fun getTitle(): String? = null
   override fun getSnippet(): String? = null

   companion object {
      fun fromAsam(asam: AsamMapItem): MapAnnotation {
         return MapAnnotation(Key(asam.reference, Type.ASAM), asam.latitude, asam.longitude)
      }
      fun fromModu(modu: ModuMapItem): MapAnnotation {
         return MapAnnotation(Key(modu.name, Type.MODU), modu.latitude, modu.longitude)
      }

      fun fromLight(light: Light): MapAnnotation {
         return MapAnnotation(Key(LightKey.fromLight(light).id(), Type.LIGHT), light.latitude, light.longitude)
      }

      val idComparator = Comparator<MapAnnotation> { a, b ->
         if (a.key == b.key) 0 else "${a.key.type}${a.key.id}".compareTo("${b.key.type}${b.key.id}")
      }
   }
}