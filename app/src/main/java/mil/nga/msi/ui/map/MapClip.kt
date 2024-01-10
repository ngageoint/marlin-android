package mil.nga.msi.ui.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import mil.nga.msi.R

@Composable
fun MapClip(
   latLng: LatLng,
   tileProvider: TileProvider? = null,
) {
   MapClip(
      latLngBounds = LatLngBounds.builder().include(latLng).build(),
      tileProvider = tileProvider
   )
}

@Composable
fun MapClip(
   latLngBounds: LatLngBounds,
   tileProvider: TileProvider? = null,
   viewModel: MapViewModel = hiltViewModel()
) {
   val cameraPositionState = rememberCameraPositionState()

   LaunchedEffect(latLngBounds) {
      cameraPositionState.move(
         update = CameraUpdateFactory.newLatLngBounds(latLngBounds, 20)
      )
   }

   val baseMap by viewModel.baseMap.observeAsState()
   val layers by viewModel.layers.observeAsState()
   val tileProviders by viewModel.tileProviders.observeAsState()
   val mgrsTileProvider = tileProviders?.get(TileProviderType.MGRS)
   val garsTileProvider = tileProviders?.get(TileProviderType.GARS)
   val osmTileProvider = tileProviders?.get(TileProviderType.OSM)

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
      layers?.forEach { TileOverlay(tileProvider = it) }
      osmTileProvider?.let { TileOverlay(tileProvider = it) }
      mgrsTileProvider?.let { TileOverlay(tileProvider = it) }
      garsTileProvider?.let { TileOverlay(tileProvider = it) }
      tileProvider?.let { TileOverlay(tileProvider = it) }
   }
}