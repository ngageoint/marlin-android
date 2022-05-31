package mil.nga.msi.ui.map

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
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch
import mil.nga.msi.TopBar
import mil.nga.msi.R
import mil.nga.msi.datasource.asam.AsamMapItem

@Composable
fun MapScreen(
   onAsam: (String) -> Unit,
   openDrawer: () -> Unit,
   viewModel: MapViewModel = hiltViewModel()
) {
   val asams by viewModel.asams.observeAsState()
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "Map",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )
      Map(
         asams,
         onMarkerClick = { id ->
            onAsam(id)
         }
      )
   }
}

@Composable
fun Map(
   asams: List<AsamMapItem>?,
   onMarkerClick: (String) -> Unit,
) {
   val scope = rememberCoroutineScope()
   val map = rememberMapViewWithLifecycle()
   var mapInitialized by remember(map) { mutableStateOf(false) }
   LaunchedEffect(map, mapInitialized) {
      if (!mapInitialized) {
         val googleMap = map.awaitMap()
         googleMap.uiSettings.isMapToolbarEnabled = false
         googleMap.setOnMarkerClickListener { marker ->
            val id = marker.tag as? String
            id?.let { onMarkerClick(it) }
            false
         }

         mapInitialized = true
      }
   }

   AndroidView({ map }) { mapView ->
      scope.launch {
         val googleMap = mapView.awaitMap()
         googleMap.clear()

         addAsams(googleMap, asams)
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
      marker?.tag = asam.id
   }
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