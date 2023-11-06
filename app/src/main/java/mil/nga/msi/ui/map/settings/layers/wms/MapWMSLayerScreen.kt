package mil.nga.msi.ui.map.settings.layers.wms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.preferences.Credentials
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import java.net.URLDecoder

@Composable
fun MapWMSLayerScreen(
   layer: Layer,
   credentials: Credentials?,
   onClose: () -> Unit,
   viewModel: MapWMSLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var name by remember { mutableStateOf(layer.name) }

   Column {
      TopBar(
         title = MapRoute.WMSLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         Column(Modifier.fillMaxHeight()) {
            WMSLayer(
               url = layer.url,
               name = name,
               onNameChanged = { name = it }
            )

            Button(
               enabled = name.isNotEmpty(),
               onClick = {
                  scope.launch {
                     viewModel.saveLayer(
                        layer = layer.copy(
                           name = name,
                           url = layer.url
                        ),
                        credentials = credentials
                     )
                     onClose()
                  }
               },
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
            ) {
               Text(text = "Save WMS Layer")
            }
         }
      }
   }
}

@Composable
private fun WMSLayer(
   url: String,
   name: String,
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
         text = URLDecoder.decode(url, "UTF-8"),
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(bottom = 4.dp)
      )
   }
}