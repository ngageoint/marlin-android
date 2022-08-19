package mil.nga.msi.ui.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
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

   val tabsPreference by viewModel.tabs.observeAsState()
   val nonTabsPreference by viewModel.nonTabs.observeAsState()

   var previousTabs by remember { mutableStateOf<List<DataSource>?>(null) }
   var previousNonTabs by remember { mutableStateOf<List<DataSource>?>(null) }
   if (previousTabs == null) { previousTabs = tabsPreference }
   if (previousNonTabs == null) { previousNonTabs = nonTabsPreference }

   val listState = rememberLazyListState()
   val dragDropState = rememberDragDropState(listState) { from, to ->
      val tabs = previousTabs
      val nonTabs = previousNonTabs

      if (tabs == null || nonTabs == null) { return@rememberDragDropState }
      if (to == 0) { return@rememberDragDropState }

      if (from <= tabs.size && to <= tabs.size) {
         // Drag within list1
         previousTabs = tabs.toMutableList().apply {
            add(if (to == 0) to else to - 1, removeAt(if (from == 0) from else from - 1))
         }
         previousTabs?.let { viewModel.setTabs(it) }
      } else if (from <= tabs.size && to > tabs.size) {
         // Drag from list1 to list2
         val list1Element = tabs[from - 1]
         previousTabs = tabs.toMutableList().apply { removeLast() }
         previousNonTabs = nonTabs.toMutableList().apply {
            add(to - tabs.size - 1, list1Element)
         }

         previousTabs?.let { viewModel.setTabs(it) }
         previousNonTabs?.let { viewModel.setNonTabs(it) }
      } else if (from >= tabs.size - 2 && to <= tabs.size + 1) {
         // Drag from list2 to list1
         val list2Element = nonTabs[from - tabs.size - 2]

         previousNonTabs = nonTabs.toMutableList().apply { removeFirst() }
         previousTabs = tabs.toMutableList().apply { add(lastIndex + 1, list2Element) }

         val size = previousTabs?.size ?: 0
         if (size > MAX_TABS) {
            val evict = tabs[tabs.lastIndex - 1]
            previousTabs = previousTabs?.toMutableList()?.apply { remove(evict) }
            previousNonTabs = previousNonTabs?.toMutableList()?.apply { add(0, evict) }
         }

         previousTabs?.let { viewModel.setTabs(it) }
         previousNonTabs?.let { viewModel.setNonTabs(it) }
      } else if (from <= tabs.size + nonTabs.size + 1 && to <= tabs.size + nonTabs.size + 1) {
         // Drag within list2
         previousNonTabs = nonTabs.toMutableList().apply {
            add(to - tabs.size - 2, removeAt(from - tabs.size - 2))
         }

         previousNonTabs?.let { viewModel.setNonTabs(it) }
      }
   }

   val tabs = previousTabs
   val nonTabs = previousNonTabs
   if (tabs != null && nonTabs != null) {
      LazyColumn(
         state = listState,
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
                  modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
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
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = "Other Data Sources (Drag to add to tabs)",
                  style = MaterialTheme.typography.body2,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
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
      }
   }
}

@Composable
private fun NavigationRow(
   tab: DataSource,
   isDragging: Boolean = false,
   isMapped: Boolean,
   onMapClicked: (() -> Unit)? = null,
   onDestinationClicked: (route: Route) -> Unit
) {
   val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

   Surface(elevation = elevation) {
      Column(
         Modifier
            .height(72.dp)
            .fillMaxWidth()
      ) {
         Row(
            modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colors.background)
               .clickable {
                  onDestinationClicked(tab.route)
               }
         ) {
            Box(
               modifier = Modifier
                  .width(6.dp)
                  .fillMaxHeight()
                  .background(tab.color)
            )

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
                     .padding(horizontal = 16.dp)
               ) {
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                     Text(
                        text = tab.route.title,
                        style = MaterialTheme.typography.body1,
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

               Divider(Modifier.fillMaxWidth())
            }
         }
      }
   }
}