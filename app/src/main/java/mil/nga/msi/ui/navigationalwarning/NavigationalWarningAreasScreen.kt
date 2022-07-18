package mil.nga.msi.ui.navigationalwarning

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.extension.nga.index.FeatureTableIndex
import mil.nga.geopackage.extension.rtree.RTreeIndexExtension
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.msi.R
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningGroup
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GeoPackageTileProvider
import mil.nga.sf.Point

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NavigationalWarningGroupScreen(
   openDrawer: () -> Unit,
   onGroupTap: (NavigationArea) -> Unit,
   viewModel: NavigationalWarningAreasViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
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
         viewModel.naturalEarthTileProvider,
         viewModel.navigationAreaTileProvider
      )

      val navigationArea = location?.let { getNavigationArea(LocalContext.current, it) }
      val sortedAreas = warningsByArea.sortedWith { a, b ->
         when {
            a.navigationArea == navigationArea -> -1
            a.navigationArea.title > b.navigationArea.title -> 1
            a.navigationArea.title < b.navigationArea.title -> -1
            else -> 0
         }
      }.toMutableList()

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
   naturalEarthTileProvider: GeoPackageTileProvider,
   navigationAreaTileProvider: GeoPackageTileProvider
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
         isMyLocationEnabled = locationPermissionState.status.isGranted
      ),
      uiSettings = MapUiSettings(
         compassEnabled = false,
         zoomControlsEnabled = false,
         myLocationButtonEnabled = false,
         rotationGesturesEnabled = false
      ),
      locationSource = locationSource
   ) {
      TileOverlay(tileProvider = naturalEarthTileProvider, zIndex = 1f)
      TileOverlay(tileProvider = navigationAreaTileProvider, zIndex = 0f)
   }
}

@Composable
private fun NavigationalWarnings(
   group: NavigationalWarningGroup,
   onGroupTap: () -> Unit
) {
   val colorMap = mapOf(
      NavigationArea.HYDROARC to Color(0xFF77DFFC),
      NavigationArea.HYDROLANT to Color(0xFF7C91F2),
      NavigationArea.HYDROPAC to Color(0xFFF5F481),
      NavigationArea.NAVAREA_IV to Color(0xFFFDBFBF),
      NavigationArea.NAVAREA_XII to Color(0xFF8BCC6B)
   )
   val color = colorMap[group.navigationArea]

   Row(modifier = Modifier
      .fillMaxSize()
      .height(IntrinsicSize.Min)
      .clickable { onGroupTap() }
   ) {
      Box(modifier = Modifier
         .width(6.dp)
         .fillMaxHeight()
         .background(color ?: Color.Transparent)
      )

      Row(
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
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
}

private fun getNavigationArea(
   context: Context,
   location: Location
): NavigationArea? {
   var navigationArea: NavigationArea? = null

   val geopackageManager = GeoPackageFactory.getManager(context)
   val resource = context.resources.openRawResource(R.raw.navigation_areas)
   try { geopackageManager.importGeoPackage("navigation_areas", resource) } catch (e: Exception) { }
   val database = geopackageManager.databasesLike("navigation_areas").firstOrNull()
   val geopackage = geopackageManager.open(database)
   val features: List<String> = geopackage.featureTables
   val featureTable: String = features[0]
   val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
   val rtree = RTreeIndexExtension(geopackage)
   val rtreeDao = rtree.getTableDao(featureDao)
   val point = Point(location.longitude, location.latitude)
   rtreeDao.queryFeatures(point.envelope).first()?.let { row ->
      val codeColumnIndex = row.getColumnIndex("code")
      val code = row.getValue(codeColumnIndex) as? String
      if (code != null) {
         navigationArea = NavigationArea.fromCode(code)
      }
   }

   return navigationArea
}