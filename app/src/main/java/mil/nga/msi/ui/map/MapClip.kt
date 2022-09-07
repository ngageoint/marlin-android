package mil.nga.msi.ui.map

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.*

@Composable
fun MapClip(
   latLng: LatLng,
   baseMap: BaseMapType?,
   tileProvider: TileProvider? = null
) {
   val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(latLng, 16f)
   }
   val uiSettings = MapUiSettings(
      zoomControlsEnabled = false,
      zoomGesturesEnabled = false,
      compassEnabled = false
   )

   val properties = baseMap?.let {
      MapProperties(mapType = it.asMapType())
   } ?: MapProperties()

   GoogleMap(
      cameraPositionState = cameraPositionState,
      properties = properties,
      uiSettings = uiSettings,
      modifier = Modifier
         .fillMaxWidth()
         .height(200.dp)
   ) {
      tileProvider?.let { TileOverlay(tileProvider = it, zIndex = -1f) }
   }
}