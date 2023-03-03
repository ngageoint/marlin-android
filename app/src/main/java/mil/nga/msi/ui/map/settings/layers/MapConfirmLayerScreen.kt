package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.overlay.GridTileProvider

@Composable
fun MapConfirmLayerScreen(
   type: LayerType,
   url: String,
   onClose: () -> Unit,
   viewModel: MapConfirmLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var name by remember { mutableStateOf("") }

   Column {
      TopBar(
         title = MapRoute.ConfirmLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         Column(
            Modifier
               .padding(horizontal = 16.dp)
               .fillMaxHeight()
         ) {
            NewLayer(
               url = url,
               type = type,
               name = name,
               onNameChanged = { name = it }
            )

            MapLayer(
               url = Uri.parse(url),
               type = type,
               modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
            )

            Button(
               enabled = name.isNotEmpty(),
               onClick = {
                  scope.launch {
                     scope.launch {
                        val layer = Layer(
                           name = name,
                           displayName = name,
                           type = type,
                           url = url,
                           visible = true
                        )

                        viewModel.saveLayer(layer)
                        onClose()
                     }
                  }
               },
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
            ) {
               Text(text = "Save Layer")
            }
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLayer(
   url: String,
   type: LayerType,
   name: String,
   onNameChanged: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 16.dp)) {
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
private fun MapLayer(
   url: Uri,
   type: LayerType,
   modifier: Modifier = Modifier
) {
   GoogleMap(
      properties = MapProperties(
         mapType = MapType.NONE
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