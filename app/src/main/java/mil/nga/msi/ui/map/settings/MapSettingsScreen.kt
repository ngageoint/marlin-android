package mil.nga.msi.ui.map.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType

@Composable
fun MapSettingsScreen(
   onLayers: () -> Unit,
   onLightSettings: () -> Unit,
   onClose: () -> Unit,
   viewModel: MapSettingsViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()

   val baseMap by viewModel.baseMap.observeAsState()
   val gars by viewModel.gars.observeAsState(false)
   val mgrs by viewModel.mgrs.observeAsState(false)
   val showLocation by viewModel.showLocation.observeAsState(false)

   Column {
      TopBar(
         title = "Map Settings",
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      Surface {
         Column(
            Modifier
               .fillMaxSize()
               .verticalScroll(scrollState)
               .padding(bottom = 16.dp)
         ) {
            baseMap?.let { baseMap ->
               BaseMapLayer(baseMap) {
                  viewModel.setBaseLayer(it)
               }
            }

            GridLayers(
               gars = gars,
               mgrs = mgrs,
               onGarsToggled = { viewModel.setGARS(it) },
               onMgrsToggled = { viewModel.setMGRS(it) }
            )

            DataSourceSettings(onLightSettings)

            DisplaySettings(
               showLocation = showLocation,
               onShowLocationToggled = { viewModel.setShowLocation(it) }
            )
         }

         GridLayers(
            gars = gars,
            mgrs = mgrs,
            onGarsToggled = { viewModel.setGARS(it) },
            onMgrsToggled = { viewModel.setMGRS(it) }
         )

         Layers() { onLayers() }

         DataSourceSettings(onLightSettings)

         DisplaySettings(
            showLocation = showLocation,
            onShowLocationToggled = { viewModel.setShowLocation(it) }
         )
      }
   }
}

@Composable
private fun BaseMapLayer(
   baseLayer: BaseMapType,
   onLayerSelected: (BaseMapType) -> Unit
) {
   var openDialog by remember { mutableStateOf(false)  }

   Column(Modifier.padding(bottom = 16.dp)) {
      Text(
         text = "Map",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 32.dp, end = 32.dp)
      )

      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { openDialog = true }
            .padding(vertical = 8.dp, horizontal = 32.dp)
      ) {
         Column {
            Text(
               text = "Base Map",
               style = MaterialTheme.typography.bodyLarge
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = baseLayer.title,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }
      }

      if (openDialog) {
         MapLayerDialog(baseLayer) {
            onLayerSelected(it)
            openDialog = false
         }
      }
   }
}

@Composable
fun MapLayerDialog(
   baseLayer: BaseMapType,
   onLayerSelected: (BaseMapType) -> Unit
) {
   Dialog(onDismissRequest = { onLayerSelected(baseLayer) }) {
      Surface(
         shape = RoundedCornerShape(4.dp)
      ) {
         Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
               Text(
                  text = "Base Map",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier
                     .height(64.dp)
                     .wrapContentHeight(align = Alignment.CenterVertically)
               )
            }

            BaseMapType.values().forEach { mapType ->
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  RadioButton(
                     selected = mapType == baseLayer,
                     onClick = {
                        onLayerSelected(mapType)
                     }
                  )
                  Text(
                     text = mapType.title,
                     modifier = Modifier.fillMaxWidth()
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun GridLayers(
   gars: Boolean,
   mgrs: Boolean,
   onGarsToggled: (Boolean) -> Unit,
   onMgrsToggled: (Boolean) -> Unit
) {
   Column(Modifier.padding(bottom = 16.dp)) {
      Text(
         text = "Grids",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
      )

      Row(
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { onGarsToggled(!gars) }
            .padding(vertical = 8.dp, horizontal = 32.dp)
      ) {
         Column {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
               Text(
                  text = "GARS",
                  style = MaterialTheme.typography.bodyLarge
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "Global Area Reference System",
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }

         Switch(
            checked = gars,
            onCheckedChange = null
         )
      }

      Row(
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { onMgrsToggled(!mgrs) }
            .padding(vertical = 16.dp, horizontal = 32.dp)
      ) {
         Column {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
               Text(
                  text = "MGRS",
                  style = MaterialTheme.typography.bodyLarge
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "Military Grid Reference System",
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }

         Switch(
            checked = mgrs,
            onCheckedChange = null
         )
      }
   }
}

@Composable
private fun Layers(
   onTap: () -> Unit
) {
   Column(
      Modifier.padding(bottom = 16.dp)
   ) {
      Text(
         text = "Data Source",
         color = MaterialTheme.colorScheme.primary,
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
      )

      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(vertical = 16.dp, horizontal = 32.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(Icons.Default.Layers,
               modifier = Modifier.padding(end = 16.dp),
               contentDescription = "Additional Map Layers"
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = "Additional Map Layers",
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}

@Composable
private fun DataSourceSettings(
   onLightSettings: () -> Unit
) {
   LightSettings(
      onSettings = onLightSettings
   )
}

@Composable
private fun LightSettings(
   onSettings: () -> Unit
) {
   Column(
      Modifier.padding(bottom = 16.dp)
   ) {
      Text(
         text = "Data Source",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
      )

      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .clickable { onSettings() }
            .padding(vertical = 16.dp, horizontal = 32.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               painter = painterResource(id = R.drawable.ic_baseline_lightbulb_24),
               modifier = Modifier.padding(end = 16.dp),
               contentDescription = "Light Icon"
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = "Light Settings",
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}

@Composable
private fun DisplaySettings(
   showLocation: Boolean,
   onShowLocationToggled: (Boolean) -> Unit,
) {
   val hasLocationPermission =
      ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
      ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

   if (hasLocationPermission) {
      Column(
         Modifier.padding(bottom = 16.dp)
      ) {
         Text(
            text = "Display",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)
         )

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
               .clickable { onShowLocationToggled(!showLocation) }
               .padding(vertical = 8.dp, horizontal = 32.dp)
         ) {
            Column {
               Text(
                  text = "Show Current Location",
                  style = MaterialTheme.typography.bodyLarge
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Display your current position on the map",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }
            }

            Switch(
               checked = showLocation,
               onCheckedChange = null
            )
         }
      }
   }
}