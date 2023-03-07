package mil.nga.msi.ui.map.settings.layers.wms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute

@Composable
fun MapWMSLayerScreen(
   url: String,
   onClose: () -> Unit,
   viewModel: MapWMSLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var name by remember { mutableStateOf("") }

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
               url = url,
               name = name,
               onNameChanged = { name = it }
            )

            Button(
               enabled = name.isNotEmpty(),
               onClick = {
                  scope.launch {
                     viewModel.saveLayer(name = name, url = url)
                     onClose()
                  }
               },
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
            ) {
               Text(text = "Create WMS Layer")
            }
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
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
         text = url,
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(bottom = 4.dp)
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = LayerType.WMS.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
         )
      }
   }
}