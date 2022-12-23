package mil.nga.msi.ui.map

import android.Manifest
import android.animation.ValueAnimator
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.toBitmap
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
import mil.nga.msi.R
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.geocoder.GeocoderState
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.location.LocationText
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.cluster.MapAnnotation
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

data class MapPosition(
   val location: MapLocation,
   val name: String? = null
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
   mapDestination : MapPosition? = null,
   onAnnotationClick: (MapAnnotation) -> Unit,
   onAnnotationsClick: (Collection<MapAnnotation>) -> Unit,
   onMapSettings: () -> Unit,
   openFilter: () -> Unit,
   openDrawer: () -> Unit,
   locationCopy: (String) -> Unit,
   viewModel: MapViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val showLocation by viewModel.showLocation.observeAsState(false)
   var searchExpanded by remember { mutableStateOf(false) }
   val searchResults by viewModel.searchResults.observeAsState(emptyList())
   val filterCount by viewModel.filterCount.observeAsState(0)
   val fetching by viewModel.fetching.observeAsState(emptyMap())
   var fetchingVisibility by rememberSaveable { mutableStateOf(true) }
   val baseMap by viewModel.baseMap.observeAsState()
   val mapOrigin by viewModel.mapLocation.observeAsState()
   var destination by remember { mutableStateOf(mapDestination) }
   val location by viewModel.locationPolicy.bestLocationProvider.observeAsState()
   var located by remember { mutableStateOf(false) }
   val tileProviders by viewModel.tileProviders.observeAsState(emptyMap())
   val mapped by viewModel.mapped.observeAsState(emptyMap())
   val annotation by viewModel.annotationProvider.annotation.observeAsState()
   val clipboardManager: ClipboardManager = LocalClipboardManager.current

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
         title = "Marlin",
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            Box {
               IconButton(onClick = { openFilter() } ) {
                  Icon(Icons.Default.FilterList, contentDescription = "Filter Map")
               }

               if (filterCount > 0) {
                  Box(
                     contentAlignment = Alignment.Center,
                     modifier = Modifier
                        .clip(CircleShape)
                        .height(24.dp)
                        .background(MaterialTheme.colors.secondary)
                        .align(Alignment.TopEnd)
                  ) {
                     Text(
                        text = "$filterCount",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.onPrimary
                     )
                  }
               }
            }
         }
      )

      if (showLocation && locationPermissionState.status.isGranted) {
         val text = location?.let {
            "${String.format("%.5f", it.latitude)}, ${String.format("%.5f", it.longitude)}"
         }
         Surface(color = MaterialTheme.colors.primarySurface) {
            Row(
               horizontalArrangement = Arrangement.Center,
               modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                     text?.let {
                        clipboardManager.setText(AnnotatedString.Builder(it).toAnnotatedString())
                        locationCopy(it)
                     }
                  }
                  .padding(vertical = 8.dp)
            ) {
               Text(text = text ?: "Searching for your location...")
            }
         }
      }

      Box(Modifier.fillMaxWidth()) {
         Map(
            origin,
            destination,
            baseMap,
            locationSource,
            locationPermissionState.status.isGranted,
            tileProviders,
            searchResults,
            annotation,
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

                  viewModel.setMapLocation(mapLocation, position.zoom.toInt())
               }
            },
            onMapClick = { latLng, zoom, region ->
               val screenPercentage = 0.04
               val tolerance = (region.farRight.longitude - region.farLeft.longitude) * screenPercentage
               scope.launch {
                  val mapAnnotations = viewModel.getMapAnnotations(
                     minLongitude = latLng.longitude - tolerance,
                     maxLongitude = latLng.longitude + tolerance,
                     minLatitude = latLng.latitude - tolerance,
                     maxLatitude = latLng.latitude + tolerance
                  )

                  if (mapAnnotations.isNotEmpty()) {
                     val bounds = LatLngBounds.builder().apply {
                        mapAnnotations.forEach { this.include(LatLng(it.latitude, it.longitude)) }
                     }.build()

                     destination = MapPosition(
                        location = MapLocation.newBuilder()
                           .setLatitude(bounds.center.latitude)
                           .setLongitude(bounds.center.longitude)
                           .setZoom(zoom.toDouble())
                           .build()
                     )

                     if (mapAnnotations.size == 1) {
                        viewModel.annotationProvider.setMapAnnotation(mapAnnotations.first())
                        onAnnotationClick(mapAnnotations.first())
                     } else if (mapAnnotations.isNotEmpty()) {
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

         Box(
            Modifier
               .align(Alignment.TopEnd)
               .padding(16.dp)
         ) {
            Settings {
               onMapSettings()
            }
         }

         if (locationPermissionState.status.isGranted) {
            Box(
               modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp)
            ) {
               Zoom(located) {
                  located = true
                  scope.launch {
                     destination = MapPosition(
                        location = MapLocation.newBuilder()
                           .setLatitude(location?.latitude ?: 0.0)
                           .setLongitude(location?.longitude ?: 0.0)
                           .setZoom(17.0)
                           .build()
                     )
                  }
               }
            }
         }

         Box(
            modifier = Modifier
               .align(Alignment.TopStart)
               .padding(16.dp)
         ) {
            Search(
               expanded = searchExpanded,
               results = searchResults,
               onExpand = {
                  searchExpanded = !searchExpanded
               },
               onTextChanged = {
                  viewModel.search(it)
               },
               onLocationTap = {
                  destination = MapPosition(
                     name = "Hello Map",
                     location = MapLocation.newBuilder()
                        .setLatitude(it.latitude)
                        .setLongitude(it.longitude)
                        .setZoom(12.0)
                        .build()
                  )
               },
               onLocationCopy = locationCopy
            )
         }

         Box(
            modifier = Modifier
               .align(Alignment.BottomStart)
               .padding(start = 8.dp, bottom = 32.dp)
         ) {
            DataSources(
               mapped = mapped
            ) {
               viewModel.toggleOnMap(it)
            }
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
   locationSource: LocationSource,
   locationEnabled: Boolean,
   tileProviders: Map<TileProviderType, TileProvider>,
   searchResults: List<GeocoderState>,
   annotation: MapAnnotation?,
   onMapMove: (CameraPosition, Int) -> Unit,
   onMapClick: (LatLng, Float, VisibleRegion) -> Unit
) {
   val scope = rememberCoroutineScope()
   val context = LocalContext.current

   var isMapLoaded by remember { mutableStateOf(false) }
   val cameraPositionState: CameraPositionState = rememberCameraPositionState {}
   var cameraMoveReason by remember { mutableStateOf(0) }

   LaunchedEffect(origin) {
      origin?.let { origin ->
         cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(origin.latitude, origin.longitude), origin.zoom.toFloat())
      }
   }

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
                  val update = CameraUpdateFactory.newLatLngZoom(
                     LatLng(
                        destination.location.latitude,
                        destination.location.longitude
                     ), destination.location.zoom.toFloat()
                  )
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

      searchResults.forEach { result ->
         Marker(
            state = MarkerState(LatLng(result.location.latitude, result.location.longitude)),
            icon = BitmapDescriptorFactory.fromResource(context, R.drawable.ic_round_location_on_24, result.name)
         )
      }

      MapEffect(destination, annotation) { map ->
         map.setOnCameraMoveStartedListener { reason ->
            cameraMoveReason = reason
         }

         map.setOnCameraMoveListener {
            onMapMove(map.cameraPosition, cameraMoveReason)
         }

         map.setOnMapClickListener { latLng ->
            onMapClick(latLng, map.cameraPosition.zoom, map.projection.visibleRegion)
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
                     .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(icon))
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
                  .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(icon))
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

@Composable
private fun Settings(
   onTap: () -> Unit
) {
   FloatingActionButton(
      onClick = { onTap() },
      backgroundColor = MaterialTheme.colors.background,
      modifier = Modifier.size(40.dp)
   ) {
      Icon(Icons.Outlined.Map,
         tint = MaterialTheme.colors.primary,
         contentDescription = "Map Settings"
      )
   }
}

@Composable
private fun Zoom(
   located: Boolean,
   onZoom: () -> Unit
) {
   FloatingActionButton(
      onClick = {
         onZoom()
      },
      backgroundColor = MaterialTheme.colors.background
   ) {
      var icon = Icons.Outlined.LocationSearching
      var tint =  MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
      if (located) {
         icon = Icons.Outlined.MyLocation
         tint = MaterialTheme.colors.primary
      }
      Icon(
         imageVector = icon,
         tint = tint,
         contentDescription = "Zoom to location"
      )
   }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Search(
   expanded: Boolean,
   results: List<GeocoderState> = emptyList(),
   onExpand: () -> Unit,
   onTextChanged: (String) -> Unit,
   onLocationTap: (LatLng) -> Unit,
   onLocationCopy: (String) -> Unit
) {
   val focusRequester = remember { FocusRequester() }
   val configuration = LocalConfiguration.current
   val colors = TextFieldDefaults.textFieldColors()
   val interactionSource = remember { MutableInteractionSource() }
   var text by remember { mutableStateOf("") }
   val width = if (expanded) configuration.screenWidthDp.dp.minus(88.dp) else 40.dp

   LaunchedEffect(expanded) {
      if (expanded) {
         focusRequester.requestFocus()
      }
   }

   Surface(
      elevation = 6.dp,
      shape = RoundedCornerShape(20.dp)
   ) {
      Column {
         BasicTextField(
            value = text,
            onValueChange = {
               text = it
               onTextChanged(it)
            },
            interactionSource = interactionSource,
            enabled = expanded,
            singleLine = true,
            modifier = Modifier
               .animateContentSize()
               .background(
                  color = MaterialTheme.colors.background,
                  shape = RoundedCornerShape(6.dp)
               )
               .indicatorLine(
                  enabled = expanded,
                  isError = false,
                  interactionSource = interactionSource,
                  colors = colors,
                  focusedIndicatorLineThickness = 0.dp,
                  unfocusedIndicatorLineThickness = 0.dp
               )
               .height(40.dp)
               .width(width)
               .focusRequester(focusRequester)
         ) {
            TextFieldDefaults.TextFieldDecorationBox(
               value = text,
               innerTextField = it,
               singleLine = true,
               enabled = expanded,
               leadingIcon = {
                  IconButton(onClick = { onExpand() }) {
                     Icon(
                        imageVector = Icons.Default.Search,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = "Search"
                     )
                  }
               },
               trailingIcon = {
                  IconButton(
                     onClick = {
                        text = ""
                        onTextChanged("")
                     }
                  ) {
                     Icon(
                        imageVector = Icons.Default.Close,
                        modifier = Modifier.size(18.dp),
                        contentDescription = "Search Clear"
                     )
                  }
               },
               visualTransformation = VisualTransformation.None,
               placeholder = {
                  Text(text = "Search")
               },
               interactionSource = interactionSource,
               contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(top = 0.dp, bottom = 0.dp),
            )
         }

         if (results.isNotEmpty()) {
            val scrollState = rememberScrollState()
            val searchHeight = configuration.screenHeightDp.dp.div(3)

            Column(
               modifier = Modifier
                  .width(width)
                  .heightIn(0.dp, searchHeight)
                  .verticalScroll(scrollState)
            ) {
               results.forEach { result ->
                  Divider(Modifier.padding(horizontal = 8.dp))

                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                  ) {
                     Column(Modifier.weight(1f)) {
                        Text(
                           text = result.name,
                           style = MaterialTheme.typography.subtitle1,
                           fontWeight = FontWeight.Medium
                        )
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                           Text(
                              text = result.address ?: "",
                              style = MaterialTheme.typography.subtitle2
                           )
                        }

                        LocationText(
                           dms = DMS.from(result.location),
                           onCopiedToClipboard = { onLocationCopy(it) }
                        )
                     }

                     result.location.let {
                        IconButton(
                           onClick = { onLocationTap(it) }
                        ) {
                           Icon(
                              imageVector = Icons.Default.LocationSearching,
                              tint = MaterialTheme.colors.primary,
                              contentDescription = "Zoom To Search Result"
                           )
                        }
                     }
                  }
               }
            }
         }
      }
   }
}


@Composable
private fun DataSources(
   mapped: Map<DataSource, Boolean>,
   onDataSourceToggle: (DataSource) -> Unit,
) {
   Column(
      Modifier.padding(horizontal = 8.dp)
   ) {
      DataSource.values().filter { it.mappable }.forEach { dataSource ->
         var tint =  MaterialTheme.colors.onPrimary
         var background = dataSource.color
         val bitmap = AppCompatResources.getDrawable(LocalContext.current, dataSource.icon)!!.toBitmap().asImageBitmap()

         if (mapped[dataSource] == false) {
            tint =  Color(0xFF999999)
            background = Color(0xFFDDDDDD)
         }

         FloatingActionButton(
            onClick = { onDataSourceToggle(dataSource) },
            backgroundColor = background,
            modifier = Modifier
               .padding(bottom = 12.dp)
               .size(40.dp)
         ) {
            Icon(
               bitmap = bitmap,
               tint = tint,
               modifier = Modifier.size(24.dp),
               contentDescription = "${dataSource.route.title} map toggle"
            )
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
         marker.setIcon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(scaled))
      }
   }

   animator?.start()
}