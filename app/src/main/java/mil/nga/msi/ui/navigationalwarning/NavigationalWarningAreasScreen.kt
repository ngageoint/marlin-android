package mil.nga.msi.ui.navigationalwarning

import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningGroup
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GeoPackageTileProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NavigationalWarningGroupScreen(
   openDrawer: () -> Unit,
   onGroupTap: (NavigationArea) -> Unit,
   viewModel: NavigationalWarningAreasViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val geoPackageTileProvider = viewModel.naturalEarthTileProvider
   val warningsByArea by viewModel.navigationalWarningsByArea.observeAsState(emptyList())
   val location by viewModel.locationProvider.observeAsState()

   val locationPermissionState: PermissionState = rememberPermissionState(
      android.Manifest.permission.ACCESS_FINE_LOCATION
   )

   LocationPermission(locationPermissionState)

   if (locationPermissionState.status.isGranted) {
      viewModel.setLocationEnabled(true)
   }

   Column {
      TopBar(
         title = NavigationWarningRoute.Main.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      NavigationAreaMap(
         location,
         locationPermissionState,
         geoPackageTileProvider
      )

      // TODO Current nav area should be on top
      val sortedAreas = warningsByArea.sortedBy { it.navigationArea.title }.toMutableList()
      Column(Modifier.verticalScroll(scrollState)) {
         sortedAreas.forEach { group ->
            NavigationalWarnings(group) {
               onGroupTap(group.navigationArea)
            }

            Divider()
         }
      }
   }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NavigationAreaMap(
   location: Location?,
   locationPermissionState: PermissionState,
   tileProvider: GeoPackageTileProvider
) {
   val cameraPositionState = rememberCameraPositionState {}

   var currentLocation by remember { mutableStateOf<Location?>(null) }
   LaunchedEffect(location) {
      if (currentLocation == null) {
         location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 0f)
         }
      }

      currentLocation = location
   }

   val locationSource = object : LocationSource {
      override fun activate(listener: LocationSource.OnLocationChangedListener) {
         location?.let { listener.onLocationChanged(it) }
      }

      override fun deactivate() {}
   }

   GoogleMap(
      modifier = Modifier
         .height(250.dp)
         .fillMaxWidth(),
      cameraPositionState = cameraPositionState,
      properties = MapProperties(
         mapType = MapType.NONE,
         minZoomPreference = 0f,
         isMyLocationEnabled = locationPermissionState.status.isGranted
      ),
      uiSettings = MapUiSettings(
         compassEnabled = false,
         zoomControlsEnabled = false,
         myLocationButtonEnabled = false
      ),
      locationSource = locationSource
   ) {
      TileOverlay(tileProvider)
   }
}

@Composable
private fun NavigationalWarnings(
   group: NavigationalWarningGroup,
   onGroupTap: () -> Unit
) {
   Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onGroupTap() }
         .padding(horizontal = 16.dp)
   ) {
      Column(modifier = Modifier.padding(vertical = 16.dp)) {
         Text(
            text = group.navigationArea.title,
            style = MaterialTheme.typography.subtitle2
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "${group.total} Active",
               style = MaterialTheme.typography.subtitle1
            )
         }
      }

      if (group.unread > 0) {
         Badge(
            backgroundColor = MaterialTheme.colors.error.copy(alpha = .87f),
            contentColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.error),
            modifier = Modifier.padding(end = 8.dp)
         ) {
            Text(
               text = "${group.unread}",
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(2.dp)
            )
         }
      }
   }
}