package mil.nga.msi.ui.route.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.route.RouteWithWaypoints
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.route.RouteSummary

@OptIn(ExperimentalMaterial3Api::class)
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
                onAction = {action ->

                },
                onCreate = onCreate
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Routes(
    routes: List<RouteWithWaypoints>,
    onAction: (Action) -> Unit,
    onCreate: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Surface(Modifier.fillMaxSize()) {
            LazyColumn(
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
                    Box(Modifier.animateItemPlacement()) {
                        RouteCard(
                            route = route,
                            onAction = onAction
                        )
                    }
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
private fun RouteCard(
    route: RouteWithWaypoints,
//    onTap: () -> Unit,
    onAction: (Action) -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
//            .clickable { onTap() }
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            RouteSummary(
                route,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}