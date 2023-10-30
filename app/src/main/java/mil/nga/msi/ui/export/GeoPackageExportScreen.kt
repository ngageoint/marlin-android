package mil.nga.msi.ui.export

import android.location.Location
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.geopackage.export.ExportStatus
import mil.nga.msi.ui.filter.FilterContent
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.mainRouteFor
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun GeoPackageExportScreen(
   exportDataSources: List<ExportDataSource>,
   close: () -> Unit,
   onExport: (Uri) -> Unit,
   viewModel: GeoPackageExportViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val scrollState = rememberScrollState()
   val counts by viewModel.counts.observeAsState(emptyMap())
   val commonFilters by viewModel.commonFilters.observeAsState(emptyList())
   val dataSourceFilters by viewModel.dataSourceFilters.observeAsState(emptyMap())
   val dataSources by viewModel.dataSources.observeAsState(emptySet())
   val exportState by viewModel.exportState.observeAsState(ExportState.None)
   var showErrorDialog by remember { mutableStateOf(false) }
   val location by viewModel.locationPolicy.bestLocationProvider.observeAsState()

   LaunchedEffect(exportDataSources) {
      viewModel.setExport(exportDataSources)
   }

   if (showErrorDialog) {
      ExportErrorDialog {
         showErrorDialog = false
      }
   }
   
   LaunchedEffect(exportState) {
      if (exportState is ExportState.Error) {
         showErrorDialog = true
      }
   }

   Column {
      TopBar(
         title = "GeoPackage Export",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant,
         modifier = Modifier.fillMaxHeight()
      ) {
         Box(Modifier.fillMaxWidth()) {
            Column(
               Modifier
                  .verticalScroll(scrollState)
                  .padding(bottom = 16.dp)
            ) {
               DataSources(
                  dataSources = dataSources,
                  onDataSourceToggle = {
                     viewModel.toggleDataSource(it)
                  }
               )

               CommonFilters(
                  filters = commonFilters,
                  filterParameters = viewModel.commonFilterParameters,
                  location = location,
                  exportState = exportState,
                  onAddFilter = { filter ->
                     val filters = commonFilters.toMutableList().apply { add(filter) }
                     viewModel.setCommonFilters(filters)
                  },
                  onRemoveFilter = { filter ->
                     val filters = commonFilters.toMutableList().apply { remove(filter) }
                     viewModel.setCommonFilters(filters)
                  }
               )
               DataSourceFilters(
                  dataSources = dataSources,
                  filters = dataSourceFilters,
                  filterParameters = viewModel.filterParameters,
                  location,
                  counts = counts,
                  exportState = exportState,
                  onAddFilter = { dataSource, filter ->
                     val added = dataSourceFilters[dataSource]?.toMutableList() ?: mutableListOf()
                     added.add(filter)
                     viewModel.setDataSourceFilters(dataSource, added)
                  },
                  onRemoveFilter = { dataSource, filter ->
                     val removed = dataSourceFilters[dataSource]?.toMutableList() ?: mutableListOf()
                     removed.remove(filter)
                     viewModel.setDataSourceFilters(dataSource, removed)
                  }
               )
            }

            Box(
               Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp)
            ) {
               ExportActionButton(
                  exportState = exportState,
                  onShare = { onExport(it) },
                  onCreate = {
                     scope.launch {
                        viewModel.createGeoPackage()?.let { onExport(it) }
                     }
                  }
               )
            }
         }
      }
   }
}

@Composable
private fun DataSources(
   dataSources: Set<DataSource>,
   onDataSourceToggle: (DataSource) -> Unit
) {
   Column {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "INCLUDED DATA SOURCES",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 8.dp)
         )
      }

      Row(
         horizontalArrangement = Arrangement.spacedBy(12.dp),
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         DataSource.values().filter { it.mappable }.forEach { dataSource ->
            DataSourceItem(
               dataSource = dataSource,
               selected = dataSources.contains(dataSource)
            ) {
               onDataSourceToggle(dataSource)
            }
         }
      }
   }
}

@Composable
private fun DataSourceItem(
   dataSource: DataSource,
   selected: Boolean?,
   onToggle: () -> Unit,
) {
   var tint =  MaterialTheme.colorScheme.onPrimary
   var background = dataSource.color
   val bitmap = AppCompatResources.getDrawable(LocalContext.current, dataSource.icon)!!.toBitmap().asImageBitmap()

   if (selected == false) {
      tint =  Color(0xFF999999)
      background = Color(0xFFDDDDDD)
   }

   FloatingActionButton(
      onClick = { onToggle() },
      containerColor = background,
      modifier = Modifier
         .size(40.dp)
   ) {
      Icon(
         bitmap = bitmap,
         tint = tint,
         modifier = Modifier.size(24.dp),
         contentDescription = "${mainRouteFor(dataSource).title} export"
      )
   }
}

@Composable
private fun CommonFilters(
   filters: List<Filter>,
   filterParameters: List<FilterParameter>,
   location: Location?,
   exportState: ExportState,
   onAddFilter: (Filter) -> Unit,
   onRemoveFilter: (Filter) -> Unit
) {
   var expanded by remember { mutableStateOf(false) }

   Column(
      Modifier
         .padding(top = 16.dp)
         .animateContentSize()) {
      if (exportState is ExportState.None) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "COMMON FILTERS",
               style = MaterialTheme.typography.titleMedium,
               modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 8.dp)
            )
         }
      }

      LocationFilter(
         filters = filters,
         filterParameters = filterParameters,
         location = location,
         expand = expanded,
         onExpand = { expanded = !expanded },
         onAddFilter = onAddFilter,
         onRemoveFilter = onRemoveFilter
      )
   }
}

@Composable
private fun DataSourceFilters(
   dataSources: Set<DataSource>,
   filters: Map<DataSource, List<Filter>>,
   filterParameters: Map<DataSource, List<FilterParameter>>,
   location: Location?,
   counts: Map<DataSource, Int?>,
   exportState: ExportState,
   onAddFilter: (DataSource, Filter) -> Unit,
   onRemoveFilter: (DataSource, Filter) -> Unit
) {
   var expanded by remember { mutableStateOf<Map<DataSource, Boolean>>(emptyMap()) }

   Column {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = if (exportState is ExportState.Creating) "EXPORT STATUS" else "DATA SOURCE FILTERS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 8.dp)
         )
      }

      dataSources.forEach { dataSource ->
         val exportStatus = when(exportState) {
            is ExportState.Creating -> exportState.status
            is ExportState.Complete -> exportState.status
            else -> emptyMap()
         }
         DataSourceFilter(
            dataSource = dataSource,
            filters = filters[dataSource] ?: emptyList(),
            filterParameters = filterParameters[dataSource] ?: emptyList(),
            count = counts[dataSource],
            exportStatus = exportStatus[dataSource],
            location = location,
            expand = expanded[dataSource] ?: false,
            onExpand = { expand ->
               expanded = expanded.toMutableMap().apply {
                  put(dataSource, expand)
               }
            },
            onAddFilter = onAddFilter,
            onRemoveFilter = onRemoveFilter
         )
      }
   }
}

@Composable
private fun LocationFilter(
   filters: List<Filter>,
   filterParameters: List<FilterParameter>,
   location: Location?,
   expand: Boolean,
   onExpand: (Boolean) -> Unit,
   onAddFilter: (Filter) -> Unit,
   onRemoveFilter: (Filter) -> Unit
) {
   val angle: Float by animateFloatAsState(
      targetValue = if (expand) 180F else 0F,
      animationSpec = tween(
         durationMillis = 250,
         easing = FastOutSlowInEasing
      ),
      label = "Filter Animation"
   )

   Surface(Modifier.fillMaxWidth()) {
      Column {
         Column(
            Modifier
               .height(56.dp)
               .fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .fillMaxSize()
                  .clickable { onExpand(!expand) }
            ) {
               Box(
                  modifier = Modifier
                     .width(8.dp)
                     .fillMaxHeight()
                     .background(MaterialTheme.colorScheme.primary)
               )

               Column(
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween,
                     modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                  ) {

                     Row {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                           Icon(
                              imageVector = Icons.Filled.LocationOn,
                              modifier = Modifier.padding(end = 8.dp),
                              contentDescription = "Location Filter Icon"
                           )
                        }

                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                           Text(
                              text = "Location",
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = FontWeight.Medium
                           )
                        }
                     }

                     Row(
                        Modifier.padding(horizontal = 16.dp)
                     ) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
                           Text(
                              text = if (filters.isEmpty()) "" else "${filters.size} Common Filters",
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = FontWeight.SemiBold,
                              modifier = Modifier.padding(end = 16.dp)
                           )
                        }

                        Icon(
                           imageVector = Icons.Default.ExpandMore,
                           tint = MaterialTheme.colorScheme.primary,
                           modifier = Modifier.rotate(angle),
                           contentDescription = "Expand Filter"
                        )
                     }
                  }
               }
            }
         }

         Column(
            Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colorScheme.background)
               .animateContentSize()
         ) {
            if (expand) {
               FilterContent(
                  location = location,
                  filters = filters,
                  filterParameters = filterParameters,
                  onAddFilter = { filter ->
                     if (filter.comparator == ComparatorType.NEAR_ME) {
                        // TODO test with location services off
                        val latitude = location?.latitude ?: 0.0
                        val longitude = location?.longitude ?: 0.0
                        val values = if (filter.value.toString().isNotEmpty()) filter.value.toString().split(",") else emptyList()
                        val distance = values.getOrNull(2)?.toDoubleOrNull()
                        val locationFilter = filter.copy(value = "${latitude},${longitude},${distance}")
                        onAddFilter(locationFilter)
                     }
                  },
                  onRemoveFilter = onRemoveFilter
               )
            }
         }
      }
   }
}

@Composable
private fun DataSourceFilter(
   dataSource: DataSource,
   filters: List<Filter>,
   filterParameters: List<FilterParameter>,
   count: Int?,
   exportStatus: ExportStatus?,
   location: Location?,
   expand: Boolean,
   onExpand: (Boolean) -> Unit,
   onAddFilter: (DataSource, Filter) -> Unit,
   onRemoveFilter: (DataSource, Filter) -> Unit
) {
   val angle: Float by animateFloatAsState(
      targetValue = if (expand) 180F else 0F,
      animationSpec = tween(
         durationMillis = 250,
         easing = FastOutSlowInEasing
      ),
      label = "Filter Animation"
   )

   Surface(Modifier.fillMaxWidth()) {
      Column {
         Column(
            Modifier
               .height(56.dp)
               .fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .fillMaxSize()
                  .clickable { onExpand(!expand) }
            ) {
               Box(
                  modifier = Modifier
                     .width(8.dp)
                     .fillMaxHeight()
                     .background(dataSource.color)
               )

               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  val bitmap = AppCompatResources.getDrawable(
                     LocalContext.current,
                     dataSource.icon
                  )!!.toBitmap().asImageBitmap()
                  Icon(
                     bitmap = bitmap,
                     modifier = Modifier.padding(start = 8.dp),
                     contentDescription = "Datasource Icon"
                  )
               }

               Column(
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween,
                     modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                  ) {
                     CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        Text(
                           text = mainRouteFor(dataSource).title,
                           style = MaterialTheme.typography.bodyMedium,
                           fontWeight = FontWeight.Medium
                        )
                     }

                     Row(
                        Modifier.padding(horizontal = 16.dp)
                     ) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
                           Text(
                              text = count?.toString().orEmpty(),
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = FontWeight.SemiBold,
                              modifier = Modifier.padding(end = 16.dp)
                           )
                        }

                        Icon(
                           imageVector = Icons.Default.ExpandMore,
                           tint = MaterialTheme.colorScheme.primary,
                           modifier = Modifier.rotate(angle),
                           contentDescription = "Expand Filter"
                        )
                     }
                  }
               }
            }
         }

         Column(
            Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colorScheme.background)
               .animateContentSize()
         ) {
            if (expand) {
               FilterContent(
                  location = location,
                  filters = filters,
                  filterParameters = filterParameters,
                  onAddFilter = { filter ->
                     onAddFilter(dataSource, filter)
                  },
                  onRemoveFilter = { filter ->
                     onRemoveFilter(dataSource, filter)
                  }
               )
            }
         }

         exportStatus?.let { (total, complete) ->
            LinearProgressIndicator(
               modifier = Modifier.fillMaxWidth(),
               progress = complete / total.toFloat()
            )
         }

         Divider(modifier = Modifier.fillMaxWidth())
      }
   }
}

@Composable
fun ExportErrorDialog(
   onDismiss: () -> Unit
) {
   AlertDialog(
      icon = {
         Icon(
            imageVector = Icons.Default.SentimentVeryDissatisfied,
            contentDescription = "Error Icon",
            modifier = Modifier.size(72.dp)
         )
      },
      title = {
         Text(text = "Export Error")
      },
      text = {
         Text(text = "We apologize, it looks like we were unable to export Marlin data for the selected data sources. Please try again later or reach out if this issue persists.")
      },
      onDismissRequest = { onDismiss() },
      confirmButton = {
         TextButton(onClick = { onDismiss() }) {
            Text("OK")
         }
      }
   )
}

@Composable
fun ExportActionButton(
   exportState: ExportState,
   onCreate: () -> Unit,
   onShare: (Uri) -> Unit
) {
   val scope = rememberCoroutineScope()

   when (exportState) {
      is ExportState.Complete -> {
         ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
               scope.launch {
                  onShare(exportState.uri)
               }
            }
         ) {
            Icon(
               imageVector = Icons.Outlined.Share,
               contentDescription = "Share GeoPackage"
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Share Export")
         }
      }
      is ExportState.None, is ExportState.Error -> {
         ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
               scope.launch {
                  onCreate()
               }
            }
         ) {
            Icon(
               imageVector = Icons.Outlined.Download,
               contentDescription = "Export as GeoPackage"
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Export")
         }
      }
      else -> {}
   }
}
