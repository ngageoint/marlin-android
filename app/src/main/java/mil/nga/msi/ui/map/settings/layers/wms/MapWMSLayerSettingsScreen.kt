package mil.nga.msi.ui.map.settings.layers.wms

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.overlay.WMSTileProvider
import mil.nga.msi.ui.map.settings.layers.MapNewLayerViewModel

@Composable
fun MapWMSLayerSettingsScreen(
   url: String,
   done: (String) -> Unit,
   onClose: () -> Unit,
   viewModel: MapNewLayerViewModel = hiltViewModel()
) {
   var wmsUrl by remember { mutableStateOf(url) }
   var wmsLayers by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
   val wmsCapabilities by viewModel.wmsCapabilities.observeAsState()

   viewModel.onLayerUrl(url)

   Column {
      TopBar(
         title = MapRoute.WMSLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      wmsCapabilities?.let { wms ->
         Surface(
            color = MaterialTheme.colorScheme.surfaceVariant
         ) {
            Column(Modifier.fillMaxHeight()) {
               WMSLayer(
                  wmsUrl = wmsUrl,
                  wmsLayers = wmsLayers,
                  wmsCapabilities = wms,
                  onLayerChecked = { name, checked ->
                     wmsLayers = wmsLayers.toMutableMap().apply { this[name] = checked }
                     wmsUrl = wmsUrl(url, wmsLayers.filter { it.value }.keys.toList(), wms)
                  },
                  modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f)
               )

               Button(
                  onClick = { done(wmsUrl) },
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
               ) {
                  Text(text = "Next")
               }
            }
         }
      }
   }
}

@Composable
private fun WMSLayer(
   wmsUrl: String,
   wmsLayers: Map<String, Boolean>,
   wmsCapabilities: WMSCapabilities,
   onLayerChecked: (String, Boolean) -> Unit,
   modifier: Modifier = Modifier
) {
   val scrollState = rememberScrollState()

   Column(modifier = modifier) {
      Column() {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "LAYERS",
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(16.dp)
            )
         }

         Column(
            Modifier
               .verticalScroll(scrollState)
               .weight(1f)
         ) {
            wmsCapabilities.capability?.layers?.asSequence()?.filter { layer ->
               layer.isWebMercator()
            }?.forEach { layer ->
               WMSCapabilitiesLayer(
                  layer = layer,
                  wmsLayers = wmsLayers,
                  onLayerChecked = onLayerChecked
               )
            }
         }

         Map(
            wmsUrl = wmsUrl,
            modifier = Modifier.height(250.dp)
         )
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WMSCapabilitiesLayer(
   layer: mil.nga.msi.network.layer.wms.Layer,
   wmsLayers: Map<String, Boolean>,
   onLayerChecked: (String, Boolean) -> Unit
) {
   if (layer.layers.isNotEmpty()) {
      ListItem(
         headlineText = {
            Text(
               text = layer.title ?: "Folder",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         supportingText = {
            layer.abstract?.let {
               Text(
                  text = it,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         },
         leadingContent = {
            Icon(
               Icons.Outlined.Folder,
               contentDescription = "Folder"
            )
         },
         trailingContent = {
            IconButton(
               onClick = { }
            ) {
               Icon(
                  Icons.Default.ExpandMore,
                  contentDescription = "Expand Folder"
               )
            }
         }
      )

      layer.layers.forEach {
         WMSCapabilitiesLayer(
            layer = it,
            wmsLayers = wmsLayers,
            onLayerChecked = onLayerChecked
         )
      }
   } else if (layer.hasTiles()) {
      // TODO filter for 3857 or google epsg
      ListItem(
         headlineText = {
            Text(
               text = layer.title ?: "Layer",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         supportingText = {
            layer.abstract?.let {
               Text(
                  text = it,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         },
         leadingContent = {
            Icon(
               Icons.Outlined.Layers,
               contentDescription = "Layer"
            )
         },
         trailingContent = {
            Checkbox(
               checked = wmsLayers[layer.name] == true,
               onCheckedChange = { checked ->
                  layer.name?.let { name -> onLayerChecked(name, checked) }
               }
            )
         },
         modifier = Modifier.padding(start = 8.dp)
      )
   }
}

@Composable
private fun Map(
   wmsUrl: String,
   modifier: Modifier = Modifier
) {
   val layer = Layer(
      url = wmsUrl,
      name = "",
      displayName = "",
      type = LayerType.WMS
   )

   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "MAP",
         style = MaterialTheme.typography.bodyMedium,
         modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
      )
   }

   GoogleMap(
      properties = MapProperties(mapType = MapType.NORMAL),
      uiSettings = MapUiSettings(compassEnabled = false),
      modifier = modifier
   ) {
      tileOverlayOptions { TileOverlay(tileProvider = WMSTileProvider(url = layer.url)) }
   }
}

private fun wmsUrl(
   baseUrl: String,
   layers: List<String>,
   wmsCapabilities: WMSCapabilities,
): String {
   val format = wmsCapabilities.capability?.request?.map?.getImageFormat() ?: "image/png"
   val version = wmsCapabilities.version
   val epsg = if (version == "1.3" || version == "1.3.0") "CRS" else "SRS"

   return Uri.parse(baseUrl).buildUpon()
      .appendQueryParameter("REQUEST", "GetMap")
      .appendQueryParameter("SERVICE", "WMS")
      .appendQueryParameter(epsg, "EPSG:3857")
      .appendQueryParameter("WIDTH", "256")
      .appendQueryParameter("HEIGHT", "256")
      .appendQueryParameter("FORMAT", format)
      .appendQueryParameter("TRANSPARENT", "false")
      .appendQueryParameter("LAYERS", layers.joinToString(","))
      .build()
      .toString()
}