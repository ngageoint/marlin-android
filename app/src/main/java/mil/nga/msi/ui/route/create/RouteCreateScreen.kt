package mil.nga.msi.ui.route.create

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RouteCreateScreen(
    onBack: () -> Unit,
    viewModel: RouteCreateViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val createState by viewModel.routeCreateState.observeAsState()

    var name by remember { mutableStateOf("") }

    val location by viewModel.locationProvider.observeAsState()
    val locationPermissionState: PermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LocationPermission(locationPermissionState)

    if (locationPermissionState.status.isGranted) {
        viewModel.setLocationEnabled(true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Create Route",
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationClicked = { onBack() }
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {

                TextField(
                    value = name,
                    label = { Text("Route Name") },
                    onValueChange = { newText ->
                        name = newText
                        viewModel.setName(name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )

                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    Text(
                        text = "Select a feature to add to the route, long press to add custom point, drag to reorder.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                }

                Box(Modifier.weight(1f)) {
                    Map(
                    )
                }
                Button(
                    enabled = name.isNotEmpty(),
                    onClick = {
                        scope.launch {
                            viewModel.saveRoute(
                                route = Route(
                                    name = name,
                                    createdTime = Date(),
                                    updatedTime = Date()
                                )
                            )
//                            onClose()
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

@Composable
private fun Map(
) {
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

//    LaunchedEffect(latLngBounds) {
//        latLngBounds?.let {
//            scope.launch {
//                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))
//            }
//        }
//    }

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.NORMAL),
            uiSettings = MapUiSettings(compassEnabled = false),
            modifier = Modifier.weight(1f)
        ) {
//            tileOverlayOptions { TileOverlay(tileProvider = WMSTileProvider(service = service, url = wmsUrl)) }
        }
    }
}