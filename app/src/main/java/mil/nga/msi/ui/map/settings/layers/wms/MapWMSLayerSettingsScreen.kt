package mil.nga.msi.ui.map.settings.layers.wms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.network.layer.LayerService
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
               layerService = viewModel.layerService,
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
               layerService = viewModel.layerService,
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
   layerService: LayerService,
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
               service = layerService,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WMSCapabilitiesLayer(
   layer: mil.nga.msi.network.layer.wms.Layer,
   wmsLayers: List<String>,
   onZoom: (mil.nga.msi.network.layer.wms.Layer) -> Unit,
   onLayerChecked: (mil.nga.msi.network.layer.wms.Layer, String, Boolean) -> Unit
) {
   if (layer.layers.isNotEmpty()) {
      ListItem(
         text = {
            Text(
               text = layer.title ?: "Folder",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         secondaryText = {
            layer.abstract?.let {
               Text(
                  text = it,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         },
         icon = {
            Icon(
               Icons.Outlined.Folder,
               contentDescription = "Folder"
            )
         },
         trailing = {
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
         text = {
            Text(
               text = layer.title ?: "Layer",
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         },
         secondaryText = {
            layer.abstract?.let {
               Text(
                  text = it,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         },
         icon = {
            Icon(
               Icons.Outlined.Layers,
               contentDescription = "Layer"
            )
         },
         trailing = {
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
   service: LayerService,
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
         tileOverlayOptions { TileOverlay(tileProvider = WMSTileProvider(service = service, url = wmsUrl)) }
      }
   }
}