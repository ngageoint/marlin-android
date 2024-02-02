package mil.nga.msi.ui.route.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWithWaypoints
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.RouteAction
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.route.RouteSummary
import mil.nga.msi.ui.theme.remove

@Composable
fun RoutesScreen(
    openDrawer: () -> Unit,
    onCreate: () -> Unit,
    onAction: (Action) -> Unit,
    viewModel: RoutesViewModel = hiltViewModel()
) {
    val routes by viewModel.routes.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar (
            title = "Routes",
            navigationIcon = Icons.Default.Menu,
            onNavigationClicked = { openDrawer() }
        )

        if (routes.isEmpty()) {
            EmptyState(onCreate)
        } else {
            Routes(
                routes = routes,
                onTap = { onAction(RouteAction.Tap(it))},
                onCreate = onCreate,
                onDelete = {
                    runBlocking { viewModel.delete(it) }
                }
            )
        }
    }
}

@Composable
private fun EmptyState(
    onCreate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                Icon(
                    Icons.Outlined.Directions,
                    modifier = Modifier
                        .size(220.dp)
                        .padding(bottom = 32.dp),
                    contentDescription = "Route icon"
                )

                Text(
                    text = "No Routes",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Create routes between Marlin features for navigation planning.  Routes you create will appear here.",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )

                ExtendedFloatingActionButton(
                    onClick = { onCreate() },
                    icon = { Icon(Icons.Outlined.Directions, "Route") },
                    text = { Text(text = "Create Route") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Routes(
    routes: List<RouteWithWaypoints>,
    onTap: (Route) -> Unit,
    onCreate: () -> Unit,
    onDelete: (Route) -> Unit
) {
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxWidth()) {
        Surface(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                items(
                    count = routes.count(),
                    key = {
                        val route = routes[it]
                        "${route.route.id}"
                    }
                ) { index ->
                    val route = routes[index]

                    val dismissState = rememberDismissState(
                        confirmValueChange = {
                            if (it == DismissValue.DismissedToStart) {
                                onDelete(route.route)
                                true
                            } else false
                        }, positionalThreshold = { 150.dp.toPx() }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier
                            .animateItemPlacement(),
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            DismissBackground(dismissState = dismissState)
                        },
                        dismissContent = {
                            Box {
                                RouteCard(
                                    route = route,
                                    onTap = { onTap(route.route) }
                                )
                            }
                        }
                    )
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { onCreate() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Outlined.Directions, "Route")
            }
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DismissBackground(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.remove
        }, label = "color_state_animator"
    )

    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
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
}

@Composable
private fun RouteCard(
    route: RouteWithWaypoints,
    onTap: () -> Unit,
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap() }
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            RouteSummary(
                route,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}