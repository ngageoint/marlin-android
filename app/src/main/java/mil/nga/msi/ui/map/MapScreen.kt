package mil.nga.msi.ui.map

import android.Manifest
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.cluster.ClusterManager
import mil.nga.msi.ui.map.cluster.MapAnnotation
import kotlin.math.roundToInt

// TODO better way to detect individual tile provider change
// TODO ASAM and MODU icons as tile images

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
   selectedAnnotation: MapAnnotation?,
   mapDestination : MapLocation? = null,
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit,
   onMapSettings: () -> Unit,
   openDrawer: () -> Unit,
   mapViewModel: MapViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val baseMap by mapViewModel.baseMap.observeAsState()
   val mapOrigin by mapViewModel.mapLocation.observeAsState()
   var destination by remember { mutableStateOf(mapDestination) }
   val annotations by mapViewModel.mapAnnotations.observeAsState(emptyList())
   val location by mapViewModel.locationPolicy.bestLocationProvider.observeAsState()
   var located by remember { mutableStateOf(false) }
   val tileProviders by mapViewModel.tileProviders.observeAsState(emptySet())

   val locationPermissionState: PermissionState = rememberPermissionState(
      Manifest.permission.ACCESS_FINE_LOCATION
   )

   LocationPermission(locationPermissionState)

   if (locationPermissionState.status.isGranted) {
      mapViewModel.locationPolicy.requestLocationUpdates()
   }

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

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "Map",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      Box(Modifier.fillMaxWidth()) {
         Map(
            selectedAnnotation,
            origin,
            destination,
            baseMap,
            locationSource,
            locationPermissionState.status.isGranted,
            tileProviders,
            annotations,
            onAnnotationClick = { onAnnotationClick.invoke(it) },
            onAnnotationsClick = { onAnnotationsClick.invoke(it) },
            onMapMove = { position, reason ->
               if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
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
   selectedAnnotation: MapAnnotation?,
   origin: MapLocation?,
   destination: MapLocation?,
   baseMap: BaseMapType?,
   locationSource: LocationSource,
   locationEnabled: Boolean,
   tileProviders: Set<TileProvider>,
   annotations: List<MapAnnotation>,
   onMapMove: (CameraPosition, Int) -> Unit,
   onMapClick: (LatLng, Float, VisibleRegion) -> Unit,
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit
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

   var previousAnnotations by remember { mutableStateOf(listOf<MapAnnotation>()) }

   var selectedMarker by remember { mutableStateOf<Marker?>(null) }
   var selectedAnimator by remember { mutableStateOf<ValueAnimator?>(null) }

   if (selectedAnnotation == null) {
      selectedAnimator?.doOnEnd {
         selectedMarker?.remove()
         selectedMarker = null
      }
      selectedAnimator?.reverse()
      selectedAnimator = null
   }

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
      val context = LocalContext.current
      var clusterManager by remember { mutableStateOf<ClusterManager?>(null)}

      if (isMapLoaded) {
         LaunchedEffect(destination) {
            destination?.let { destination ->
               scope.launch {
                  val update = CameraUpdateFactory.newLatLngZoom(LatLng(destination.latitude, destination.longitude), destination.zoom.toFloat())
                  cameraPositionState.animate(update)
               }
            }
         }

         tileProviders.forEach { TileOverlay(tileProvider = it ) }
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

      MapEffect(annotations) { map ->
         if (clusterManager == null) {
            clusterManager = ClusterManager(context, map).apply {
               setOnClusterItemClickListener { item ->
                  onAnnotationClick(item)
                  selectedAnimator = ValueAnimator.ofFloat(1f, 2f)
                  selectedMarker = map.addMarker(MarkerOptions().apply {
                     item.key.type.icon?.let { icon ->
                        icon(BitmapDescriptorFactory.fromResource(icon))
                     }
                     position(item.position)
                  })
                  selectedMarker?.tag = item.key

                  item.key.type.icon?.let { icon ->
                     val bitmap = BitmapFactory.decodeResource(context.resources, icon)
                     animateAnnotation(selectedMarker, selectedAnimator, bitmap)
                  }

                  animateMap(map, item.position)
                  true
               }

               setOnClusterClickListener { cluster ->
                  if (map.maxZoomLevel == map.cameraPosition.zoom) {
                     onAnnotationsClick(cluster.items)
                  } else {
                     val builder = LatLngBounds.Builder()
                     cluster.items.forEach { builder.include(it.position) }
                     map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20))
                  }
                  true
               }
            }
         }

         var updateCluster = false
         clusterManager?.let { manager ->
            val annotationSet = sortedSetOf(MapAnnotation.idComparator, *annotations.toTypedArray())
            val previousAnnotationSet = sortedSetOf(MapAnnotation.idComparator, *previousAnnotations.toTypedArray())

            // add new
            annotationSet.minus(previousAnnotationSet).forEach {
               manager.addItem(it)
               updateCluster = true
            }

            // update existing
            annotationSet.intersect(previousAnnotationSet).forEach { annotation ->
               clusterManager?.getClusterItem(annotation.key)?.let {
                  manager.removeItem(it)
                  manager.addItem(annotation)
               }

               updateCluster = true
            }

            // remove old
            previousAnnotationSet.minus(annotationSet).forEach {
               manager.removeItem(it)
               updateCluster = true
            }

            if (updateCluster) {
               manager.cluster()
               previousAnnotations = annotations
            }
         }
      }
   }
}

private fun animateMap(map: GoogleMap, latLng: LatLng) {
   val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
   map.animateCamera(cameraUpdate)
}

private fun animateAnnotation(
   marker: Marker?,
   animator: ValueAnimator?,
   bitmap: Bitmap
) {
   animator?.duration = 500
   animator?.addUpdateListener { animation ->
      val scale = animation.animatedValue as Float
      val sizeX = (bitmap.width * scale).roundToInt()
      val sizeY = (bitmap.height * scale).roundToInt()
      val scaled =  Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false)

      if (marker?.tag != null) {
         marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaled))
      }
   }
   animator?.start()
}