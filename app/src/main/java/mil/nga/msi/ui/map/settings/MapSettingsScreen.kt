package mil.nga.msi.ui.map.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.coordinate.CoordinateSystem
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.search.SearchProvider
import mil.nga.msi.ui.theme.onSurfaceDisabled

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
   val showScale by viewModel.showScale.observeAsState(false)
   val coordinateSystem by viewModel.coordinateSystem.observeAsState()
   val searchProvider by viewModel.searchProvider.observeAsState()

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

            Layers { onLayers() }

            DataSourceSettings(onLightSettings)

            searchProvider?.let { searchProvider ->
               SearchProvider(searchProvider) {
                  viewModel.setSearchProvider(it)
               }
            }

            DisplaySettings(
               showLocation = showLocation,
               showScale = showScale,
               coordinateSystem = coordinateSystem,
               onShowLocationToggled = { viewModel.setShowLocation(it) },
               onShowScaleToggled = { viewModel.setShowScale(it) },
               onCoordinateSystemToggle = { viewModel.setCoordinateSystem(it) }
            )
         }
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

            BaseMapType.entries.forEach { mapType ->
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
   Column {
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
private fun SearchProvider(
   searchProvider: SearchProvider,
   onSearchProviderSelected: (SearchProvider) -> Unit
) {
   var openDialog by remember { mutableStateOf(false)  }

   Column(Modifier.padding(bottom = 16.dp)) {
      Text(
         text = "Search",
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
               text = "Search Provider",
               style = MaterialTheme.typography.bodyLarge
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = searchProvider.title,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }
      }

      if (openDialog) {
         SearchProviderDialog(searchProvider) {
            onSearchProviderSelected(it)
            openDialog = false
         }
      }
   }
}

@Composable
private fun SearchProviderDialog(
   searchProvider: SearchProvider,
   onSearchProviderSelected: (SearchProvider) -> Unit
) {
   val context = LocalContext.current
   val attribution = buildAnnotatedString {
      append("Nominatim data is provided by ")
      pushStringAnnotation(tag = "osmLink", annotation = "https://www.openstreetmap.org/copyright/")
      withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
         append("OpenStreetMap")
      }
      pop()
      append(".")
   }
   Dialog(onDismissRequest = { onSearchProviderSelected(searchProvider) }) {
      Surface(
         shape = RoundedCornerShape(4.dp)
      ) {
         Column(Modifier
            .padding(16.dp)
            .fillMaxWidth()
         ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
               Text(
                  text = "Search Provider",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.padding(bottom = 4.dp)
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
               Text(
                  text = "Provider used when searching for places and/or addresses.",
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
               )
            }

            SearchProvider.entries.forEach { provider ->
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxWidth()
               ) {
                  RadioButton(
                     selected = provider == searchProvider,
                     onClick = {
                        onSearchProviderSelected(provider)
                     }
                  )
                  Text(
                     text = provider.title,
                     modifier = Modifier.fillMaxWidth()
                  )
               }
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
               ClickableText(
                  text = attribution,
                  style = MaterialTheme.typography.bodySmall.copy(color = LocalContentColor.current),
                  onClick = { clickedOffset ->
                     attribution.getStringAnnotations(tag = "osmLink", start = clickedOffset, end = clickedOffset)
                        .firstOrNull()?.let {
                           val intent = Intent(Intent.ACTION_VIEW)
                           intent.setData(it.item.toUri())
                           context.startActivity(intent)
                        }
                  },
                  modifier = Modifier.padding(top = 16.dp)
               )
            }
         }
      }
   }
}

@Composable
private fun DisplaySettings(
   showLocation: Boolean,
   showScale: Boolean,
   coordinateSystem: CoordinateSystem?,
   onShowLocationToggled: (Boolean) -> Unit,
   onShowScaleToggled: (Boolean) -> Unit,
   onCoordinateSystemToggle: (CoordinateSystem) -> Unit
) {
   var openCoordinateSystemDialog by remember { mutableStateOf(false) }

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
               .padding(vertical = 16.dp, horizontal = 32.dp)
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

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
               .clickable { onShowScaleToggled(!showScale) }
               .padding(vertical = 16.dp, horizontal = 32.dp)
         ) {
            Column(
               Modifier.weight(1f)
            ) {
               Text(
                  text = "Show Map Scale",
                  style = MaterialTheme.typography.bodyLarge
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Show a scale bar indicating the current map scale",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }
            }

            Switch(
               checked = showScale,
               onCheckedChange = null
            )
         }

         Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
               .clickable { openCoordinateSystemDialog = true }
               .padding(vertical = 16.dp, horizontal = 32.dp)
         ) {
            Column(
               Modifier.weight(1f)
            ) {
               Text(
                  text = "Coordinate System",
                  style = MaterialTheme.typography.bodyLarge
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = coordinateSystem?.title ?: "",
                     style = MaterialTheme.typography.bodyMedium
                  )
               }
            }
         }
      }
   }

   if (openCoordinateSystemDialog) {
      CoordinateSystemDialog(
         coordinateSystem = coordinateSystem,
         onCoordinateSystemSelected = {
            onCoordinateSystemToggle(it)
            openCoordinateSystemDialog = false
         }
      )
   }
}

@Composable
private fun CoordinateSystemDialog(
   coordinateSystem: CoordinateSystem?,
   onCoordinateSystemSelected: (CoordinateSystem) -> Unit
) {
   Dialog(
      onDismissRequest = {
         coordinateSystem?.let { onCoordinateSystemSelected(it) }
      }
   ) {
      Surface(
         shape = RoundedCornerShape(4.dp)
      ) {
         Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
               Text(
                  text = "Coordinate System",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier
                     .height(64.dp)
                     .wrapContentHeight(align = Alignment.CenterVertically)
               )
            }

            CoordinateSystem.entries.forEach { row ->
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxWidth()
                     .clickable { onCoordinateSystemSelected(row) }
               ) {
                  RadioButton(
                     selected = row == coordinateSystem,
                     onClick = { onCoordinateSystemSelected(row) }
                  )
                  Text(
                     text = row.title,
                     modifier = Modifier.fillMaxWidth()
                  )
               }
            }
         }
      }
   }
}