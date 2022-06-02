package mil.nga.msi.ui.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch
import mil.nga.msi.TopBar
import mil.nga.msi.R
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import kotlin.math.roundToInt

var markerAnimator: ValueAnimator? = null

@Composable
fun MapScreen(
   onAnnotationClick: (Annotation) -> Unit,
   openDrawer: () -> Unit,
   viewModel: MapViewModel = hiltViewModel()
) {
   val asams by viewModel.asams.observeAsState()
   val modus by viewModel.modus.observeAsState()
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "Map",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )
      Map(
         asams,
         modus,
         onAnnotationClick = { annotation ->
            onAnnotationClick(annotation)
         }
      )
   }

//   if (nav.navigatorSheetState.currentValue == ModalBottomSheetValue.Hidden) {
////      markerAnimator?.reverse()
////      markerAnimator = null
//   }
}

@Composable
private fun Map(
   asams: List<AsamMapItem>?,
   modus: List<ModuMapItem>?,
   onAnnotationClick: (Annotation) -> Unit,
) {
   val scope = rememberCoroutineScope()
   val mapView = rememberMapViewWithLifecycle()
//   var previousAsams by remember { mutableStateOf(asams)}
   var mapInitialized by remember(mapView) { mutableStateOf(false) }
   LaunchedEffect(mapView, mapInitialized) {
      if (!mapInitialized) {
         val googleMap = mapView.awaitMap()
         googleMap.uiSettings.isMapToolbarEnabled = false
         googleMap.setOnMarkerClickListener { marker ->
            val annotation = marker.tag as? Annotation
            annotation?.let {
               onAnnotationClick(it)
            }
//            animateMarker(marker, context)
//            animateMap(googleMap, marker.position)

            true
         }

         // TODO this really needs to happen when bottom sheet goes away
//         googleMap.setOnMapClickListener {
//            markerAnimator?.reverse()
//            markerAnimator = null
//         }

         mapInitialized = true
      }
   }

   AndroidView({ mapView }) { mapView ->
      // TODO if anything changes here the entire map is recomposed
      scope.launch {
         val googleMap = mapView.awaitMap()
         googleMap.uiSettings.isMapToolbarEnabled = false
         googleMap.clear()

         addAsams(googleMap, asams)
         addModus(googleMap, modus)
      }
   }
}

private fun addAsams(
   map: GoogleMap,
   asams: List<AsamMapItem>?
) {
   asams?.forEach { asam ->
      val point = LatLng(asam.latitude, asam.longitude)
      val marker = map.addMarker {
         position(point)
         icon(BitmapDescriptorFactory.fromResource(R.drawable.asam_map_marker_24dp ))
      }
      marker?.tag = Annotation(Annotation.Type.ASAM, asam.reference)
   }
}

private fun addModus(
   map: GoogleMap,
   modus: List<ModuMapItem>?
) {
   modus?.forEach { modu ->
      val point = LatLng(modu.latitude, modu.longitude)
      val marker = map.addMarker {
         position(point)
         icon(BitmapDescriptorFactory.fromResource(R.drawable.modu_map_marker_24dp ))
      }
      marker?.tag = Annotation(Annotation.Type.MODU, modu.name)
   }
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
fun rememberMapViewWithLifecycle(): MapView {
   val context = LocalContext.current
   val mapView = remember {
      MapView(context)
   }

   // Makes MapView follow the lifecycle of this composable
   val lifecycleObserver = rememberMapLifecycleObserver(mapView)
   val lifecycle = LocalLifecycleOwner.current.lifecycle
   DisposableEffect(lifecycle) {
      lifecycle.addObserver(lifecycleObserver)
      onDispose {
         lifecycle.removeObserver(lifecycleObserver)
      }
   }

   return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
   remember(mapView) {
      LifecycleEventObserver { _, event ->
         when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
         }
      }
   }