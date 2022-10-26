package mil.nga.msi.ui.map

import android.Manifest
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.cluster.MapAnnotation
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
   mapDestination : MapLocation? = null,
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit,
   onMapSettings: () -> Unit,
   openFilter: () -> Unit,
   openDrawer: () -> Unit,
   mapViewModel: MapViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val fetching by mapViewModel.fetching.observeAsState(emptyMap())
   var fetchingVisibility by rememberSaveable { mutableStateOf(true) }
   val baseMap by mapViewModel.baseMap.observeAsState()
   val mapOrigin by mapViewModel.mapLocation.observeAsState()
   var destination by remember { mutableStateOf(mapDestination) }
   val location by mapViewModel.locationPolicy.bestLocationProvider.observeAsState()
   var located by remember { mutableStateOf(false) }
   val tileProviders by mapViewModel.tileProviders.observeAsState(emptyMap())

   LaunchedEffect(fetching) {
      if(fetching.none { it.value } && fetchingVisibility) {
         delay(1.seconds)
         fetchingVisibility = false
      }
   }

   val locationPermissionState: PermissionState = rememberPermissionState(
      Manifest.permission.ACCESS_FINE_LOCATION
   )

   LocationPermission(locationPermissionState)

   var origin by remember { mutableStateOf(mapOrigin) }
   if (origin == null) {
      origin = mapOrigin
   }

   val locationSource = object : LocationSource {
      override fun activate(listener: LocationSource.OnLocationChangedListener) {
         location?.let { listener.onLocationChanged(it) }
      }

      override fun deactivate() {}
   }

   Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()
   ) {
      TopBar(
         title = "Map",
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openFilter() } ) {
               Icon(Icons.Default.FilterList, contentDescription = "Filter Map")
            }
         }
      )

      Box(Modifier.fillMaxWidth()) {
         Map(
            origin,
            destination,
            baseMap,
            locationSource,
            locationPermissionState.status.isGranted,
            tileProviders,
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
            onMapClick = { latLng, zoom, region ->
               val screenPercentage = 0.03
               val tolerance = (region.farRight.longitude - region.farLeft.longitude) * screenPercentage
               scope.launch {
                  val mapAnnotations = mapViewModel.getMapAnnotations(
                     minLongitude = latLng.longitude - tolerance,
                     maxLongitude = latLng.longitude + tolerance,
                     minLatitude = latLng.latitude - tolerance,
                     maxLatitude = latLng.latitude + tolerance
                  )

                  if (mapAnnotations.isNotEmpty()) {
                     val bounds = LatLngBounds.builder().apply {
                        mapAnnotations.forEach { this.include(LatLng(it.latitude, it.longitude)) }
                     }.build()

                     destination = MapLocation.newBuilder()
                        .setLatitude(bounds.center.latitude)
                        .setLongitude(bounds.center.longitude)
                        .setZoom(if (mapAnnotations.size == 1) 17.0 else zoom.toDouble())
                        .build()

                     if (mapAnnotations.size == 1) {
                        onAnnotationClick(mapAnnotations.first())
                     } else {
                        onAnnotationsClick(mapAnnotations)
                     }
                  }
               }
            }
         )

         androidx.compose.animation.AnimatedVisibility(
            visible = fetchingVisibility,
            exit = fadeOut()
         ) {
            Column(
               modifier = Modifier
                  .fillMaxSize()
                  .padding(top = 16.dp)
            ) {
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .align(Alignment.CenterHorizontally)
                     .height(40.dp)
                     .clip(RoundedCornerShape(20.dp))
                     .background(MaterialTheme.colors.primary)
                     .padding(horizontal = 16.dp)
               ) {
                  Box(Modifier.align(Alignment.CenterVertically)) {
                     CircularProgressIndicator(
                        color = MaterialTheme.colors.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                           .padding(end = 8.dp)
                           .size(18.dp)
                     )
                  }

                  Text(
                     text = "Loading Data",
                     style = MaterialTheme.typography.body2,
                     color = MaterialTheme.colors.onPrimary)
               }
            }
         }

         FloatingActionButton(
            onClick = { onMapSettings() },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
               .align(Alignment.TopEnd)
               .padding(16.dp)
               .size(40.dp)
         ) {
            Icon(Icons.Outlined.Map,
               tint = MaterialTheme.colors.secondary,
               contentDescription = "Map Settings"
            )
         }

         if (locationPermissionState.status.isGranted) {
            FloatingActionButton(
               onClick = {
                  location?.let {
                     located = true
                     scope.launch {
                        destination = MapLocation.newBuilder()
                           .setLatitude(it.latitude)
                           .setLongitude(it.longitude)
                           .setZoom(17.0)
                           .build()
                     }
                  }
               },
               backgroundColor = MaterialTheme.colors.background,
               modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp)
            ) {
               var icon = Icons.Outlined.LocationSearching
               var tint =  MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
               if (located) {
                  icon = Icons.Outlined.MyLocation
                  tint = MaterialTheme.colors.secondary
               }
               Icon(
                  imageVector = icon,
                  tint = tint,
                  contentDescription = "Zoom to location"
               )
            }
         }
      }
   }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun Map(
   origin: MapLocation?,
   destination: MapLocation?,
   baseMap: BaseMapType?,
   locationSource: LocationSource,
   locationEnabled: Boolean,
   tileProviders: Map<TileProviderType, TileProvider>,
   onMapMove: (CameraPosition, Int) -> Unit,
   onMapClick: (LatLng, Float, VisibleRegion) -> Unit
) {
   val scope = rememberCoroutineScope()

   var isMapLoaded by remember { mutableStateOf(false) }
   val cameraPositionState: CameraPositionState = rememberCameraPositionState {}
   var cameraMoveReason by remember { mutableStateOf(0) }

   LaunchedEffect(origin) {
      origin?.let { origin ->
         cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(origin.latitude, origin.longitude), origin.zoom.toFloat())
      }
   }

   val mgrsTileProvider = tileProviders[TileProviderType.MGRS]
   val garsTileProvider = tileProviders[TileProviderType.GARS]
   val osmTileProvider = tileProviders[TileProviderType.OSM]
   val asamTileProvider = tileProviders[TileProviderType.ASAM]
   val moduTileProvider = tileProviders[TileProviderType.MODU]
   val lightTileProvider = tileProviders[TileProviderType.LIGHT]
   val portTileProvider = tileProviders[TileProviderType.PORT]
   val beaconTileProvider = tileProviders[TileProviderType.RADIO_BEACON]
   val dgpsStationTileProvider = tileProviders[TileProviderType.DGPS_STATION]

   GoogleMap(
      cameraPositionState = cameraPositionState,
      onMapLoaded = { isMapLoaded = true },
      properties = MapProperties(
         minZoomPreference = 0f,
         mapType = baseMap?.asMapType() ?: BaseMapType.NORMAL.asMapType(),
         isMyLocationEnabled = locationEnabled
      ),
      uiSettings = MapUiSettings(
         mapToolbarEnabled = false,
         compassEnabled = false,
         zoomControlsEnabled = false,
         myLocationButtonEnabled = false
      ),
      locationSource = locationSource
   ) {
      if (isMapLoaded) {
         LaunchedEffect(destination) {
            destination?.let { destination ->
               scope.launch {
                  val update = CameraUpdateFactory.newLatLngZoom(LatLng(destination.latitude, destination.longitude), destination.zoom.toFloat())
                  cameraPositionState.animate(update)
               }
            }
         }
         mgrsTileProvider?.let { TileOverlay(tileProvider = it)}
         garsTileProvider?.let { TileOverlay(tileProvider = it)}
         osmTileProvider?.let { TileOverlay(tileProvider = it)}
         asamTileProvider?.let { TileOverlay(tileProvider = it)}
         moduTileProvider?.let { TileOverlay(tileProvider = it)}
         lightTileProvider?.let { TileOverlay(tileProvider = it)}
         portTileProvider?.let { TileOverlay(tileProvider = it)}
         beaconTileProvider?.let { TileOverlay(tileProvider = it)}
         dgpsStationTileProvider?.let { TileOverlay(tileProvider = it)}
      }

      MapEffect(null) { map ->
         map.setOnCameraMoveStartedListener { reason ->
            cameraMoveReason = reason
         }

         map.setOnCameraMoveListener {
            onMapMove(map.cameraPosition, cameraMoveReason)
         }

         map.setOnMapClickListener { latLng ->
            onMapClick(latLng, map.cameraPosition.zoom, map.projection.visibleRegion)
         }
      }
   }
}