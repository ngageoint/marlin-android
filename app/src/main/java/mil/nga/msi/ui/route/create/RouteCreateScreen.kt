package mil.nga.msi.ui.route.create

import android.Manifest
import android.animation.ValueAnimator
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.VisibleRegion
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.TileOverlayState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberTileOverlayState
import kotlinx.coroutines.launch
import mil.nga.msi.R
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.coordinate.CoordinateText
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.drag.DraggableItem
import mil.nga.msi.ui.drag.dragContainer
import mil.nga.msi.ui.drag.rememberDragDropState
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapPosition
import mil.nga.msi.ui.map.MapViewModel
import mil.nga.msi.ui.map.TileProviderType
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.theme.remove
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteCreateScreen(
    routeId: Long? = null,
    onBack: () -> Unit,
    viewModel: RouteCreateViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
    mapDestination : MapPosition? = null,
    onMapTap: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val layers by mapViewModel.layers.observeAsState(emptyList())
    val mapOrigin by mapViewModel.mapLocation.observeAsState()
    var destination by remember { mutableStateOf(mapDestination) }
    val annotation by mapViewModel.annotationProvider.annotation.observeAsState()
    var located by remember { mutableStateOf(false) }
    val tileProviders by mapViewModel.tileProviders.observeAsState(emptyMap())
    val cameraPositionState = rememberCameraPositionState()
    var origin by remember { mutableStateOf(mapOrigin) }
    if (origin == null) {
        origin = mapOrigin
    }
    val baseMap by mapViewModel.baseMap.observeAsState()

    val routeTileProvider = viewModel.tileProvider
    val routeTileOverlayState = rememberTileOverlayState()
    viewModel.tileOverlayState = routeTileOverlayState

    val route by viewModel.route.observeAsState()

    val locationPermissionState: PermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LocationPermission(locationPermissionState)

    if (locationPermissionState.status.isGranted) {
        viewModel.setLocationEnabled(true)
    }
    var name by remember { mutableStateOf("") }

    LaunchedEffect(routeId) {
        viewModel.setRouteId(routeId)
        name = viewModel.name.value
    }

    val waypoints by viewModel.waypoints.observeAsState(emptyList())
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Create Route",
            navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
            onNavigationClicked = {
                viewModel.clearWaypoints()
                onBack()
            }
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier
                .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    label = { Text("Route Name") },
                    placeholder = { Text(text = "Route Name") },
                    onValueChange = { newText ->
                        name = newText
                        viewModel.setName(name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    Text(
                        text = "Select a feature to add to the route, long press to add custom point, drag to reorder.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    )
                }

                WaypointList(waypoints = waypoints, viewModel = viewModel)

                if (waypoints.count() > 1) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        Text(
                            text = "Total Distance: ${"%.2f".format(route?.distanceNauticalMiles())} nmi",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                        )
                    }
                }

                Box(Modifier.weight(1f)) {
                    Map(
                        origin = origin,
                        destination = destination,
                        baseMap = baseMap,
                        layers = layers,
                        cameraPositionState = cameraPositionState,
                        locationEnabled = locationPermissionState.status.isGranted,
                        tileProviders = tileProviders,
                        routeTileProvider = routeTileProvider,
                        routeTileOverlayState = routeTileOverlayState,
                        annotation = annotation,
                        onMapMove = { position, reason ->
                            if (reason == com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                                located = false
                                destination = null
                            }
                            scope.launch {
                                val mapLocation = MapLocation.newBuilder()
                                    .setLatitude(position.target.latitude)
                                    .setLongitude(position.target.longitude)
                                    .setZoom(position.zoom.toDouble())
                                    .build()

                                mapViewModel.setMapLocation(mapLocation, position.zoom.toInt())
                            }
                        },
                        onMapTap = { latLng, region ->
                            val screenPercentage = 0.04
                            val tolerance = (region.farRight.longitude - region.farLeft.longitude) * screenPercentage
                            val bounds = LatLngBounds(
                                LatLng(latLng.latitude - tolerance, latLng.longitude - tolerance),
                                LatLng(latLng.latitude + tolerance, latLng.longitude + tolerance)
                            )

                            scope.launch {
                                val count = mapViewModel.setTapLocation(latLng, bounds)
                                if (count > 0) { onMapTap() }
                            }
                        },
                        onMapLongClick = { latLng ->
                            viewModel.addUserWaypoint(latLng)
                        }
                    )
                }
                Button(
                    enabled = name.isNotEmpty(),
                    onClick = {
                        scope.launch {
                            viewModel.saveRoute()
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Save Route")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WaypointList(waypoints: List<RouteWaypoint>, viewModel: RouteCreateViewModel) {
    var previousWaypoints = waypoints

    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        previousWaypoints = viewModel.moveWaypoint(fromIndex, toIndex)
    }

    LaunchedEffect(waypoints){
        listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
    }

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .heightIn(max = 175.dp)
            .fillMaxWidth()
            .dragContainer(dragDropState)
            .padding(vertical = 0.dp)
            .padding(start = 16.dp, end = 16.dp)
    ) {
        itemsIndexed(
            previousWaypoints,
            key = { _, waypoint ->
                waypoint.itemKey
            }
        ) { index, waypoint ->
            DraggableItem(dragDropState, index) {

                val positionalThreshold = with(LocalDensity.current) { 150.dp.toPx() }
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.removeWaypoint(waypoint)
                            true
                        } else false
                    },
                    positionalThreshold = { positionalThreshold }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier.animateItemPlacement(),
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true,
                    backgroundContent = {
                        DismissBackground()
                    },
                    content = {
                        WaypointRow(waypoint = waypoint)
                    }
                )
            }
        }
    }
}

@Composable
private fun DismissBackground() {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.remove)
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

@Composable
private fun WaypointRow(waypoint: RouteWaypoint) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(72.dp)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DataSourceIcon(
                dataSource = waypoint.dataSource,
                iconSize = 24
            )
            DataSourceWaypoint(waypoint = waypoint)
        }
    }
}

@Composable
private fun DataSourceWaypoint(
    waypoint: RouteWaypoint
) {
    Column {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            val (title, latLng) = waypoint.getTitleAndCoordinate()
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            latLng?.let {
                CoordinateText(
                    latLng = latLng,
                    onCopiedToClipboard = { }
                )
            }
        }
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun Map(
    origin: MapLocation?,
    destination: MapPosition?,
    baseMap: BaseMapType?,
    layers: List<TileProvider>,
    cameraPositionState: CameraPositionState,
    locationEnabled: Boolean,
    tileProviders: Map<TileProviderType, TileProvider>,
    routeTileProvider: TileProvider?,
    routeTileOverlayState: TileOverlayState,
    annotation: MapAnnotation?,
    onMapMove: (CameraPosition, Int) -> Unit,
    onMapTap: (LatLng, VisibleRegion) -> Unit,
    onMapLongClick: (LatLng) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }

    var cameraMoveReason by remember { mutableIntStateOf(0) }

    LaunchedEffect(origin) {
        origin?.let { origin ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(origin.latitude, origin.longitude), origin.zoom.toFloat())
        }
    }

    val mapStyleOptions = if (isSystemInDarkTheme()) {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_theme_night)
    } else null

    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var selectedAnimator by remember { mutableStateOf<ValueAnimator?>(null) }

    val mgrsTileProvider = tileProviders[TileProviderType.MGRS]
    val garsTileProvider = tileProviders[TileProviderType.GARS]
    val osmTileProvider = tileProviders[TileProviderType.OSM]

    val asamTileProvider = tileProviders[TileProviderType.ASAM]
    val moduTileProvider = tileProviders[TileProviderType.MODU]
    val lightTileProvider = tileProviders[TileProviderType.LIGHT]
    val portTileProvider = tileProviders[TileProviderType.PORT]
    val beaconTileProvider = tileProviders[TileProviderType.RADIO_BEACON]
    val dgpsStationTileProvider = tileProviders[TileProviderType.DGPS_STATION]
    val navigationalWarningTileProvider = tileProviders[TileProviderType.NAVIGATIONAL_WARNING]

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true },
            properties = MapProperties(
                minZoomPreference = 0f,
                mapType = baseMap?.asMapType() ?: BaseMapType.NORMAL.asMapType(),
                isMyLocationEnabled = locationEnabled,
                mapStyleOptions = mapStyleOptions

            ),
            uiSettings = MapUiSettings(
                mapToolbarEnabled = false,
                compassEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            modifier = Modifier.weight(1f)
        ) {
            if (isMapLoaded) {
                osmTileProvider?.let { TileOverlay(tileProvider = it) }

                layers.forEach { TileOverlay(tileProvider = it) }

                mgrsTileProvider?.let { TileOverlay(tileProvider = it) }
                garsTileProvider?.let { TileOverlay(tileProvider = it) }

                asamTileProvider?.let { TileOverlay(tileProvider = it) }
                moduTileProvider?.let { TileOverlay(tileProvider = it) }
                lightTileProvider?.let { TileOverlay(tileProvider = it) }
                portTileProvider?.let { TileOverlay(tileProvider = it) }
                beaconTileProvider?.let { TileOverlay(tileProvider = it) }
                dgpsStationTileProvider?.let { TileOverlay(tileProvider = it) }
                navigationalWarningTileProvider?.let { TileOverlay(tileProvider = it) }
                routeTileProvider?.let { TileOverlay(tileProvider = it, state = routeTileOverlayState) }
            }
            MapEffect(destination, annotation) { map ->
                map.setOnCameraMoveStartedListener { reason ->
                    cameraMoveReason = reason
                }

                map.setOnCameraMoveListener {
                    onMapMove(map.cameraPosition, cameraMoveReason)
                }

                map.setOnMapClickListener { latLng ->
                    onMapTap(latLng, map.projection.visibleRegion)
                }

                map.setOnMapLongClickListener { latLng ->
                    onMapLongClick(latLng)
                }

                if (annotation != null) {
                    if (selectedAnimator != null) {
                        selectedAnimator?.doOnEnd {
                            selectedMarker?.remove()
                            selectedAnimator = ValueAnimator.ofFloat(.5f, 2f)

                            val icon = AppCompatResources.getDrawable(context, annotation.key.type.icon)!!.toBitmap()
                            val position = LatLng(annotation.latitude, annotation.longitude)
                            val options = MarkerOptions()
                                .position(position)
                                .anchor(.5f, .5f)
                                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            selectedMarker = map.addMarker(options)?.apply {
                                tag = annotation
                            }

                            scope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLng(position))
                            }

                            animateAnnotation(selectedMarker, icon, selectedAnimator)
                        }
                        selectedAnimator?.reverse()
                    } else {
                        selectedAnimator = ValueAnimator.ofFloat(.5f, 2f)

                        val icon = AppCompatResources.getDrawable(context, annotation.key.type.icon)!!.toBitmap()
                        val position = LatLng(annotation.latitude, annotation.longitude)
                        val options = MarkerOptions()
                            .position(position)
                            .anchor(.5f, .5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        selectedMarker = map.addMarker(options)?.apply {
                            tag = annotation
                        }
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(position))
                        }

                        animateAnnotation(selectedMarker, icon, selectedAnimator)
                    }
                } else {
                    selectedAnimator?.doOnEnd {
                        selectedMarker?.remove()
                        selectedMarker = null
                    }
                    selectedAnimator?.reverse()
                    selectedAnimator = null
                }
            }
        }
    }
}

private fun animateAnnotation(
    marker: Marker?,
    bitmap: Bitmap,
    animator: ValueAnimator?
) {
    animator?.duration = 500
    animator?.addUpdateListener { animation ->
        val scale = animation.animatedValue as Float
        val sizeX = (bitmap.width * scale).roundToInt()
        val sizeY = (bitmap.height * scale).roundToInt()
        val scaled = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false)

        if (marker?.tag != null) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaled))
        }
    }

    animator?.start()
}