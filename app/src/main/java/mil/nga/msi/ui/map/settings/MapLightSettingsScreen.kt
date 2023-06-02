package mil.nga.msi.ui.map.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.outlined.Stream
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import mil.nga.msi.R
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType

@Composable
fun MapLightSettingsScreen(
   onClose: () -> Unit,
   viewModel: MapLightSettingsViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val showLightRanges by viewModel.showLightRanges.observeAsState(false)
   val showSectorLightRanges by viewModel.showSectorLightRanges.observeAsState(false)
   val lightTileProvider by viewModel.lightTileProvider.observeAsState()

   Column {
      TopBar(
         title = "Light Settings",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { onClose() }
      )

      Map(
         baseMap = baseMap,
         center = viewModel.center,
         tileProvider = lightTileProvider
      )

      Surface {
         Options(
            lightRange = showLightRanges,
            sectorLightRange = showSectorLightRanges,
            onLightRangeToggle = {
               viewModel.setShowLightRanges(it)
            },
            onSectorLightRangeToggle = {
               viewModel.setShowSectorLightRanges(it)
            }
         )
      }
   }
}

@Composable
private fun Map(
   baseMap: BaseMapType?,
   center: LatLng,
   tileProvider: TileProvider?
) {
   val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(center, 9.5f)
   }

   val uiSettings = MapUiSettings(
      zoomControlsEnabled = false,
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
         .height(300.dp)
   ) {
      tileProvider?.let { TileOverlay(it) }
   }
}

@Composable
private fun Options(
   lightRange: Boolean,
   sectorLightRange: Boolean,
   onLightRangeToggle: (Boolean) -> Unit,
   onSectorLightRangeToggle: (Boolean) -> Unit,
) {
   Column(
      Modifier
         .fillMaxSize()
         .background(MaterialTheme.colorScheme.surfaceVariant)
   ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "MAP OPTIONS",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
         )
      }

      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onSectorLightRangeToggle(!sectorLightRange) }
            .padding(horizontal = 32.dp, vertical = 16.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               Icons.Outlined.Stream,
               modifier = Modifier.padding(end = 8.dp),
               contentDescription = "Light Sector Icon"
            )
         }

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
         ) {
            Column {
               Text(
                  text = "Show Sector Light Ranges",
                  style = MaterialTheme.typography.bodyLarge
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Lights with defined sectors",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }
            }

            Switch(
               checked = sectorLightRange,
               onCheckedChange = null
            )
         }
      }

      Divider(
         modifier = Modifier
            .padding(start = 64.dp)
            .background(MaterialTheme.colorScheme.background)
      )

      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onLightRangeToggle(!lightRange) }
            .padding(horizontal = 32.dp, vertical = 16.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               Icons.Filled.ModeStandby,
               modifier = Modifier.padding(end = 8.dp),
               contentDescription = "Light Range Icon"
            )
         }

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
         ) {
            Column(Modifier.weight(1f)) {
               Text(
                  text = "Show Light Ranges",
                  style = MaterialTheme.typography.bodyLarge
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Lights showing an unbroken light over an arc of the horizon of 360 degrees",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }
            }

            Switch(
               checked = lightRange,
               onCheckedChange = null
            )
         }
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "A lights range is the distance, expressed in nautical miles, that a light can be seen in clear water. These ranges can be visualized on the map. Lights which have defined color sectors, or have visibility or obscured ranges are drawn as arcs of visibility.  All other lights are drawn as full circles.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
               .padding(horizontal = 16.dp, vertical = 16.dp)
         )
      }
   }
}