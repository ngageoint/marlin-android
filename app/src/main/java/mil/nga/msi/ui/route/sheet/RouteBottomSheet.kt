package mil.nga.msi.ui.route.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.dgpsstation.sheet.DgpsStationSheetScreen
import mil.nga.msi.ui.light.sheet.LightSheetScreen
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.navigationalwarning.sheet.NavigationalWarningSheetScreen
import mil.nga.msi.ui.port.sheet.PortSheetScreen
import mil.nga.msi.ui.radiobeacon.sheet.RadioBeaconSheetScreen
import mil.nga.msi.ui.sheet.DataSourceSheetViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteBottomSheet(
    viewModel: DataSourceSheetViewModel = hiltViewModel(),
    routeSheetViewModel: RouteBottomSheetViewModel = hiltViewModel(),
    close: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val mapAnnotations by viewModel.mapAnnotations.observeAsState(emptyList())
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        pageCount = { mapAnnotations.size }
    )

    var badgeColor = remember(pagerState.currentPage, mapAnnotations) {
        mapAnnotations.getOrNull(pagerState.currentPage)?.key?.type?.color ?: Color.Transparent
    }

    Row(Modifier.height(400.dp)) {
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(badgeColor)
        )

        Box {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val annotation = mapAnnotations[pagerState.currentPage]
                viewModel.annotationProvider.setMapAnnotation(annotation)
                badgeColor = annotation.key.type.color

                Column(modifier = Modifier.fillMaxWidth()) {
                    when (annotation.key.type) {
                        MapAnnotation.Type.ASAM -> {
                            AsamPage(
                                reference = annotation.key.id,
                                onAddToRoute = { asam ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.ASAM,
                                        itemKey = asam.reference
                                    )
                                    waypoint.json = Json.encodeToString(value = asam)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.MODU -> {
                            ModuPage(
                                name = annotation.key.id,
                                onAddToRoute = { modu ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.MODU,
                                        itemKey = modu.name
                                    )
                                    waypoint.json = Json.encodeToString(value = modu)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.LIGHT -> {
                            LightPage(
                                id = annotation.key.id,
                                onAddToRoute = { light ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.LIGHT,
                                        itemKey = light.id
                                    )
                                    waypoint.json = Json.encodeToString(value = light)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.PORT -> {
                            PortPage(
                                id = annotation.key.id,
                                onAddToRoute = { port ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.PORT,
                                        itemKey = port.portNumber.toString()
                                    )
                                    waypoint.json = Json.encodeToString(value = port)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.RADIO_BEACON -> {
                            RadioBeaconPage(
                                id = annotation.key.id,
                                onAddToRoute = { beacon ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.RADIO_BEACON,
                                        itemKey = beacon.id
                                    )
                                    waypoint.json = Json.encodeToString(value = beacon)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.DGPS_STATION -> {
                            DgpsStationPage(
                                id = annotation.key.id,
                                onAddToRoute = { dgps ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.DGPS_STATION,
                                        itemKey = DgpsStationKey.fromDgpsStation(dgps).id()
                                    )
                                    waypoint.json = Json.encodeToString(value = dgps)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.NAVIGATIONAL_WARNING -> {
                            NavigationWarningPage(
                                id = annotation.key.id,
                                onAddToRoute = { navWarning ->
                                    var waypoint = RouteWaypoint(
                                        dataSource = DataSource.NAVIGATION_WARNING,
                                        itemKey = navWarning.id
                                    )
                                    waypoint.json = Json.encodeToString(value = navWarning)
                                    routeSheetViewModel.addWaypoint(waypoint)
                                    close()
                                }
                            )
                        }
                        MapAnnotation.Type.GEOPACKAGE -> {
                        }
                        MapAnnotation.Type.ROUTE -> {
                        }
                    }
                }
            }

            if (pagerState.pageCount > 1) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    val previousEnabled = pagerState.currentPage > 0
                    IconButton(
                        enabled = previousEnabled,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        })
                    {
                        Icon(
                            Icons.Default.ChevronLeft,
                            tint = if (previousEnabled) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.38f),
                            contentDescription = "Previous Page"
                        )
                    }

                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Text(
                            text = "${pagerState.currentPage + 1} of ${mapAnnotations.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }

                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                        Icon(
                            Icons.Default.ChevronRight,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Next Page"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AsamPage(
    reference: String,
    onAddToRoute: (Asam) -> Unit
) {
    AsamSheetScreen(
        reference,
        modifier = Modifier.fillMaxHeight(),
        onRoute = onAddToRoute,
    )
}

@Composable
private fun ModuPage(
    name: String,
    onAddToRoute: (Modu) -> Unit
) {
    ModuSheetScreen(
        name,
        onRoute = onAddToRoute,
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
private fun LightPage(
    id: String,
    onAddToRoute: (Light) -> Unit
) {
    val key = LightKey.fromId(id)
    LightSheetScreen(
        key,
        onRoute = onAddToRoute,
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
private fun PortPage(
    id: String,
    onAddToRoute: (Port) -> Unit
) {
    id.toIntOrNull()?.let { portNumber ->
        PortSheetScreen(
            portNumber,
            onRoute = onAddToRoute,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
private fun RadioBeaconPage(
    id: String,
    onAddToRoute: (RadioBeacon) -> Unit
) {
    val key = RadioBeaconKey.fromId(id)
    RadioBeaconSheetScreen(
        key = key,
        onRoute = onAddToRoute,
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
private fun DgpsStationPage(
    id: String,
    onAddToRoute: (DgpsStation) -> Unit
) {
    DgpsStationSheetScreen(
        key =  DgpsStationKey.fromId(id),
        onRoute = onAddToRoute,
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
private fun NavigationWarningPage(
    id: String,
    onAddToRoute: (NavigationalWarning) -> Unit
) {
    val key = NavigationalWarningKey.fromId(id)
    NavigationalWarningSheetScreen(
        key,
        onRoute = onAddToRoute,
        modifier = Modifier.fillMaxHeight()
    )
}
