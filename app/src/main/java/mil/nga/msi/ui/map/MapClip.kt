package mil.nga.msi.ui.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.*
import mil.nga.msi.R

@Composable
fun MapClip(
   latLng: LatLng,
   baseMap: BaseMapType?,
   tileProvider: TileProvider? = null,
   viewModel: MapViewModel = hiltViewModel()
) {

   val layers by viewModel.layers.observeAsState()
   val tileProviders by viewModel.tileProviders.observeAsState()
   val mgrsTileProvider = tileProviders?.get(TileProviderType.MGRS)
   val garsTileProvider = tileProviders?.get(TileProviderType.GARS)
   val osmTileProvider = tileProviders?.get(TileProviderType.OSM)

   val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(latLng, 16f)
   }
   val uiSettings = MapUiSettings(
      zoomControlsEnabled = false,
      zoomGesturesEnabled = false,
      compassEnabled = false
   )

   val mapStyleOptions = if (isSystemInDarkTheme()) {
      MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.map_theme_night)
   } else null

   val properties = baseMap?.let {
      MapProperties(
         mapType = it.asMapType(),
         mapStyleOptions = mapStyleOptions
      )
   } ?: MapProperties()


   GoogleMap(
      cameraPositionState = cameraPositionState,
      properties = properties,
      uiSettings = uiSettings,
      modifier = Modifier
         .fillMaxWidth()
         .height(200.dp)
   ) {
      osmTileProvider?.let { TileOverlay(tileProvider = it) }

      layers?.forEach { TileOverlay(tileProvider = it) }

      mgrsTileProvider?.let { TileOverlay(tileProvider = it) }
      garsTileProvider?.let { TileOverlay(tileProvider = it) }

      tileProvider?.let { TileOverlay(tileProvider = it) }
   }
}