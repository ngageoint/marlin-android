package mil.nga.msi.ui.map.settings.layers.wms

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.overlay.WMSTileProvider

@Composable
fun MapWMSLayerSettingsScreen(
   id: Long,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: MapWMSLayerViewModel = hiltViewModel()
) {
   val wmsState by viewModel.wmsState.observeAsState()

   LaunchedEffect(id) {
      viewModel.setLayerId(id)
   }

   Column {
      TopBar(
         title = MapRoute.WMSLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      wmsState?.let { wmsState ->
         Column(Modifier.fillMaxHeight()) {
            WMSLayer(
               wmsState = wmsState,
               onDone = {
                  val layer = Layer(
                     id = wmsState.layer?.id ?: 0,
                     name = wmsState.layer?.name ?: "",
                     url = wmsState.mapUrl,
                     type = LayerType.WMS,
                     boundingBox = wmsState.boundingBox
                  )
                  done(layer)
               },
               onLayerChecked = { layer, name, checked ->
                  viewModel.setLayer(layer, name, checked)
               },
               modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
            )
         }
      }
   }
}

@Composable
fun MapWMSLayerSettingsScreen(
   layer: Layer,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: MapWMSLayerViewModel = hiltViewModel()
) {
   val wmsState by viewModel.wmsState.observeAsState()

   LaunchedEffect(layer.url) {
      viewModel.setUrl(layer.url)
   }

   Column {
      TopBar(
         title = MapRoute.WMSLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      wmsState?.let { wmsState ->
         Column(Modifier.fillMaxHeight()) {
            WMSLayer(
               wmsState = wmsState,
               onDone = {
                  done(Layer(
                     id = layer.id,
                     name = layer.name,
                     url = wmsState.mapUrl,
                     type = LayerType.WMS,
                     boundingBox = wmsState.boundingBox
                  ))
               },
               onLayerChecked = { layer, name, checked ->
                  viewModel.setLayer(layer, name, checked)
               },
               modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
            )
         }
      }
   }
}

@Composable
private fun WMSLayer(
   wmsState: WmsState,
   onDone: () -> Unit,
   onLayerChecked: (mil.nga.msi.network.layer.wms.Layer, String, Boolean) -> Unit,
   modifier: Modifier = Modifier
) {
   var latLngBounds by remember { mutableStateOf<LatLngBounds?>(null) }

   Surface(
      color = MaterialTheme.colorScheme.surfaceVariant
   ) {
      Column(modifier = modifier) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "LAYERS",
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(16.dp)
            )
         }

         LazyColumn(
            modifier = Modifier.heightIn(0.dp, 250.dp)
         ) {
            items(
               wmsState.wmsCapabilities.capability?.layers?.filter {
                  it.isWebMercator()
               } ?: emptyList()
            ) { layer ->
               WMSCapabilitiesLayer(
                  layer = layer,
                  wmsLayers = wmsState.layers,
                  onZoom = { zoomLayer ->
                     latLngBounds = zoomLayer.boundingBoxes.firstOrNull {
                        it.crs.equals("CRS:84", ignoreCase = true)
                     }?.let {
                        val southwest = LatLng(it.minY, it.minX)
                        val northeast = LatLng(it.maxY, it.maxX)
                        LatLngBounds(southwest, northeast)
                     }
                  },
                  onLayerChecked = onLayerChecked
               )
            }
         }

         Box(Modifier.weight(1f)){
            Map(
               wmsUrl = wmsState.mapUrl,
               latLngBounds = latLngBounds
            )
         }

         Button(
            onClick = { onDone() },
            modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp)
         ) {
            Text(text = "Next")
         }
      }
   }
}

@Composable
private fun WMSCapabilitiesLayer(
   layer: mil.nga.msi.network.layer.wms.Layer,
   wmsLayers: List<String>,
   onZoom: (mil.nga.msi.network.layer.wms.Layer) -> Unit,
   onLayerChecked: (mil.nga.msi.network.layer.wms.Layer, String, Boolean) -> Unit
) {
   if (layer.layers.isNotEmpty()) {
      ListItem(
         headlineContent = {
            Text(
               text = layer.title ?: "Folder",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         supportingContent = {
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
               onClick = { onZoom(layer) }
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
            onZoom = onZoom,
            onLayerChecked = onLayerChecked
         )
      }
   } else if (layer.hasTiles()) {
      ListItem(
         headlineContent = {
            Text(
               text = layer.title ?: "Layer",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         supportingContent = {
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
            Row {
               IconButton(onClick = { onZoom(layer) }) {
                  Icon(Icons.Default.MyLocation, contentDescription = "Zoom to GeoPackage Bounds")
               }

               Checkbox(
                  checked = wmsLayers.contains(layer.name),
                  onCheckedChange = { checked ->
                     layer.name?.let { name -> onLayerChecked(layer, name, checked) }
                  }
               )
            }
         }
      )
   }
}

@Composable
private fun Map(
   wmsUrl: String,
   latLngBounds: LatLngBounds?
) {
   val scope = rememberCoroutineScope()
   val cameraPositionState = rememberCameraPositionState()

   LaunchedEffect(latLngBounds) {
      latLngBounds?.let {
         scope.launch {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))
         }
      }
   }

   Column(Modifier.fillMaxSize()) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "MAP",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
         )
      }

      GoogleMap(
         cameraPositionState = cameraPositionState,
         properties = MapProperties(mapType = MapType.NORMAL),
         uiSettings = MapUiSettings(compassEnabled = false),
         modifier = Modifier.weight(1f)
      ) {
         tileOverlayOptions { TileOverlay(tileProvider = WMSTileProvider(url = wmsUrl)) }
      }
   }
}