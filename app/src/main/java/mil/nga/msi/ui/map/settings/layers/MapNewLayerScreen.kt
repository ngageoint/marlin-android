package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.overlay.GridTileProvider

data class NewLayerState(
   val url: String,
   val type: LayerType
)

@Composable
fun MapNewLayerScreen(
   onConfirm: (NewLayerState) -> Unit,
   onClose: () -> Unit,
   viewModel: MapNewLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   var url by remember { mutableStateOf("") }
   var type by remember { mutableStateOf<LayerType?>(null) }
   val tileServerUrl by viewModel.tileUrl.observeAsState()

   LaunchedEffect(tileServerUrl) {
      if (tileServerUrl != null && type == null) {
         type = LayerType.XYZ
      }
   }

   Column {
      TopBar(
         title = MapRoute.NewLayer.title,
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
               onUrlChanged = {
                  url = it
                  viewModel.onLayerUrl(it)
               },
               onTypeChanged = { type = it }
            )

            tileServerUrl?.let { uri ->
               MapLayer(
                  url = uri,
                  type = type,
                  modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f)
               )
            }

            if (tileServerUrl != null) {
               Button(
                  onClick = {
                     scope.launch {
                        type?.let { onConfirm(NewLayerState(url, it)) }
                     }
                  },
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
               ) {
                  Text(text = "Confirm URL")
               }
            }
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLayer(
   url: String,
   type: LayerType?,
   onUrlChanged: (String) -> Unit,
   onTypeChanged: (LayerType) -> Unit
) {
   Column(Modifier.padding(vertical = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = buildAnnotatedString {
               append("Please enter your layer URL, Marlin will do its best to auto detect the type of your layer.")

               withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(" NOTE: If tiles appear misaligned toggle between XYZ and TMS types.")
               }
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
               .fillMaxWidth()
               .padding(bottom = 24.dp)
         )
      }

      TextField(
         value = url,
         label = { Text("Layer URL") },
         onValueChange = { onUrlChanged(it) },
         modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
      )

      Row(
         horizontalArrangement = Arrangement.Center,
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
      ) {

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.XYZ,
               onClick = { onTypeChanged(LayerType.XYZ) }
            )

            Text(text = "XYZ")
         }

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.TMS,
               onClick = { onTypeChanged(LayerType.TMS) }
            )

            Text(text = "TMS")
         }

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.WMS,
               onClick = { onTypeChanged(LayerType.WMS) }
            )

            Text(text = "WMS")
         }
      }
   }
}

@Composable
private fun MapLayer(
   url: Uri,
   type: LayerType?,
   modifier: Modifier = Modifier
) {
   // TODO if XYZ no zoom
   // TODO if WMS zoom to bound if available

//   val cameraPositionState = rememberCameraPositionState {
//      position = CameraPosition.fromLatLngZoom(latLng, 16f)
//   }

   val uiSettings = MapUiSettings(
      compassEnabled = false
   )

   val properties = MapProperties(
      mapType = MapType.NORMAL
   )

   GoogleMap(
//      cameraPositionState = cameraPositionState,
      properties = properties,
      uiSettings = uiSettings,
      modifier = modifier
   ) {
      tileOverlayOptions {
         TileOverlay(
            tileProvider = GridTileProvider(url, invertYAxis = type == LayerType.TMS)
         )
      }
   }
}