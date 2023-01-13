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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
import mil.nga.msi.ui.report.ReportRoute
import mil.nga.msi.ui.settings.AboutRoute
import mil.nga.msi.ui.theme.screenBackground

private const val MAX_TABS = 4

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigationDrawer(
   onDestinationClicked: (route: String) -> Unit,
   viewModel: NavigationViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val mapped by viewModel.mapped.observeAsState()
   val loadingState by viewModel.fetching.observeAsState(emptyMap())

   val tabsPreference by viewModel.tabs.observeAsState()
   val nonTabsPreference by viewModel.nonTabs.observeAsState()

   var previousTabs by remember { mutableStateOf<List<DataSource>?>(null) }
   var previousNonTabs by remember { mutableStateOf<List<DataSource>?>(null) }
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
      LazyColumn(
         state = listState,
         contentPadding = PaddingValues(bottom = 16.dp),
         modifier = Modifier
            .fillMaxHeight()
            .dragContainer(dragDropState)
            .background(MaterialTheme.colors.screenBackground)
      ) {
         item {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = "Data Source Tabs (Drag to reorder, $MAX_TABS max.)",
                  style = MaterialTheme.typography.body2,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
               )
            }
         }

         itemsIndexed(
            tabs,
            key = { _, tab -> tab.name }
         ) { index, tab ->
            DraggableItem(dragDropState, index + 1) { isDragging ->
               val isMapped = mapped?.get(tab) ?: false
               NavigationRow(
                  tab = tab,
                  loading = loadingState[tab],
                  isMapped = isMapped,
                  isDragging = isDragging,
                  onMapClicked = {
                     viewModel.toggleOnMap(tab)
                  },
                  onDestinationClicked = {
                     onDestinationClicked(it.name)
                  }
               )
            }
         }

         item {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = "Other Data Sources (Drag to add to tabs)",
                  style = MaterialTheme.typography.body2,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp)
               )
            }
         }

         itemsIndexed(
            nonTabs,
            key = { _, tab -> tab.name }
         ) { index, tab ->
            DraggableItem(dragDropState, tabs.size + index + 2) { isDragging ->
               val isMapped = mapped?.get(tab) ?: false

               NavigationRow(
                  tab = tab,
                  loading = loadingState[tab],
                  isMapped = isMapped,
                  isDragging = isDragging,
                  onMapClicked = {
                     scope.launch {
                        viewModel.toggleOnMap(tab)
                     }
                  },
                  onDestinationClicked = {
                     onDestinationClicked(it.name)
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
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxSize()
                     .background(MaterialTheme.colors.background)
                     .clickable {
                        onDestinationClicked(ReportRoute.Main.name)
                     }
               ) {

                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
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
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                           Text(
                              text = ReportRoute.Main.title,
                              style = MaterialTheme.typography.body2,
                              fontWeight = FontWeight.Medium
                           )
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
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .fillMaxSize()
                     .background(MaterialTheme.colors.background)
                     .clickable {
                        onDestinationClicked(AboutRoute.Main.name)
                     }
               ) {

                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
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
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                           Text(
                              text = AboutRoute.Main.title,
                              style = MaterialTheme.typography.body2,
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

@Composable
private fun NavigationRow(
   tab: DataSource,
   loading: Boolean?,
   isDragging: Boolean = false,
   isMapped: Boolean,
   onMapClicked: (() -> Unit)? = null,
   onDestinationClicked: (route: Route) -> Unit
) {
   val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

   Surface(elevation = elevation) {
      Column(
         Modifier
            .height(56.dp)
            .fillMaxWidth()
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colors.background)
               .clickable {
                  onDestinationClicked(mainRouteFor(tab))
               }
         ) {
            Box(
               modifier = Modifier
                  .width(8.dp)
                  .fillMaxHeight()
                  .background(tab.color)
            )

            if (loading == true) {
               Box(Modifier.padding(start = 8.dp)) {
                  CircularProgressIndicator(
                     color = MaterialTheme.colors.onSurface.copy(alpha = .4f),
                     strokeWidth = 3.dp,
                     modifier = Modifier.size(24.dp)
                  )
               }
            } else {
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                  val bitmap = AppCompatResources.getDrawable(LocalContext.current, tab.icon)!!.toBitmap().asImageBitmap()
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
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                     Text(
                        text = mainRouteFor(tab).title,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium
                     )
                  }

                  if (tab.mappable) {
                     var icon = Icons.Default.LocationOff
                     var iconColor = Color.Black.copy(alpha = ContentAlpha.disabled)
                     if (isMapped) {
                        icon = Icons.Default.LocationOn
                        iconColor = MaterialTheme.colors.primary
                     }

                     IconButton(
                        onClick = { onMapClicked?.invoke() },
                     ) {
                        Box(
                           Modifier
                              .width(24.dp)
                              .height(24.dp)
                              .clip(CircleShape)
                              .background(iconColor)
                        )

                        Icon(
                           icon,
                           contentDescription = "Toggle On Map",
                           tint = Color.White,
                           modifier = Modifier
                              .height(16.dp)
                              .width(16.dp)
                        )
                     }
                  }
               }
            }
         }
      }

      Divider(Modifier.fillMaxWidth())
   }
}