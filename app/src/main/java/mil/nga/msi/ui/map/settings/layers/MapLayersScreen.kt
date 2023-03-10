package mil.nga.msi.ui.map.settings.layers

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.remove

@Composable
fun MapLayersScreen(
   onTap: (Long, LayerType) -> Unit,
   onAddLayer: () -> Unit,
   onClose: () -> Unit,
   viewModel: MapLayersViewModel = hiltViewModel()
) {
   val layers by viewModel.layers.observeAsState(emptyList())

   Column {
      TopBar(
         title = MapRoute.Layers.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { onClose() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         Column(
            Modifier
               .fillMaxSize()
               .padding(vertical = 32.dp)
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surface)
                  .clickable {
                     onAddLayer()
                  }
                  .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Icon(Icons.Outlined.AddBox,
                     modifier = Modifier.padding(end = 16.dp),
                     contentDescription = "Add New Layer"
                  )
               }

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                  Text(
                     text = "Add New Layer",
                     style = MaterialTheme.typography.bodyLarge
                  )
               }
            }

            Layers(
               layers,
               onTap = { onTap(it.id, it.type) },
               onToggle = { layer, enabled -> viewModel.enableLayer(layer, enabled) },
               onRemove = { viewModel.deleteLayer(it) },
               onLayerReorder = { fromIndex, toIndex ->
                  val ordered = layers.toMutableList().apply {
                     val removed = removeAt(fromIndex)
                     add(toIndex, removed)
                  }
                  viewModel.setLayerOrder(ordered.map { it.id.toInt() })
               }
            )
         }
      }
   }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun Layers(
   layers: List<Layer>,
   onTap: (Layer) -> Unit,
   onToggle: (Layer, Boolean) -> Unit,
   onRemove: (Layer) -> Unit,
   onLayerReorder: (from: Int, to: Int) -> Unit
) {
   val listState = rememberLazyListState()
   val dragDropState = rememberDragDropState(listState) { from, to ->
      onLayerReorder(from, to)
   }

   Column(
      Modifier
         .padding(vertical = 16.dp)
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
         color = MaterialTheme.colorScheme.surface
      ) {
         LazyColumn(
            state = listState,
            modifier = Modifier.dragContainer(dragDropState)
         ) {
            itemsIndexed(
               items = layers,
               key = { _, item -> item.id }
            ) { index, layer ->
               DraggableItem(dragDropState, index) { isDragging ->
                  val dismissState = rememberDismissState()

                  if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                     onRemove(layer)
                  }

                  SwipeToDismiss(
                     state = dismissState,
                     directions = setOf(DismissDirection.EndToStart),
                     background = {
                        val color by animateColorAsState(
                           when (dismissState.targetValue) {
                              DismissValue.Default -> MaterialTheme.colorScheme.surface
                              else -> MaterialTheme.colorScheme.remove
                           }
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

                     }
                  ) {
                     Layer(
                        layer = layer,
                        isDragging = isDragging,
                        onTap = { onTap(layer) },
                        onToggle = { layer, enabled ->
                           onToggle(layer, enabled)
                        }
                     )
                  }
               }
            }
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Layer(
   layer: Layer,
   isDragging: Boolean,
   onTap: () -> Unit,
   onToggle: (Layer, Boolean) -> Unit
) {
   val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

   ListItem(
      tonalElevation = elevation,
      shadowElevation = elevation,
      headlineText = { Text(layer.name) },
      supportingText = {
         Text(
            text = layer.url,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      },
      leadingContent = {
         Icon(
            Icons.Default.Menu,
            contentDescription = "Reorder Layer"
         )
      },
      trailingContent = {
         androidx.compose.material3.Checkbox(
            checked = layer.visible,
            onCheckedChange = {
               onToggle(layer, !layer.visible)
            }
         )
      },
      modifier = Modifier.clickable { onTap() }
   )

   Divider()
}