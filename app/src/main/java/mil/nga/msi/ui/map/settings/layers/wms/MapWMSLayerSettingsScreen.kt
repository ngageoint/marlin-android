package mil.nga.msi.ui.map.settings.layers.wms

import android.util.Log
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
                     type = LayerType.WMS
                  )
                  done(layer)
               },
               onLayerChecked = { name, checked ->
                  viewModel.setLayer(name, checked)
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
                     type = LayerType.WMS
                  ))
               },
               onLayerChecked = { name, checked ->
                  viewModel.setLayer(name, checked)
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
   onLayerChecked: (String, Boolean) -> Unit,
   modifier: Modifier = Modifier
) {
   val scrollState = rememberScrollState()
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

         Column(
            Modifier
               .verticalScroll(scrollState)
               .weight(1f)
         ) {
            wmsState.wmsCapabilities.capability?.layers?.asSequence()?.filter { layer ->
               layer.isWebMercator()
            }?.forEach { layer ->
               WMSCapabilitiesLayer(
                  layer = layer,
                  wmsLayers = wmsState.layers,
                  onLayerChecked = { layer, name, checked ->
                     if (checked) {
                        latLngBounds = layer.boundingBoxes.firstOrNull {
                           it.crs.equals("CRS:84", ignoreCase = true)
                        }?.let {
                           val southwest = LatLng(it.minY, it.minX)
                           val northeast = LatLng(it.maxY, it.maxX)
                           LatLngBounds(southwest, northeast)
                        }
                     }

                     onLayerChecked(name, checked)
                  }
               )
            }
         }

         Map(
            wmsUrl = wmsState.mapUrl,
            latLngBounds = latLngBounds,
            modifier = Modifier.height(250.dp)
         )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WMSCapabilitiesLayer(
   layer: mil.nga.msi.network.layer.wms.Layer,
   wmsLayers: List<String>,
   onLayerChecked: (mil.nga.msi.network.layer.wms.Layer, String, Boolean) -> Unit
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
               checked = wmsLayers.contains(layer.name),
               onCheckedChange = { checked ->
                  layer.name?.let { name -> onLayerChecked(layer, name, checked) }
               }
            )
         }
      )
   }
}

@Composable
private fun Map(
   wmsUrl: String,
   latLngBounds: LatLngBounds?,
   modifier: Modifier = Modifier
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
      modifier = modifier
   ) {
      tileOverlayOptions { TileOverlay(tileProvider = WMSTileProvider(url = wmsUrl)) }
   }
}