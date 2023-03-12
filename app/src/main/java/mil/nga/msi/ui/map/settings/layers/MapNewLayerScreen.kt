package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
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
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GridTileProvider
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerViewModel
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerViewModel

@Composable
fun MapNewLayerScreen(
   onClose: () -> Unit,
   onLayer: (Layer) -> Unit,
   wmsViewModel: MapWMSLayerViewModel = hiltViewModel(),
   gridViewModel: MapGridLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val scrollState = rememberScrollState()
   var url by remember { mutableStateOf("") }
   var type by remember { mutableStateOf<LayerType?>(null) }
   val tileServerUrl by gridViewModel.tileUrl.observeAsState()
   val wmsState by wmsViewModel.wmsState.observeAsState()

   LaunchedEffect(tileServerUrl) {
      if (tileServerUrl != null && type == null) {
         type = LayerType.XYZ
      }
   }

   LaunchedEffect(wmsState?.wmsCapabilities) {
      if (wmsState?.wmsCapabilities != null && type == null) {
         type = LayerType.WMS
      }
   }

   Column {
      TopBar(
         title = MapLayerRoute.NewLayer.title,
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
               .verticalScroll(scrollState)
         ) {
            Layer(
               url = url,
               type = type,
               onUrlChanged = {
                  url = it
                  wmsViewModel.setUrl(it)
                  gridViewModel.setUrl(it)
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

            wmsState?.wmsCapabilities?.let { wmsCapabilities ->
               WMSCapabilities(wmsCapabilities)
            }

            if (tileServerUrl != null || wmsState?.wmsCapabilities?.isValid() == true) {
               Button(
                  onClick = {
                     scope.launch {
                        if (tileServerUrl != null) {
                           type?.let {
                              val layer = Layer(
                                 name = "",
                                 type = it,
                                 url = tileServerUrl.toString()
                              )
                              onLayer(layer)
                           }
                        }

                        wmsState?.wmsCapabilities?.let {
                           val layer = Layer(
                              name = "",
                              type = LayerType.WMS,
                              url = url
                           )

                           onLayer(layer)
                        }
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
private fun Layer(
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
   GoogleMap(
      properties = MapProperties(mapType = MapType.NORMAL),
      uiSettings = MapUiSettings(compassEnabled = false),
      modifier = modifier
   ) {
      tileOverlayOptions {
         TileOverlay(
            tileProvider = GridTileProvider(url, invertYAxis = type == LayerType.TMS)
         )
      }
   }
}

@Composable
private fun WMSCapabilities(
   wmsCapabilities: WMSCapabilities
) {
   var expanded by remember { mutableStateOf(false) }

   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "WMS SERVER INFORMATION",
         style = MaterialTheme.typography.bodyMedium,
         modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
      )
   }

   Card {
      Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
         Text(
            text = wmsCapabilities.service?.title ?: "WMS Server",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
               .fillMaxWidth()
               .padding(top = 8.dp, bottom = 16.dp)
         )

         wmsCapabilities.service?.abstract?.let { abstract ->
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = abstract,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(bottom = 16.dp)
               )
            }
         }

         if (wmsCapabilities.capability?.request?.map?.hasImageFormat() != true) {
            Text(
               text = "WMS server does not support image tiles",
               style = MaterialTheme.typography.titleMedium,
               color = MaterialTheme.colorScheme.error,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp, bottom = 16.dp)
            )
         }

         if (!expanded) {
            IconButton(
               onClick = { expanded = true },
               modifier = Modifier.align(End)
            ) {
               Icon(Icons.Default.MoreHoriz,
                  tint = MaterialTheme.colorScheme.primary,
                  contentDescription = "Zoom to ASAM"
               )
            }
         }

         Column(Modifier.animateContentSize()) {
            if (expanded) {
               wmsCapabilities.version?.let {
                  WMSServiceProperty(name = "WMS Version", value = it)
               }

               wmsCapabilities.service?.contactInformation?.person?.name?.let {
                  WMSServiceProperty(name = "Contact Person", value = it)
               }

               wmsCapabilities.service?.contactInformation?.person?.organization?.let {
                  WMSServiceProperty(name = "Contact Organization", value = it)
               }

               wmsCapabilities.service?.contactInformation?.phone?.let {
                  WMSServiceProperty(name = "Contact Phone", value = it)
               }

               wmsCapabilities.service?.contactInformation?.email?.let {
                  WMSServiceProperty(name = "Contact Phone", value = it)
               }
            }
         }
      }
   }
}

@Composable
private fun WMSServiceProperty(
   name: String,
   value: String
) {
   Column(
      Modifier.padding(vertical = 8.dp)
   ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
         Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
         )
      }
   }
}