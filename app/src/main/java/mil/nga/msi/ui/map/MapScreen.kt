package mil.nga.msi.ui.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Map
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.awaitMap
import mil.nga.msi.R
import mil.nga.msi.TopBar
import mil.nga.msi.ui.map.overlay.OsmTileProvider
import kotlin.math.roundToInt

var markerAnimator: ValueAnimator? = null

@Composable
fun MapScreen(
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit,
   onMapSettings: () -> Unit,
   openDrawer: () -> Unit,
   viewModel: MapViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val annotations by viewModel.mapAnnotations.observeAsState()
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "Map",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      Box(Modifier.fillMaxWidth()) {
         annotations?.let { annotations ->
            Map(
               baseMap,
               annotations,
               onAnnotationClick = { onAnnotationClick.invoke(it) },
               onAnnotationsClick = { onAnnotationsClick.invoke(it) }
            )
         }

         FloatingActionButton(
            onClick = { onMapSettings() },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier
               .align(Alignment.TopEnd)
               .padding(16.dp)
         ) {
            Icon(Icons.Outlined.Map,
               tint = MaterialTheme.colors.secondary,
               contentDescription = "Map Settings"
            )
         }
      }

   }

//   if (nav.navigatorSheetState.currentValue == ModalBottomSheetValue.Hidden) {
////      markerAnimator?.reverse()
////      markerAnimator = null
//   }
}

@Composable
private fun Map(
   baseMap: BaseMapType?,
   annotations: List<MapAnnotation>,
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit
) {
   val context = LocalContext.current
   val mapView = remember { MapView(context) }
   var previousAnnotations by remember { mutableStateOf(listOf<MapAnnotation>()) }
   var clusterManager by remember { mutableStateOf<ClusterManager?>(null)}

   AndroidView(factory = { mapView })
   MapLifecycle(mapView)

   LaunchedEffect(mapView, annotations) {
      if (clusterManager == null) {
         val map = mapView.awaitMap().apply {
            uiSettings.isMapToolbarEnabled = false
            mapType = baseMap?.value ?: BaseMapType.NORMAL.value
            if (baseMap == BaseMapType.OSM) {
               addTileOverlay(TileOverlayOptions().tileProvider(OsmTileProvider()))
            }
         }
         clusterManager = getClusterManager(context, map, onAnnotationsClick, onAnnotationClick)
      }

      var updateCluster = false
      val manager = clusterManager
      if (manager != null) {
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

//   LaunchedEffect(clusterManager, annotations) {
//      var updateCluster = false
//      val manager = clusterManager
//      if (manager != null) {
//         val annotationSet = sortedSetOf(MapAnnotation.idComparator, *annotations.toTypedArray())
//         val previousAnnotationSet = sortedSetOf(MapAnnotation.idComparator, *previousAnnotations.toTypedArray())
//         Log.i("Billy", "annotationSet size ${annotationSet.size}")
//         Log.i("Billy", "previousAnnotations size ${previousAnnotationSet.size}")
//
//         // add new
//         annotationSet.minus(previousAnnotationSet).forEach {
//            manager.addItem(it)
//            updateCluster = true
//         }
//
//         // update existing
//         annotationSet.intersect(previousAnnotationSet).forEach { annotation ->
//            clusterManager?.getClusterItem(annotation.key)?.let {
//               manager.removeItem(it)
//               manager.addItem(annotation)
//            }
//
//            updateCluster = true
//         }
//
//         // remove old
//         previousAnnotationSet.minus(annotationSet).forEach {
//            manager.removeItem(it)
//            updateCluster = true
//         }
//
//         if (updateCluster) {
//            Log.i("Billy", "cluster annotations: ${clusterManager?.algorithm?.items?.size}")
//
//            manager.cluster()
//            previousAnnotations = annotations
//         }
//      }
//   }
}

private fun getClusterManager(
   context: Context,
   map: GoogleMap,
   onClusterClick: (Collection<MapAnnotation>) -> Unit,
   onClusterItemClick: (MapAnnotation) -> Unit
): ClusterManager {
   val clusterManager = ClusterManager(context, map)
   clusterManager.setOnClusterItemClickListener { item ->
      onClusterItemClick(item)
      true
   }
   clusterManager.setOnClusterClickListener { cluster ->
      if (map.maxZoomLevel == map.cameraPosition.zoom) {
         onClusterClick(cluster.items)
      } else {
         val builder = LatLngBounds.Builder()
         cluster.items.forEach { builder.include(it.position) }
         map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20))
      }
      true
   }
   return clusterManager
}

private fun animateMap(map: GoogleMap, latLng: LatLng) {
//   val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
//   map.animateCamera(cameraUpdate)
}

private fun animateMarker(marker: Marker, context: Context) {
   val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.asam_map_marker_24dp)

   val animator = ValueAnimator.ofFloat(1f, 2f)
   animator.duration = 500
   animator.addUpdateListener { animation ->
      val scale = animation.animatedValue as Float
      val sizeX = (bitmap.width * scale).roundToInt()
      val sizeY = (bitmap.height * scale).roundToInt()
      val scaled =  Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false)

      if (marker.tag != null) {
         marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaled))
      }
   }
   animator.start()
   markerAnimator = animator
}

@Composable
private fun MapLifecycle(mapView: MapView) {
   val context = LocalContext.current
   val lifecycle = LocalLifecycleOwner.current.lifecycle
   DisposableEffect(context, lifecycle, mapView) {
      val mapLifecycleObserver = mapView.lifecycleObserver()
      lifecycle.addObserver(mapLifecycleObserver)
      onDispose {
         lifecycle.removeObserver(mapLifecycleObserver)
      }
   }
}

private fun MapView.lifecycleObserver(): LifecycleEventObserver =
   LifecycleEventObserver { _, event ->
      when (event) {
         Lifecycle.Event.ON_CREATE -> this.onCreate(Bundle())
         Lifecycle.Event.ON_START -> this.onStart()
         Lifecycle.Event.ON_RESUME -> this.onResume()
         Lifecycle.Event.ON_PAUSE -> this.onPause()
         Lifecycle.Event.ON_STOP -> this.onStop()
         Lifecycle.Event.ON_DESTROY -> this.onDestroy()
         else -> throw IllegalStateException()
      }
   }
