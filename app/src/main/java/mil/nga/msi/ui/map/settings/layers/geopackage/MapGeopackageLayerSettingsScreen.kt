package mil.nga.msi.ui.map.settings.layers.geopackage

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.settings.layers.MapLayerRoute

@Composable
fun MapGeoPackageLayerSettingsScreen(
   id: Long,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: GeopackageLayerViewModel = hiltViewModel()
) {
   val geoPackageState by viewModel.geopackageState.observeAsState()

   LaunchedEffect(id) {
      viewModel.setLayerId(id)
   }

   Column {
      TopBar(
         title = MapLayerRoute.GeoPackageLayerCreateSettings.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      geoPackageState?.let { state ->
         Column(Modifier.fillMaxHeight()) {
            GeoPackageLayer(
               geoPackageState = state,
               onDone = {
                  done(Layer(
                     id = state.layer?.id ?: 0,
                     name = state.layer?.name ?: "",
                     type = LayerType.GEOPACKAGE,
                     url = state.selectedLayers.joinToString(",") { it },
                     filePath = state.layer?.filePath,
                     tables = geoPackageState?.overlays?.map { it.table } ?: emptyList(),
                     boundingBox = state.boundingBox
                  ))
               },
               onLayerChecked = { name, checked ->
                  viewModel.enableLayer(name, checked)
               }
            )
         }
      }
   }
}

@Composable
fun MapGeoPackageLayerSettingsScreen(
   uri: Uri?,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: GeopackageLayerViewModel = hiltViewModel()
) {
   val geoPackageState by viewModel.geopackageState.observeAsState()

   LaunchedEffect(uri) { uri?.let { viewModel.setUri(it) } }

   Column {
      TopBar(
         title = MapLayerRoute.GeoPackageLayerCreateSettings.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      geoPackageState?.let { state ->
         Column(Modifier.fillMaxHeight()) {
            GeoPackageLayer(
               geoPackageState = state,
               onDone = {
                  done(Layer(
                     id = state.layer?.id ?: 0,
                     name = state.layer?.name ?: "",
                     type = LayerType.GEOPACKAGE,
                     url = state.selectedLayers.joinToString(",") { it },
                     filePath = state.layer?.filePath,
                     tables = state.overlays.map { it.table },
                     boundingBox = state.boundingBox
                  ))
               },
               onLayerChecked = { name, checked ->
                  viewModel.enableLayer(name, checked)
               }
            )
         }
      }
   }
}

@Composable
fun MapGeoPackageLayerSettingsScreen(
   layer: Layer,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: GeopackageLayerViewModel = hiltViewModel()
) {
   val geoPackageState by viewModel.geopackageState.observeAsState()

   LaunchedEffect(layer) { viewModel.enableLayer(layer) }

   Column {
      TopBar(
         title = MapLayerRoute.GeoPackageLayerCreateSettings.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      geoPackageState?.let { state ->
         Column(Modifier.fillMaxHeight()) {
            GeoPackageLayer(
               geoPackageState = state,
               onDone = {
                  done(Layer(
                     id = layer.id,
                     name = layer.name,
                     type = LayerType.GEOPACKAGE,
                     url = state.selectedLayers.joinToString(",") { it },
                     filePath = layer.filePath,
                     tables = state.overlays.map { it.table },
                     boundingBox = state.boundingBox
                  ))
               },
               onLayerChecked = { name, checked ->
                  viewModel.enableLayer(name, checked)
               }
            )
         }
      }
   }
}

@Composable
private fun GeoPackageLayer(
   geoPackageState: GeoPackageState,
   onDone: () -> Unit,
   onLayerChecked: (String, Boolean) -> Unit
) {
   var latLngBounds by remember { mutableStateOf<LatLngBounds?>(null) }

   Surface(
      color = MaterialTheme.colorScheme.surfaceVariant
   ) {
      Column {
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
            items(geoPackageState.overlays) { overlay ->
               Table(
                  table = overlay.table,
                  layers = geoPackageState.selectedLayers,
                  onZoom = {
                     latLngBounds = overlay.boundingBox
                  },
                  onLayerChecked = onLayerChecked
               )
            }
         }

         Map(
            geoPackageState = geoPackageState,
            latLngBounds = latLngBounds,
            modifier = Modifier.weight(1f)
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

@Composable
private fun Table(
   table: String,
   layers: List<String>,
   onZoom: () -> Unit,
   onLayerChecked: (String, Boolean) -> Unit
) {
   ListItem(
      headlineContent = {
         Text(
            text = table,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      },
      leadingContent = {
         Icon(
            Icons.Outlined.Layers,
            contentDescription = "Layer"
         )
      },
      trailingContent = {
         Row {
            IconButton(onClick = { onZoom() }) {
               Icon(Icons.Default.MyLocation, contentDescription = "Zoom to GeoPackage Bounds")
            }

            Checkbox(
               checked = layers.contains(table),
               onCheckedChange = { checked ->
                  onLayerChecked(table, checked)
               }
            )
         }
      }
   )
}

@Composable
private fun Map(
   geoPackageState: GeoPackageState,
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
      geoPackageState.selectedLayers.forEach { table ->
         geoPackageState.overlays.find { it.table == table }?.let { overlay ->
            TileOverlay(tileProvider = overlay.tileProvider)
         }
      }
   }
}