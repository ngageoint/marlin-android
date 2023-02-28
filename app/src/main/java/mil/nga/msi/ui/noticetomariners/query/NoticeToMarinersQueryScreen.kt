package mil.nga.msi.ui.noticetomariners.query

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.ui.filter.ComparatorSelection
import mil.nga.msi.ui.filter.IntValue
import mil.nga.msi.ui.filter.LocationValue
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.add
import mil.nga.msi.ui.theme.remove

@Composable
fun NoticeToMarinersQueryScreen(
   close: () -> Unit,
   onQuery: () -> Unit,
   viewModel: NoticeToMarinersQueryViewModel = hiltViewModel()
) {
   val locationFilter by viewModel.locationFilter.observeAsState()
   val noticeFilter by viewModel.noticeFilter.observeAsState()
   val location by viewModel.locationPolicy.bestLocationProvider.observeAsState()

   Column(
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      TopBar(
         title = NoticeToMarinersRoute.Home.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      NoticeToMarinersChartCorrections(
         location = location,
         locationParameter = viewModel.locationParameter,
         locationFilter = locationFilter,
         noticeParameter = viewModel.noticeParameter,
         noticeFilter = noticeFilter,
         onAddLocationFilter = { viewModel.addLocationFilter(it) },
         onRemoveLocationFilter = { viewModel.removeLocationFilter() },
         onAddNoticeFilter = { viewModel.addNoticeFilter(it) },
         onRemoveNoticeFilter = { viewModel.removeNoticeFilter() }
      )

      Button(
         onClick = { onQuery() },
         enabled = locationFilter != null,
         modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
      ) {
         Text("Query")
      }
   }
}

@Composable
private fun NoticeToMarinersChartCorrections(
   location: Location?,
   locationParameter: FilterParameter,
   locationFilter: Filter?,
   noticeParameter: FilterParameter,
   noticeFilter: Filter?,
   onAddLocationFilter: (Filter) -> Unit,
   onRemoveLocationFilter: () -> Unit,
   onAddNoticeFilter: (Filter) -> Unit,
   onRemoveNoticeFilter: () -> Unit
) {
   Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
      LocationFilter(
         location = location,
         parameter = locationParameter,
         filter = locationFilter,
         onAdd = { onAddLocationFilter(it) },
         onRemove = { onRemoveLocationFilter() }
      )

      if (locationFilter != null) {
         NoticeFilter(
            parameter = noticeParameter,
            filter = noticeFilter,
            onAdd = { onAddNoticeFilter(it) },
            onRemove = { onRemoveNoticeFilter() }
         )
      }
   }
}

@Composable
private fun LocationFilter(
   location: Location?,
   parameter: FilterParameter,
   filter: Filter?,
   onAdd: (Filter) -> Unit,
   onRemove: () -> Unit
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "Required Filters",
         fontWeight = FontWeight.SemiBold,
         style = MaterialTheme.typography.titleSmall,
         modifier = Modifier.padding(top = 16.dp)
      )
   }

   if (filter == null) {
      LocationFilterSelect(
         location = location,
         parameter = parameter
      ) {
         onAdd(it)
      }

   } else {
      LocationFilterValue(filter = filter) {
         onRemove()
      }
   }
}

@Composable
private fun LocationFilterSelect(
   location: Location?,
   parameter: FilterParameter,
   onAdd: (Filter) -> Unit
) {
   val defaultComparator = parameter.type.comparators.first()
   var comparator by remember { mutableStateOf(defaultComparator) }
   var value by remember { mutableStateOf<Any?>(null) }

   Row(Modifier.padding(bottom = 16.dp)) {
      Text(
         text = "Location",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(end = 16.dp)
      )

      ComparatorSelection(
         parameter = parameter,
         selectedComparator = comparator,
         onSelectComparator = { comparator = it }
      )
   }

   Row(
      verticalAlignment= Alignment.Bottom
   ) {
      Column(Modifier.weight(1f)) {
         LocationValue(
            location = location,
            comparator = comparator,
            value = value.toString(),
            onValueChanged = {
               value = it
            }
         )
      }

      IconButton(
         onClick = {
            val values = if (value.toString().isNotEmpty()) value.toString().split(",") else emptyList()
            val latitude = values.getOrNull(0)?.toDoubleOrNull()
            val longitude = values.getOrNull(1)?.toDoubleOrNull()
            val distance = values.getOrNull(2)?.toDoubleOrNull()

            val filter = Filter(
               parameter = parameter,
               comparator = comparator,
               value = "$latitude,$longitude,$distance"
            )
            onAdd(filter)
         }
      ) {
         Icon(
            imageVector = Icons.Filled.AddCircle,
            tint = MaterialTheme.colorScheme.add,
            contentDescription = "Add Location Filter",
         )
      }
   }
}

@Composable
private fun LocationFilterValue(
   filter: Filter,
   removeFilter: () -> Unit
) {
   val value = filter.value.toString()
   val values = if (value.isNotEmpty()) value.split(",") else emptyList()
   val latitude = values.getOrNull(0)?.toDoubleOrNull()
   val longitude = values.getOrNull(1)?.toDoubleOrNull()
   val distance = values.getOrNull(2)?.toDoubleOrNull()

   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
   ) {
      Text(
         text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append("Location")
            }

            append(" within ")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append("$distance")
            }

            append(" of ")

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append("${latitude},${longitude}")
            }
         },
         modifier = Modifier.weight(1f))

      IconButton(
         onClick = { removeFilter() }
      ) {
         Icon(
            imageVector = Icons.Filled.RemoveCircle,
            tint = MaterialTheme.colorScheme.remove,
            contentDescription = "Remove Filter",
         )
      }
   }
}

@Composable
private fun NoticeFilter(
   parameter: FilterParameter,
   filter: Filter?,
   onAdd: (Filter) -> Unit,
   onRemove: () -> Unit
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "Additional Filters",
         fontWeight = FontWeight.SemiBold,
         style = MaterialTheme.typography.titleSmall,
         modifier = Modifier.padding(top = 16.dp)
      )
   }

   if (filter == null) {
      NoticeFilterSelect(
         parameter = parameter
      ) {
         onAdd(it)
      }

   } else {
      NoticeFilterValue(filter = filter) {
         onRemove()
      }
   }
}

@Composable
private fun NoticeFilterSelect(
   parameter: FilterParameter,
   onAdd: (Filter) -> Unit
) {
   val defaultComparator = parameter.type.comparators.first()
   var comparator by remember { mutableStateOf(defaultComparator) }
   var value by remember { mutableStateOf<Any?>(null) }

   Row(Modifier.padding(bottom = 16.dp)) {
      Text(
         text = "Notice Number",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(end = 16.dp)
      )

      ComparatorSelection(
         parameter = parameter,
         selectedComparator = comparator,
         onSelectComparator = { comparator = it }
      )
   }

   Row(
      verticalAlignment= Alignment.Bottom
   ) {
      Column(Modifier.weight(1f)) {
         IntValue(
            value = value?.toString().orEmpty(),
            onValueChanged = { value = it }
         )
      }

      IconButton(
         onClick = {
            val filter = Filter(
               parameter = parameter,
               comparator = comparator,
               value = value?.toString()?.toIntOrNull()
            )
            onAdd(filter)
         }
      ) {
         Icon(
            imageVector = Icons.Filled.AddCircle,
            tint = MaterialTheme.colorScheme.add,
            contentDescription = "Add Location Filter",
         )
      }
   }
}

@Composable
private fun NoticeFilterValue(
   filter: Filter,
   removeFilter: () -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
   ) {
      Text(
         text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append("Notice Number")
            }

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append(" ${filter.comparator.title} ")
            }

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
               append(filter.value.toString())
            }
         },
         modifier = Modifier.weight(1f))

      IconButton(
         onClick = { removeFilter() }
      ) {
         Icon(
            imageVector = Icons.Filled.RemoveCircle,
            tint = MaterialTheme.colorScheme.remove,
            contentDescription = "Remove Filter",
         )
      }
   }
}