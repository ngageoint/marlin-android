package mil.nga.msi.ui.map.settings.layers.grid

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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

   Column {
      TopBar(
         title = MapLayerRoute.CreateGridLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      MapGridLayerScreen(
         name = name,
         type = layer.type,
         url = layer.url,
         onNameChanged = { name = it },
         onSave = {
            scope.launch {
               viewModel.createLayer(
                  name = name,
                  type = layer.type,
                  url = layer.url
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
   val layer by viewModel.layer.observeAsState()
   viewModel.setId(id)

   LaunchedEffect(layer) {
      layer?.let { name = it.name }
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
            onSave = {
               scope.launch {
                  val update = Layer(
                     id = layer.id,
                     name = name,
                     type = layer.type,
                     url = layer.url
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
   name: String,
   type: LayerType,
   url: String,
   onNameChanged: (String) -> Unit,
   onSave: () -> Unit
) {
   Surface(
      color = MaterialTheme.colorScheme.surfaceVariant
   ) {
      Column(Modifier.fillMaxHeight()) {
         GridLayer(
            url = url,
            type = type,
            name = name,
            onNameChanged = onNameChanged
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
   url: String,
   type: LayerType,
   onNameChanged: (String) -> Unit
) {
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
         text = url,
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(bottom = 4.dp)
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = type.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
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