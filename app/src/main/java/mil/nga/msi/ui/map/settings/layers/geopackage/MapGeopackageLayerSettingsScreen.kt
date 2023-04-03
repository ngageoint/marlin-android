package mil.nga.msi.ui.map.settings.layers.geopackage

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.geopackage.map.tiles.overlay.XYZGeoPackageOverlay
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles
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
                     url = state.selectedLayers.joinToString(",") { it },
                     filePath = state.layer?.filePath,
                     tables = geoPackageState?.layers?.map { it.table } ?: emptyList(),
                     type = LayerType.GEOPACKAGE
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
                     url = state.selectedLayers.joinToString(",") { it },
                     filePath = state.layer?.filePath,
                     tables = state.layers.map { it.table },
                     type = LayerType.GEOPACKAGE
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
fun MapGeoPackageLayerSettingsScreen(
   layer: Layer,
   done: (Layer) -> Unit,
   onClose: () -> Unit,
   viewModel: GeopackageLayerViewModel = hiltViewModel()
) {
   val geoPackageState by viewModel.geopackageState.observeAsState()

   LaunchedEffect(layer) { viewModel.setLayer(layer) }

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
                     url = state.layers?.map { it.table }?.joinToString(",") ?: "",
                     filePath = layer.filePath,
                     tables = state.layers.map { it.table },
                     type = LayerType.GEOPACKAGE
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
private fun GeoPackageLayer(
   geoPackageState: GeoPackageState,
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
            geoPackageState.layers.forEach { layer ->
               Table(
                  table = layer.table,
                  layers = geoPackageState.selectedLayers,
                  onLayerChecked = onLayerChecked
               )
            }
         }

         Map(
            geoPackageState = geoPackageState,
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
private fun Table(
   table: String,
   layers: List<String>,
   onLayerChecked: (String, Boolean) -> Unit
) {
   ListItem(
      headlineText = {
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
         Checkbox(
            checked = layers.contains(table),
            onCheckedChange = { checked ->
               onLayerChecked(table, checked)
            }
         )
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
         geoPackageState.layers.find { it.table == table }?.let { layer ->
            val tileProvider = when(layer.type) {
               GeoPackageLayerType.TILE -> {
                  val tileDao = geoPackageState.geoPackage.getTileDao(layer.table)
                  XYZGeoPackageOverlay(tileDao)
               }
               GeoPackageLayerType.FEATURE -> {
                  val featureDao = geoPackageState.geoPackage.getFeatureDao(layer.table)
                  val featureTiles = DefaultFeatureTiles(LocalContext.current, geoPackageState.geoPackage, featureDao)
                  FeatureOverlay(featureTiles)
               }
            }

            TileOverlay(tileProvider = tileProvider)
         }
      }
   }
}