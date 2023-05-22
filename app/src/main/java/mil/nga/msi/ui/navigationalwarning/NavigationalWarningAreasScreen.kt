package mil.nga.msi.ui.navigationalwarning

import android.content.Context
import android.location.Location
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.extension.rtree.RTreeIndexExtension
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapPosition
import com.google.maps.android.compose.clustering.Clustering
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.map.CircleShape
import mil.nga.msi.ui.map.MapShape
import mil.nga.msi.ui.map.PointShape
import mil.nga.msi.ui.map.PolygonShape
import mil.nga.msi.ui.map.PolylineShape
import mil.nga.msi.ui.map.cluster.ClusterItem

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NavigationalWarningGroupScreen(
   position: MapPosition?,
   openDrawer: () -> Unit,
   onGroupTap: (NavigationArea) -> Unit,
   onNavigationWarningsTap: (List<NavigationalWarningKey>) -> Unit,
   viewModel: NavigationalWarningAreasViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val screenHeight = LocalConfiguration.current.screenHeightDp
   var fullScreenMap by remember { mutableStateOf(false) }
   var mapPosition by remember { mutableStateOf(position) }
   val annotations by viewModel.annotations.observeAsState(emptyList())
   val unparsedWarnings by viewModel.unparsedWarnings.observeAsState(emptyList())
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
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant,
         modifier = Modifier.fillMaxHeight()
      ) {
         Column {
            Box(Modifier.animateContentSize()) {
               NavigationAreaMap(
                  height = if (fullScreenMap) screenHeight * 1f else screenHeight * .33f,
                  fullScreenMap = fullScreenMap,
                  mapPosition = mapPosition,
                  onZoom = {
                     location?.let {
                        mapPosition = MapPosition(
                           location = MapLocation.newBuilder()
                              .setLatitude(it.latitude)
                              .setLongitude(it.longitude)
                              .setZoom(17.0)
                              .build()
                        )
                     }
                  },
                  onMapFullScreen = { fullScreenMap = it },
                  onNavigationWarningsTap = { keys -> onNavigationWarningsTap(keys) },
                  onMapPositionChanged = { mapPosition = it },
                  location = location,
                  annotations = annotations,
                  locationPermissionState = locationPermissionState,
                  naturalEarthTileProvider = viewModel.naturalEarthTileProvider,
                  navigationAreaTileProvider = viewModel.navigationAreaTileProvider
               )
            }

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
                  NavigationalWarnings(
                     title = group.navigationArea.title,
                     color = group.navigationArea.color,
                     active = "Active ${group.total}",
                     unread = group.unread
                  ) {
                     onGroupTap(group.navigationArea)
                  }

                  Divider()
               }

               if (unparsedWarnings.isNotEmpty()) {
                  NavigationalWarnings(
                     title = NavigationArea.UNPARSED.title,
                     color = NavigationArea.UNPARSED.color,
                     active = "${unparsedWarnings.size}",
                     unread = 0
                  ) {
                     onGroupTap(NavigationArea.UNPARSED)
                  }
               }
            }
         }
      }
   }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NavigationAreaMap(
   mapPosition: MapPosition?,
   location: Location?,
   locationPermissionState: PermissionState,
   annotations: List<MapShape>,
   naturalEarthTileProvider: TileProvider,
   navigationAreaTileProvider: TileProvider,
   height: Float,
   fullScreenMap: Boolean,
   onZoom: () -> Unit,
   onMapFullScreen: (Boolean) -> Unit,
   onMapPositionChanged: (MapPosition) -> Unit,
   onNavigationWarningsTap: (List<NavigationalWarningKey>) -> Unit
) {
   val scope = rememberCoroutineScope()
   val cameraPositionState = rememberCameraPositionState {}

   var setLocation by remember { mutableStateOf(false) }
   LaunchedEffect(location) {
      if (!setLocation) {
         location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 0f)
         }
      }

      setLocation = true
   }

   LaunchedEffect(mapPosition) {
      mapPosition?.location?.let { location ->
         scope.launch {
            val update = CameraUpdateFactory.newLatLngZoom(
               LatLng(
                  location.latitude,
                  location.longitude
               ), location.zoom.toFloat()
            )
            cameraPositionState.animate(update)
         }
      }

      mapPosition?.bounds?.let { bounds ->
         scope.launch {
            val update = CameraUpdateFactory.newLatLngBounds(bounds, 20)
            cameraPositionState.animate(update)
         }
      }
   }

   Box(
      Modifier
         .fillMaxWidth()
         .height(height.dp)
   ) {

      Map(
         annotations = annotations,
         naturalEarthTileProvider = naturalEarthTileProvider,
         navigationAreaTileProvider = navigationAreaTileProvider,
         cameraPositionState = cameraPositionState,
         locationPermissionState = locationPermissionState,
         onMapPositionChanged = onMapPositionChanged,
         onNavigationWarningsTap = onNavigationWarningsTap
      )

      Column(
         Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
      ) {
         Box (Modifier.padding(bottom = 8.dp)) {
            FloatingActionButton(
               containerColor = MaterialTheme.colorScheme.surface,
               onClick = { onZoom() },
               modifier = Modifier.size(40.dp)
            ) {
               Icon(Icons.Outlined.LocationSearching,
                  tint = MaterialTheme.colorScheme.tertiary,
                  contentDescription = "Zoom To Location"
               )
            }
         }

         Box {
            FloatingActionButton(
               containerColor = MaterialTheme.colorScheme.surface,
               onClick = { onMapFullScreen(!fullScreenMap) },
               modifier = Modifier.size(40.dp)
            ) {
               val icon = if (fullScreenMap) Icons.Default.FullscreenExit else Icons.Default.Fullscreen
               Icon(icon,
                  tint = MaterialTheme.colorScheme.tertiary,
                  contentDescription = "Expand/Collapse Map"
               )
            }
         }
      }
   }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map(
   annotations: List<MapShape>,
   naturalEarthTileProvider: TileProvider,
   navigationAreaTileProvider: TileProvider,
   cameraPositionState: CameraPositionState,
   locationPermissionState: PermissionState,
   onMapPositionChanged: (MapPosition) -> Unit,
   onNavigationWarningsTap: (List<NavigationalWarningKey>) -> Unit
) {
   val mapStyleOptions = if (isSystemInDarkTheme()) {
      MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.map_theme_night)
   } else null

   GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState,
      properties = MapProperties(
         mapType = MapType.NORMAL,
         mapStyleOptions = mapStyleOptions,
         isMyLocationEnabled = locationPermissionState.status.isGranted
      ),
      uiSettings = MapUiSettings(
         compassEnabled = false,
         zoomControlsEnabled = false,
         myLocationButtonEnabled = false,
         rotationGesturesEnabled = false
      )
   ) {
      TileOverlay(tileProvider = naturalEarthTileProvider, zIndex = 1f)
      TileOverlay(tileProvider = navigationAreaTileProvider, zIndex = 0f)

      MapAnnotations(
         annotations = annotations,
         onMapPositionChanged = onMapPositionChanged,
         onTap = { annotations ->
            val keys = annotations.map { NavigationalWarningKey.fromId(it) }
            onNavigationWarningsTap(keys)
         }
      )
   }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapAnnotations(
   annotations: List<MapShape>,
   onTap: ((List<String>) -> Unit)? = null,
   onMapPositionChanged: ((MapPosition) -> Unit)? = null
) {
   val context = LocalContext.current

   Clustering(
      items = annotations.filterIsInstance(PointShape::class.java).map {
         ClusterItem(it.latLng, it.tag)
      },
      onClusterClick = { cluster ->
         onTap?.invoke(cluster.items.mapNotNull { it.tag })
         onMapPositionChanged?.invoke(
            MapPosition(
               location = MapLocation
                  .newBuilder()
                  .setLatitude(cluster.position.latitude)
                  .setLongitude(cluster.position.longitude)
                  .build()
            )
         )

         true
      },
      onClusterItemClick = { item ->
         item.tag?.let { onTap?.invoke(listOf(it)) }
         onMapPositionChanged?.invoke(
            MapPosition(
               location = MapLocation
                  .newBuilder()
                  .setLatitude(item.position.latitude)
                  .setLongitude(item.position.longitude)
                  .build()
            )
         )
         true
      },
      clusterContent = null,
      clusterItemContent = {
         val icon = AppCompatResources.getDrawable(
            context,
            R.drawable.ic_navigationwarning_marker_24dp
         )!!.toBitmap()

         Image(
            bitmap = icon.asImageBitmap(),
            contentDescription = "Navigational Warning")
      }
   )

   annotations.filterIsInstance<CircleShape>().forEach { circle ->
      Circle(
         tag = circle.tag,
         center = circle.center,
         radius = circle.radius,
         strokeColor = DataSource.NAVIGATION_WARNING.color,
         fillColor = DataSource.NAVIGATION_WARNING.color.copy(alpha = .2f),
         zIndex = 2f,
         clickable = true,
         onClick = { circle.tag?.let { onTap?.invoke(listOf(it)) } }
      )
   }

   annotations.filterIsInstance<PolylineShape>().forEach { polyline ->
      Polyline(
         tag = polyline.tag,
         points = polyline.points,
         color = DataSource.NAVIGATION_WARNING.color,
         zIndex = 2f,
         clickable = true,
         onClick = { polyline.tag?.let { onTap?.invoke(listOf(it)) } }
      )
   }

   annotations.filterIsInstance<PolygonShape>().forEach { polygon ->
      Polygon(
         tag = polygon.tag,
         points = polygon.points,
         strokeColor = DataSource.NAVIGATION_WARNING.color,
         fillColor = DataSource.NAVIGATION_WARNING.color.copy(alpha = .2f),
         zIndex = 2f,
         clickable = true,
         onClick = { polygon.tag?.let { onTap?.invoke(listOf(it)) } }
      )
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationalWarnings(
   title: String,
   color: Color,
   active: String,
   unread: Int,
   onGroupTap: () -> Unit
) {
   Surface {
      Row(modifier = Modifier
         .fillMaxSize()
         .height(IntrinsicSize.Min)
         .clickable { onGroupTap() }
      ) {
         Box(
            modifier = Modifier
               .width(6.dp)
               .fillMaxHeight()
               .background(color)
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
                  text = title,
                  style = MaterialTheme.typography.titleSmall
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = active,
                     style = MaterialTheme.typography.titleMedium
                  )
               }
            }

            if (unread > 0) {
               Badge(
                  containerColor = DataSource.NAVIGATION_WARNING.color,
                  contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.error),
                  modifier = Modifier.padding(end = 8.dp)
               ) {
                  Text(
                     text = "$unread",
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(2.dp)
                  )
               }
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
   try { geopackageManager.importGeoPackage("navigation_areas", resource) } catch (_: Exception) { }
   val database = geopackageManager.databasesLike("navigation_areas").firstOrNull()
   val geopackage = geopackageManager.open(database)
   val features: List<String> = geopackage.featureTables
   val featureTable: String = features[0]
   val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
   val rtree = RTreeIndexExtension(geopackage)
   val rtreeDao = rtree.getTableDao(featureDao)
   val point = mil.nga.sf.Point(location.longitude, location.latitude)
   rtreeDao.queryFeatures(point.envelope).first()?.let { row ->
      val codeColumnIndex = row.getColumnIndex("code")
      val code = row.getValue(codeColumnIndex) as? String
      if (code != null) {
         navigationArea = NavigationArea.fromCode(code)
      }
   }

   return navigationArea
}