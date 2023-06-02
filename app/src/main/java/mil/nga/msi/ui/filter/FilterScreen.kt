package mil.nga.msi.ui.filter

import android.Manifest
import android.app.DatePickerDialog
import android.location.Location
import android.widget.DatePicker
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.types.EnumerationType
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.ui.image.tint
import mil.nga.msi.ui.location.LocationPermission
import mil.nga.msi.ui.location.webMercatorToWgs84
import mil.nga.msi.ui.location.wgs84ToWebMercator
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.add
import mil.nga.msi.ui.theme.remove
import mil.nga.sf.Point
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FilterScreen(
   dataSource: DataSource,
   close: () -> Unit,
   viewModel: FilterViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val location by viewModel.locationPolicy.bestLocationProvider.observeAsState()

   viewModel.setDataSource(dataSource)
   val title by viewModel.title.observeAsState("")

   Column {
      TopBar(
         title = title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant,
         modifier = Modifier.fillMaxSize()
      ) {
         Column(
            Modifier
               .fillMaxSize()
               .verticalScroll(scrollState)
         ) {
            Filter(
               dataSource = dataSource,
               location = location
            )
         }
      }
   }
}

@Composable
fun Filter(
   dataSource: DataSource,
   location: Location?,
   viewModel: FilterViewModel = hiltViewModel()
) {
   viewModel.setDataSource(dataSource)
   val filters by viewModel.filters.observeAsState(emptyList())
   val filterParameters by viewModel.filterParameters.observeAsState(emptyList())

   Filters(
      filters = filters,
      removeFilter = {
         val removed = filters.toMutableList()
         removed.remove(it)
         viewModel.setFilters(dataSource, removed)
      }
   )

   if (filterParameters.isNotEmpty()) {
      FilterHeader(
         location = location,
         filterParameters = filterParameters,
         addFilter = {
            val added = filters.toMutableList()
            added.add(it)
            viewModel.setFilters(dataSource, added)
         }
      )
   }
}

@Composable
private fun Filters(
   filters: List<Filter>,
   removeFilter: (Filter) -> Unit
) {
   filters.forEach { filter ->
      Surface {
         Column(
            Modifier.fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .padding(horizontal = 16.dp, vertical = 8.dp)
                  .fillMaxWidth()
            ) {
               Row(Modifier.weight(1f)) {
                  Text(buildAnnotatedString {
                     withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${filter.parameter.title} ")
                     }

                     append(" ${filter.comparator.title} ")

                     withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${valueString(parameter = filter.parameter, value = filter.value)} ")
                     }
                  })
               }

               IconButton(
                  onClick = { removeFilter(filter) }
               ) {
                  Icon(
                     imageVector = Icons.Filled.RemoveCircle,
                     tint = MaterialTheme.colorScheme.remove,
                     contentDescription = "Remove Filter",
                  )
               }
            }
         }
      }

      Divider()
   }
}

private fun valueString(
   parameter: FilterParameter,
   value: Any?
): String {
   return when (parameter.type) {
      FilterParameterType.DATE -> { dateValue(value = value) }
      FilterParameterType.DOUBLE -> { doubleValue(value) }
      FilterParameterType.ENUMERATION -> { enumerationValue(value) }
      FilterParameterType.FLOAT -> { floatValue(value) }
      FilterParameterType.INT -> { intValue(value) }
      FilterParameterType.LOCATION -> { locationValue(value) }
      FilterParameterType.STRING -> { stringValue(value ) }
   }
}

private fun dateValue(
   value: Any?
): String {
   return value?.toString() ?: ""
}

private fun doubleValue(
   value: Any?
): String {
   return value?.toString()?.toDoubleOrNull().toString()
}

private fun enumerationValue(
   value: Any?
): String {
   return (value as? EnumerationType)?.title ?: ""
}

private fun floatValue(
   value: Any?
): String {
   return value?.toString()?.toFloatOrNull().toString()
}

private fun intValue(
   value: Any?
): String {
   return value?.toString()?.toIntOrNull().toString()
}

private fun locationValue(
   value: Any?
): String {
   val values = value?.toString()?.split(",") ?: emptyList()
   val latitude = values.getOrNull(0) ?: 0
   val longitude = values.getOrNull(1) ?: 0
   val distance = values.getOrNull(2) ?: 0
   return "${distance}nm of $latitude,$longitude"
}

private fun stringValue(
   value: Any?
): String {
   return value?.toString() ?: ""
}

@Composable
private fun FilterHeader(
   location: Location?,
   filterParameters: List<FilterParameter>,
   addFilter: (Filter) -> Unit
) {
   val getDefaultValue: (FilterParameter) -> Any? = { parameter ->
      when (parameter.type) {
         FilterParameterType.DATE -> "last 30 days"
         FilterParameterType.ENUMERATION -> {
            parameter.enumerationValues.firstOrNull()
         }
         else -> ""
      }
   }

   val defaultParameter = filterParameters.first()
   val defaultComparator = defaultParameter.type.comparators.first()
   var parameter by remember { mutableStateOf(defaultParameter) }
   var comparator by remember { mutableStateOf(defaultComparator) }
   var value by remember { mutableStateOf(getDefaultValue(defaultParameter)) }

   Surface(
      contentColor = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.fillMaxSize()
   ) {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
      ) {
         Column(
            Modifier
               .weight(1f)
               .fillMaxWidth()
         ) {
            Row(
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.fillMaxWidth()
            ) {
               ParameterSelection(
                  filterParameters = filterParameters,
                  selectedParameter = parameter,
                  onSelectParameter = {
                     parameter = it
                     comparator = parameter.type.comparators.first()
                     value = getDefaultValue(parameter)
                  },
                  modifier = Modifier.padding(end = 16.dp)
               )

               ComparatorSelection(
                  parameter = parameter,
                  selectedComparator = comparator,
                  onSelectComparator = { comparator = it }
               )

               if (parameter.type != FilterParameterType.STRING && parameter.type != FilterParameterType.LOCATION) {
                  Row(
                     Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                  ) {
                     ValueSelection(
                        location = location,
                        parameter = parameter,
                        comparator = comparator,
                        value = value,
                        onValueChanged = { value = it }
                     )
                  }
               }
            }

            if (parameter.type == FilterParameterType.STRING || parameter.type == FilterParameterType.LOCATION) {
               Column(Modifier.padding(top = 16.dp)) {
                  ValueSelection(
                     location = location,
                     comparator = comparator,
                     parameter = parameter,
                     value = value,
                     onValueChanged = { value = it }
                  )
               }
            }
         }

         IconButton(
            onClick = {
               addFilter(
                  Filter(
                     parameter = parameter,
                     comparator = comparator,
                     value = value
                  )
               )
            }
         ) {
            Icon(
               imageVector = Icons.Filled.AddCircle,
               tint = MaterialTheme.colorScheme.add,
               contentDescription = "Add Filter",
            )
         }
      }
   }
}

@Composable
private fun ParameterSelection(
   filterParameters: List<FilterParameter>,
   selectedParameter: FilterParameter,
   onSelectParameter: (FilterParameter) -> Unit,
   modifier: Modifier = Modifier,
) {
   var expanded by remember { mutableStateOf(false) }

   Column(modifier = modifier) {
      Row(
         modifier = Modifier.clickable {
            expanded = true
         }
      ) {
         Text(
            text = selectedParameter.title,
            style = MaterialTheme.typography.titleMedium
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "Parameters"
         )
      }

      DropdownMenu(
         expanded = expanded,
         onDismissRequest = { expanded = false }
      ) {
         filterParameters.forEach { parameter ->
            DropdownMenuItem(
               onClick = {
                  onSelectParameter(parameter)
                  expanded = false
               },
               text = {
                  Row(
                     verticalAlignment = Alignment.CenterVertically
                  ) {
                     if (selectedParameter == parameter) {
                        Icon(
                           imageVector = Icons.Default.Check,
                           contentDescription = "Selected Parameter"
                        )
                     } else {
                        Spacer(modifier = Modifier.size(24.dp))
                     }

                     Text(
                        text = parameter.title,
                        modifier = Modifier.padding(start = 8.dp)
                     )
                  }
               }
            )
         }
      }
   }
}

@Composable
fun ComparatorSelection(
   parameter: FilterParameter,
   selectedComparator: ComparatorType,
   onSelectComparator: (ComparatorType) -> Unit,
   modifier: Modifier = Modifier,
) {
   var expanded by remember { mutableStateOf(false) }

   Column(modifier = modifier) {
      Row(
         modifier = Modifier.clickable {
            expanded = true
         }
      ) {
         Text(
            text = selectedComparator.title,
            style = MaterialTheme.typography.titleMedium
         )
         Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "Parameters"
         )
      }

      DropdownMenu(
         expanded = expanded,
         onDismissRequest = { expanded = false }
      ) {
         parameter.type.comparators.forEach { comparator ->
            DropdownMenuItem(
               onClick = {
                  onSelectComparator(comparator)
                  expanded = false
               },
               text = {
                  Row {
                     if (selectedComparator == comparator) {
                        Icon(
                           imageVector = Icons.Default.Check,
                           contentDescription = "Selected Parameter"
                        )
                     } else {
                        Spacer(modifier = Modifier.size(24.dp))
                     }

                     Text(
                        text = comparator.title,
                        modifier = Modifier.padding(start = 8.dp)
                     )
                  }
               }
            )
         }
      }
   }
}

@Composable
private fun ValueSelection(
   location: Location?,
   parameter: FilterParameter,
   comparator: ComparatorType,
   value: Any?,
   onValueChanged: (Any) -> Unit
) {
   var expanded by remember { mutableStateOf(false) }

   when(parameter.type) {
      FilterParameterType.DATE -> {
         DateValue(
            comparator = comparator,
            value = value,
            onSelectValue = onValueChanged
         )
      }
      FilterParameterType.DOUBLE -> {
         DoubleValue(
            value = value?.toString().orEmpty(),
            onValueChanged = { onValueChanged(it) }
         )
      }
      FilterParameterType.ENUMERATION -> {
         Row(
            modifier = Modifier.clickable {
               expanded = true
            }
         ) {
            Text(
               text = (value as EnumerationType).title,
               style = MaterialTheme.typography.titleMedium
            )
            Icon(
               imageVector = Icons.Default.ExpandMore,
               contentDescription = "Select new enumeration"
            )
         }

         DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
         ) {
            parameter.enumerationValues.forEach { enumeration ->
               DropdownMenuItem(
                  onClick = {
                     onValueChanged(enumeration)
                     expanded = false
                  },
                  text = {
                     Row {
                        if (value == enumeration) {
                           Icon(
                              imageVector = Icons.Default.Check,
                              contentDescription = "Selected Enumeration"
                           )
                        } else {
                           Spacer(modifier = Modifier.size(24.dp))
                        }

                        Text(
                           text = enumeration.title,
                           modifier = Modifier.padding(start = 8.dp)
                        )
                     }
                  }
               )
            }
         }
      }
      FilterParameterType.FLOAT -> {
         DoubleValue(
            value = value?.toString().orEmpty(),
            onValueChanged = { onValueChanged(it) }
         )
      }
      FilterParameterType.INT -> {
         IntValue(
            value = value?.toString().orEmpty(),
            onValueChanged = { onValueChanged(it) }
         )
      }
      FilterParameterType.LOCATION -> {
         LocationValue(
            location = location,
            comparator = comparator,
            value = value.toString(),
            onValueChanged = { onValueChanged(it) }
         )
      }
      FilterParameterType.STRING -> {
         StringValue(
            value = value?.toString() ?: "",
            onValueChanged = { onValueChanged(it) }
         )
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateValue(
   comparator: ComparatorType,
   value: Any?,
   onSelectValue: (Any) -> Unit
) {
   val focusManager = LocalFocusManager.current
   var expanded by remember { mutableStateOf(false) }

   if (comparator == ComparatorType.WITHIN) {
      Column {
         Row(
            modifier = Modifier.clickable {
               expanded = true
            }
         ) {
            Text(
               text = value?.toString() ?: "",
               style = MaterialTheme.typography.titleMedium
            )
            Icon(
               imageVector = Icons.Default.ExpandMore,
               contentDescription = "Date Value"
            )
         }

         DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
         ) {
            listOf("last 30 days", "last 7 days", "last 365 days").forEach { comparator ->
               DropdownMenuItem(
                  onClick = {
                     onSelectValue(comparator)
                     expanded = false
                  },
                  text = {
                     Row {
                        if (value == comparator) {
                           Icon(
                              imageVector = Icons.Default.Check,
                              contentDescription = "Selected Date"
                           )
                        } else {
                           Spacer(modifier = Modifier.size(24.dp))
                        }

                        Text(
                           text = comparator,
                           modifier = Modifier.padding(start = 8.dp)
                        )
                     }
                  }
               )
            }
         }
      }
   } else {
      val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      val date = try {
         LocalDate.parse(value.toString(), dateFormat)
      } catch (e: Exception) { LocalDate.now() }

      val dialog = DatePickerDialog(LocalContext.current, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
         val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
         onSelectValue(dateFormat.format(selectedDate))
      }, date.year, date.monthValue - 1, date.dayOfMonth)

      Column {
         TextField(
            value = dateFormat.format(date),
            onValueChange = { },
            enabled = false,
            colors = TextFieldDefaults.colors(
               disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
               disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current)
            ),
            modifier = Modifier
               .fillMaxWidth()
               .clickable(onClick = {
                  dialog.show()
                  focusManager.clearFocus()
               })
         )
      }
   }
}

@Composable
fun DoubleValue(
   value: String,
   onValueChanged: (Any) -> Unit,
) {
   val focusManager = LocalFocusManager.current

   Column {
      TextField(
         value = value,
         onValueChange = { onValueChanged(it) },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
         keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
      )
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntValue(
   value: String,
   onValueChanged: (Any) -> Unit,
) {
   val focusManager = LocalFocusManager.current

   Column {
      TextField(
         value = value,
         onValueChange = { onValueChanged(it) },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
         keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
      )
   }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationValue(
   location: Location?,
   comparator: ComparatorType,
   value: String,
   onValueChanged: (Any) -> Unit,
) {
   val focusManager = LocalFocusManager.current
   val values = if (value.isNotEmpty()) value.split(",") else emptyList()
   val latitude = values.getOrNull(0)?.toDoubleOrNull()
   val longitude = values.getOrNull(1)?.toDoubleOrNull()
   val distance = values.getOrNull(2)?.toDoubleOrNull()

   val mapLocation: LatLng? = if (comparator == ComparatorType.NEAR_ME) {
      if (location != null) {
         LatLng(location.latitude, location.longitude)
      } else null
   } else {
      if (latitude != null && longitude != null) {
         LatLng(latitude, longitude)
      } else null
   }

   val cameraPositionState = rememberCameraPositionState {
      if (mapLocation != null) {
         position = CameraPosition.fromLatLngZoom(mapLocation, 16f)
      }
   }

   val locationPermissionState: PermissionState = rememberPermissionState(
      Manifest.permission.ACCESS_FINE_LOCATION
   )

   val uiSettings = MapUiSettings(
      zoomControlsEnabled = false,
      compassEnabled = false
   )

   val mapStyleOptions = if (isSystemInDarkTheme()) {
      MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.map_theme_night)
   } else null

   val mapProperties = MapProperties(
      minZoomPreference = 0f,
      isMyLocationEnabled = locationPermissionState.status.isGranted,
      mapStyleOptions = mapStyleOptions
   )

   LocationPermission(locationPermissionState)

   LaunchedEffect(mapLocation, distance) {
      if (mapLocation != null) {
         if (distance == null) {
            cameraPositionState.animate(
               update = CameraUpdateFactory.newCameraPosition(
                  CameraPosition(mapLocation, 11f, 0f, 0f)
               ),
               durationMs = 1000
            )
         } else {
            val distanceInMeters = distance * 1852
            val center4326 = Point(mapLocation.longitude, mapLocation.latitude).wgs84ToWebMercator()
            val neCorner = Point(center4326.x + distanceInMeters, center4326.y + distanceInMeters).webMercatorToWgs84()
            val swCorner = Point(center4326.x - distanceInMeters, center4326.y - distanceInMeters).webMercatorToWgs84()
            val bounds = LatLngBounds(
               LatLng(swCorner.y, swCorner.x),
               LatLng(neCorner.y, neCorner.x),
            )
            cameraPositionState.animate(
               update = CameraUpdateFactory.newLatLngBounds(bounds, 20),
               durationMs = 1000
            )
         }
      }
   }

   Column(Modifier.padding(end = 8.dp)) {
      GoogleMap(
         uiSettings = uiSettings,
         properties = mapProperties,
         cameraPositionState = cameraPositionState,
         modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(bottom = 16.dp)
      ) {
         if (mapLocation != null && comparator == ComparatorType.CLOSE_TO) {
            val bitmap = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_outline_my_location_24)!!.toBitmap()
            Marker(
               state = MarkerState(position = mapLocation),
               icon = BitmapDescriptorFactory.fromBitmap(bitmap.tint(Color.Black.toArgb()))
            )
         }
      }

      if (comparator == ComparatorType.CLOSE_TO) {
         Row(Modifier.padding(bottom = 16.dp)) {
            TextField(
               value = latitude?.toString() ?: "",
               label = { Text("Latitude") },
               onValueChange = { onValueChanged("${it},${longitude},${distance}") },
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
               keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
               modifier = Modifier
                  .weight(1f)
                  .padding(end = 16.dp)
            )

            TextField(
               value = longitude?.toString() ?: "",
               label = { Text("Longitude") },
               onValueChange = { onValueChanged("${latitude},${it},${distance}") },
               keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
               keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
               modifier = Modifier.weight(1f)
            )
         }
      }

      TextField(
         value = distance?.toString() ?: "",
         label = { Text("Distance") },
         onValueChange = { onValueChanged("${latitude},${longitude},${it}") },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
         keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
         modifier = Modifier.fillMaxWidth()
      )
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StringValue(
   value: String,
   onValueChanged: (String) -> Unit
) {
   TextField(
      value = value,
      onValueChange = { onValueChanged(it) },
      placeholder = {
         Text("Value")
      }
   )
}

