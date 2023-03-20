package mil.nga.msi.ui.map.settings.layers.grid

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GridTileProvider
import mil.nga.msi.ui.map.settings.layers.MapLayerRoute

@Composable
fun MapGridLayerScreen(
   layer: Layer,
   onClose: () -> Unit,
   viewModel: MapGridLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var name by remember { mutableStateOf("") }
   var minZoom by remember { mutableStateOf<Int?>(null) }
   var maxZoom by remember { mutableStateOf<Int?>(null) }

   Column {
      TopBar(
         title = MapLayerRoute.CreateGridLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      MapGridLayerScreen(
         type = layer.type,
         url = layer.url,
         name = name,
         onNameChanged = { name = it },
         minZoom = minZoom,
         onMinZoomChanged = { minZoom = it },
         maxZoom = maxZoom,
         onMaxZoomChanged =  {maxZoom = it },
         onSave = {
            scope.launch {
               viewModel.createLayer(
                  name = name,
                  type = layer.type,
                  url = layer.url,
                  minZoom = minZoom,
                  maxZoom = maxZoom
               )
               onClose()
            }
         }
      )
   }
}

@Composable
fun MapGridLayerScreen(
   id: Long,
   onClose: () -> Unit,
   viewModel: MapGridLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var name by remember { mutableStateOf("") }
   var minZoom by remember { mutableStateOf<Int?>(null) }
   var maxZoom by remember { mutableStateOf<Int?>(null) }
   val layer by viewModel.layer.observeAsState()
   viewModel.setId(id)

   LaunchedEffect(layer) {
      layer?.let {
         name = it.name
         minZoom = it.minZoom ?: 0
         maxZoom = it.maxZoom ?: 25
      }
   }

   Column {
      TopBar(
         title = layer?.name ?: "",
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      layer?.let { layer ->
         MapGridLayerScreen(
            name = name,
            type = layer.type,
            url = layer.url,
            onNameChanged = { name = it },
            minZoom = minZoom,
            onMinZoomChanged = { minZoom = it },
            maxZoom = maxZoom,
            onMaxZoomChanged = { maxZoom = it },
            onSave = {
               scope.launch {
                  val update = Layer(
                     id = layer.id,
                     name = name,
                     type = layer.type,
                     url = layer.url,
                     minZoom = minZoom,
                     maxZoom = maxZoom
                  )
                  viewModel.updateLayer(update)
                  onClose()
               }
            }
         )
      }
   }
}

@Composable
private fun MapGridLayerScreen(
   type: LayerType,
   url: String,
   name: String,
   onNameChanged: (String) -> Unit,
   minZoom: Int?,
   onMinZoomChanged: (Int?) -> Unit,
   maxZoom: Int?,
   onMaxZoomChanged: (Int?) -> Unit,
   onSave: () -> Unit
) {
   Surface {
      Column(Modifier.fillMaxHeight()) {
         GridLayer(
            name = name,
            onNameChanged = onNameChanged,
            minZoom = minZoom,
            onMinZoomChanged = onMinZoomChanged,
            maxZoom = maxZoom,
            onMaxZoomChanged = onMaxZoomChanged
         )

         Map(
            url = Uri.parse(url),
            type = type,
            modifier = Modifier
               .fillMaxWidth()
               .weight(1f)
         )

         Button(
            enabled = name.isNotEmpty(),
            onClick = { onSave() },
            modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp)
         ) {
            Text(text = "Save Layer")
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GridLayer(
   name: String,
   onNameChanged: (String) -> Unit,
   minZoom: Int?,
   onMinZoomChanged: (Int?) -> Unit,
   maxZoom: Int?,
   onMaxZoomChanged: (Int?) -> Unit
) {
   val focusManager = LocalFocusManager.current

   Column(Modifier.padding(16.dp)) {
      TextField(
         value = name,
         label = { Text("Layer Name") },
         onValueChange = { onNameChanged(it) },
         modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
      )

      Text(
         text = "Zoom Level Constraints",
         style = MaterialTheme.typography.labelSmall,
         modifier = Modifier.padding(bottom = 4.dp)
      )

      Row(
         verticalAlignment = Alignment.Bottom,
         modifier = Modifier.fillMaxWidth()
      ) {
         TextField(
            value = minZoom?.toString() ?: "",
            onValueChange = { onMinZoomChanged(it.toIntOrNull()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.weight(1f)
         )

         Text(
            text = "to",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
         )

         TextField(
            value = maxZoom?.toString() ?: "",
            onValueChange = { onMaxZoomChanged(it.toIntOrNull()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.weight(1f)
         )
      }
   }
}

@Composable
private fun Map(
   url: Uri,
   type: LayerType,
   modifier: Modifier = Modifier
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "MAP",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(start = 16.dp, top = 0.dp, bottom = 16.dp)
      )
   }

   GoogleMap(
      properties = MapProperties(
         mapType = MapType.NORMAL
      ),
      uiSettings = MapUiSettings(
         compassEnabled = false
      ),
      modifier = modifier
   ) {
      tileOverlayOptions {
         TileOverlay(
            tileProvider = GridTileProvider(url, invertYAxis = type == LayerType.TMS)
         )
      }
   }
}