package mil.nga.msi.ui.map.settings.layers

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.remove

@Composable
fun MapLayersScreen(
   onTap: (Long, LayerType) -> Unit,
   onZoom: (LatLngBounds) -> Unit,
   onAddLayer: () -> Unit,
   onDeleteLayer: (() -> Unit) -> Unit,
   onClose: () -> Unit,
   viewModel: MapLayersViewModel = hiltViewModel()
) {
   val layers by viewModel.layers.observeAsState(emptyList())

   Column {
      TopBar(
         title = MapLayerRoute.Layers.title,
         navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
         onNavigationClicked = { onClose() }
      )

      Box {
         if (layers.isEmpty()) {
            Empty()
         } else {
            Layers(
               layers = layers,
               onTap = { onTap(it.id, it.type) },
               onZoom = onZoom,
               onToggle = { layer, enabled -> viewModel.enableLayer(layer, enabled) },
               onDelete = { layer ->
                  viewModel.deleteLayer(layer)
                  onDeleteLayer { viewModel.addLayer(layer) }
               },
               onLayerReorder = { fromIndex, toIndex ->
                  val ordered = layers.toMutableList().apply {
                     val removed = removeAt(fromIndex)
                     add(toIndex, removed)
                  }
                  viewModel.setLayerOrder(ordered.map { it.layer.id.toInt() })
               },
               modifier = Modifier.fillMaxSize()
            )
         }

         Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
               .fillMaxWidth()
               .align(Alignment.BottomCenter)
               .padding(bottom = 16.dp)
         ) {
            ExtendedFloatingActionButton(
               containerColor = MaterialTheme.colorScheme.primary,
               onClick = { onAddLayer() }
            ) {
               Icon(
                  imageVector = Icons.Outlined.AddBox,
                  contentDescription = "Add Layer"
               )
               Spacer(Modifier.size(ButtonDefaults.IconSpacing))
               Text("Add New Layer")
            }
         }
      }
   }
}

@Composable
private fun Empty() {
   Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
         .fillMaxSize()
         .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
   ) {
      if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) {
            Icon(
               imageVector = Icons.Filled.Layers,
               contentDescription = "No Layers",
               modifier = Modifier
                  .size(260.dp)
                  .padding(bottom = 8.dp)
            )
         }
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
         Text(
            text = "No Layers",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) {
         Text(
            text = "Create a new layer and it will show up here.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
         )
      }
   }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Layers(
   layers: List<LayerState>,
   onTap: (Layer) -> Unit,
   onZoom: (LatLngBounds) -> Unit,
   onToggle: (Layer, Boolean) -> Unit,
   onDelete: (Layer) -> Unit,
   onLayerReorder: (from: Int, to: Int) -> Unit,
   modifier: Modifier = Modifier
) {
   val listState = rememberLazyListState()
   val dragDropState = rememberDragDropState(listState) { from, to ->
      onLayerReorder(from, to)
   }

   Column(
      modifier = modifier
   ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "MAP LAYERS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
         Text(
            text = "Reorder layers on map with long press and drag.",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
         )
      }

      Surface(
         color = MaterialTheme.colorScheme.surface,
         modifier = Modifier.fillMaxSize()
      ) {
         LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 72.dp),
            modifier = Modifier.dragContainer(dragDropState)
         ) {
            itemsIndexed(
               items = layers,
               key = { _, state -> state.layer.id }
            ) { index, state ->
               val dismissState = rememberDismissState(
                  confirmValueChange = {
                     onDelete(state.layer)
                     true
                  }
               )

               DraggableItem(dragDropState, index) { isDragging ->
                  SwipeToDismiss(
                     state = dismissState,
                     directions = setOf(DismissDirection.EndToStart),
                     background = {
                        val color by animateColorAsState(
                           when (dismissState.targetValue) {
                              DismissValue.Default -> MaterialTheme.colorScheme.surface
                              else -> MaterialTheme.colorScheme.remove
                           }, label = "color_state_animator"
                        )

                        Surface(
                           color = MaterialTheme.colorScheme.remove
                        ) {
                           Box(
                              Modifier
                                 .fillMaxSize()
                                 .background(color)
                                 .padding(horizontal = 16.dp),
                              contentAlignment = Alignment.CenterEnd
                           ) {
                              Icon(
                                 Icons.Default.Delete,
                                 tint = MaterialTheme.colorScheme.onPrimary,
                                 contentDescription = "Delete Icon"
                              )
                           }
                        }

                     },
                     dismissContent = {
                        Layer(
                           state = state,
                           isDragging = isDragging,
                           onTap = { onTap(state.layer) },
                           onZoom = onZoom,
                           onToggle = { layer, enabled -> onToggle(layer, enabled) }
                        )
                     }
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun Layer(
   state: LayerState,
   isDragging: Boolean,
   onTap: () -> Unit,
   onZoom: (LatLngBounds) -> Unit,
   onToggle: (Layer, Boolean) -> Unit
) {
   val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "elevation_animator")

   ListItem(
      tonalElevation = elevation,
      shadowElevation = elevation,
      headlineContent = { Text(state.layer.name) },
      supportingContent = {
         val text = when (state.layer.type) {
            LayerType.GEOPACKAGE -> "GeoPackage"
            else -> state.layer.url
         }

         Column {
            Text(
               text = text,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
            state.latLngBounds?.let { bounds ->
               val southwest = DMS.from(LatLng(bounds.southwest.latitude, bounds.southwest.longitude)).format()
               val northeast = DMS.from(LatLng(bounds.southwest.latitude, bounds.southwest.longitude)).format()

               Text(
                  text = "($southwest) - ($northeast)",
                  modifier = Modifier.padding(top = 4.dp)
               )
            }
         }
      },
      leadingContent = {
         Icon(
            Icons.Default.Menu,
            contentDescription = "Reorder Layer"
         )
      },
      trailingContent = {
         Row {
            state.layer.boundingBox?.let { boundingBox ->
               IconButton(
                  onClick = { onZoom(boundingBox.latLngBounds) }
               ) {
                  Icon(Icons.Default.MyLocation, contentDescription = "Zoom to GeoPackage")
               }
            }

            Checkbox(
               checked = state.layer.visible,
               onCheckedChange = {
                  onToggle(state.layer, !state.layer.visible)
               }
            )
         }
      },
      modifier = Modifier.clickable { onTap() }
   )

   Divider()
}