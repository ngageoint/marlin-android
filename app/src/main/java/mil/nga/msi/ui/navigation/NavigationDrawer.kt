package mil.nga.msi.ui.navigation

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository.Companion.MAX_TABS
import mil.nga.msi.ui.about.AboutRoute
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
import mil.nga.msi.ui.report.ReportRoute
import mil.nga.msi.ui.theme.screenBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigationDrawer(
   onDestinationClicked: (route: String) -> Unit,
   viewModel: NavigationViewModel = hiltViewModel()
) {
   val loadingState by viewModel.fetching.observeAsState(emptyMap())

   val tabsPreference by viewModel.tabs.observeAsState()
   val nonTabsPreference by viewModel.nonTabs.observeAsState()

   var previousTabs by remember { mutableStateOf<List<Tab>?>(null) }
   var previousNonTabs by remember { mutableStateOf<List<Tab>?>(null) }
   if (previousTabs == null) { previousTabs = tabsPreference }
   if (previousNonTabs == null) { previousNonTabs = nonTabsPreference }

   val listState = rememberLazyListState()
   val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
      val tabs = previousTabs
      val nonTabs = previousNonTabs

      if (tabs == null || nonTabs == null) { return@rememberDragDropState }
      if (toIndex == 0) { return@rememberDragDropState }

      if (fromIndex <= tabs.size && toIndex <= tabs.size) {
         // Drag within list1
         val to = toIndex - 1
         val from = if (fromIndex == 0) 0 else fromIndex - 1
         previousTabs = tabs.toMutableList().apply { add(to, removeAt(from)) }
         previousTabs?.let { viewModel.setTabs(it) }
      } else if (fromIndex <= tabs.size) {
         // Drag from list1 to list2
         val list1Element = tabs[fromIndex - 1]
         previousTabs = tabs.toMutableList().apply { removeLast() }
         previousNonTabs = nonTabs.toMutableList().apply {
            add(toIndex - tabs.size - 1, list1Element)
         }
         previousTabs?.let { viewModel.setTabs(it) }
         previousNonTabs?.let { viewModel.setNonTabs(it) }
      } else if (toIndex <= tabs.size + 1) {
         // Drag from list2 to list1
         nonTabs.getOrNull(fromIndex - tabs.size - 2)?.let { dataSource ->
            var newNonTabs = nonTabs.toMutableList().apply { removeFirst() }
            var newTabs = tabs.toMutableList().apply { add(lastIndex + 1, dataSource) }

            if (newTabs.size > MAX_TABS) {
               newTabs.getOrNull(newTabs.lastIndex - 1)?.let { evict ->
                  newTabs = newTabs.toMutableList().apply { remove(evict) }
                  newNonTabs = newNonTabs.toMutableList().apply { add(0, evict) }
               }
            }

            viewModel.setTabs(newTabs)
            viewModel.setNonTabs(newNonTabs)
            previousTabs = newTabs
            previousNonTabs = newNonTabs
         }
      } else if (fromIndex <= tabs.size + nonTabs.size + 1 && toIndex <= tabs.size + nonTabs.size + 1) {
         // Drag within list2
         previousNonTabs = nonTabs.toMutableList().apply {
            getOrNull(fromIndex - tabs.size - 2)?.let { dataSource ->
               remove(dataSource)
               add(toIndex - tabs.size - 2, dataSource)
            }
         }
         previousNonTabs?.let { viewModel.setNonTabs(it) }
      }
   }

   val tabs = previousTabs
   val nonTabs = previousNonTabs
   if (tabs != null && nonTabs != null) {
      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier
               .fillMaxHeight()
               .dragContainer(dragDropState)
               .background(MaterialTheme.colorScheme.screenBackground)
         ) {
            item {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Data Source Tabs (Drag to reorder, $MAX_TABS max.)",
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.Medium,
                     modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
                  )
               }
            }

            itemsIndexed(
               tabs,
               key = { _, tab -> tab.dataSource.name }
            ) { index, tab ->
               DraggableItem(dragDropState, index + 1) { isDragging ->

                  NavigationRow(
                     dataSource = tab.dataSource,
                     loading = loadingState[tab.dataSource],
                     isDragging = isDragging,
                     onDestinationClicked = {
                        onDestinationClicked(tab.route.name)
                     }
                  )
               }
            }

            item {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "Other Data Sources (Drag to add to tabs)",
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.Medium,
                     modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
                  )
               }
            }

            itemsIndexed(
               nonTabs,
               key = { _, tab -> tab.dataSource.name }
            ) { index, tab ->
               DraggableItem(dragDropState, tabs.size + index + 2) { isDragging ->
                  NavigationRow(
                     dataSource = tab.dataSource,
                     loading = loadingState[tab.dataSource],
                     isDragging = isDragging,
                     onDestinationClicked = {
                        onDestinationClicked(tab.route.name)
                     }
                  )
               }
            }

            item {
               Column(
                  Modifier
                     .padding(top = 32.dp)
                     .height(56.dp)
                     .fillMaxWidth()
               ) {
                  Surface {
                     Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                           .fillMaxSize()
                           .clickable {
                              onDestinationClicked(ReportRoute.Main.name)
                           }
                     ) {

                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                           Icon(
                              Icons.Filled.NoteAdd,
                              modifier = Modifier.padding(start = 8.dp),
                              contentDescription = "Submit Report to NGA"
                           )
                        }

                        Column(
                           verticalArrangement = Arrangement.Center,
                           modifier = Modifier.fillMaxWidth()
                        ) {
                           Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween,
                              modifier = Modifier
                                 .height(72.dp)
                                 .fillMaxWidth()
                                 .weight(1f)
                                 .padding(horizontal = 8.dp)
                           ) {
                              CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                 Text(
                                    text = ReportRoute.Main.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                 )
                              }
                           }
                        }
                     }
                  }
               }

               Column(
                  Modifier
                     .padding(top = 32.dp)
                     .height(56.dp)
                     .fillMaxWidth()
               ) {
                  Surface {
                     Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                           .fillMaxSize()
                           .clickable {
                              onDestinationClicked(AboutRoute.Main.name)
                           }
                     ) {

                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                           Icon(
                              Icons.Default.Info,
                              modifier = Modifier.padding(start = 8.dp),
                              contentDescription = "About"
                           )
                        }

                        Column(
                           verticalArrangement = Arrangement.Center,
                           modifier = Modifier.fillMaxWidth()
                        ) {
                           Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween,
                              modifier = Modifier
                                 .height(72.dp)
                                 .fillMaxWidth()
                                 .weight(1f)
                                 .padding(horizontal = 8.dp)
                           ) {
                              CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                                 Text(
                                    text = AboutRoute.Main.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                 )
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun NavigationRow(
   dataSource: DataSource,
   loading: Boolean?,
   isDragging: Boolean = false,
   onDestinationClicked: () -> Unit
) {
   val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "elevation_animator")

   Surface(
      tonalElevation = elevation,
      shadowElevation = elevation
   ) {
      Column(
         Modifier
            .height(56.dp)
            .fillMaxWidth()
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxSize()
               .clickable {
                  onDestinationClicked()
               }
         ) {
            Box(
               modifier = Modifier
                  .width(8.dp)
                  .fillMaxHeight()
                  .background(dataSource.color)
            )

            if (loading == true) {
               Box(Modifier.padding(start = 8.dp)) {
                  CircularProgressIndicator(
                     color = MaterialTheme.colorScheme.onSurface.copy(alpha = .4f),
                     strokeWidth = 3.dp,
                     modifier = Modifier.size(24.dp)
                  )
               }
            } else {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  val bitmap = AppCompatResources.getDrawable(LocalContext.current, dataSource.icon)!!.toBitmap().asImageBitmap()
                  Icon(
                     bitmap = bitmap,
                     modifier = Modifier.padding(start = 8.dp),
                     contentDescription = "Navigation Tab Icon"
                  )
               }
            }

            Column(
               verticalArrangement = Arrangement.Center,
               modifier = Modifier.fillMaxWidth()
            ) {
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier
                     .height(72.dp)
                     .fillMaxWidth()
                     .weight(1f)
                     .padding(horizontal = 8.dp)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                     Text(
                        text = dataSource.labelPlural,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                     )
                  }
               }
            }
         }
      }

      Divider(Modifier.fillMaxWidth())
   }
}