package mil.nga.msi.ui.export

import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.Download
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.geopackage.export.ExportStatus
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.mainRouteFor
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun GeoPackageExportScreen(
   dataSource: DataSource?,
   close: () -> Unit,
   onExport: (Uri) -> Unit,
   viewModel: GeoPackageExportViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val counts by viewModel.counts.observeAsState(emptyMap())
   val filters by viewModel.filters.observeAsState(emptyMap())
   val dataSources by viewModel.dataSources.observeAsState(emptySet())
   val exportState by viewModel.exportState.observeAsState(ExportState.None)
   var showErrorDialog by remember { mutableStateOf(false) }

   LaunchedEffect(dataSource) {
      dataSource?.let { viewModel.toggleDataSource(it) }
   }

   if (showErrorDialog) {
      ExportErrorDialog() {
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
            Column {
               DataSources(
                  dataSources = dataSources,
                  counts = counts,
                  onDataSourceToggle = {
                     viewModel.toggleDataSource(it)
                  }
               )

               CommonFilters()
               DataSourceFilters(
                  dataSources = dataSources,
                  counts = counts,
                  exportState = exportState
               )
            }

            Box(
               Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp)
            ) {

               ExtendedFloatingActionButton(
                  containerColor = MaterialTheme.colorScheme.primary,
                  onClick = {
                     scope.launch {
                        viewModel.createGeoPackage()?.let { onExport(it) }
                     }
                  }
               ) {
                  Icon(
                     imageVector = Icons.Outlined.Download,
                     contentDescription = "Export as GeoPacakge"
                  )
                  Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                  Text("Export")
               }
            }
         }
      }
   }
}

@Composable
private fun DataSources(
   dataSources: Set<DataSource>,
   counts: Map<DataSource, Int>,
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
private fun CommonFilters() {
   Column(Modifier.padding(top = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "COMMON FILTERS",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 8.dp)
         )
      }
   }
}

@Composable
private fun DataSourceFilters(
   dataSources: Set<DataSource>,
   counts: Map<DataSource, Int>,
   exportState: ExportState
) {
   Column {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "DATA SOURCE FILTERS",
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
            count = counts[dataSource] ?: 0,
            exportStatus = exportStatus[dataSource]
         ) {

         }
      }
   }
}

@Composable
private fun DataSourceFilter(
   dataSource: DataSource,
   count: Int,
   exportStatus: ExportStatus?,
   onTap: () -> Unit
) {
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
                  .clickable { onTap() }
            ) {
               Box(
                  modifier = Modifier
                     .width(8.dp)
                     .fillMaxHeight()
                     .background(dataSource.color)
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
                     CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        Text(
                           text = mainRouteFor(dataSource).title,
                           style = MaterialTheme.typography.bodyMedium,
                           fontWeight = FontWeight.Medium
                        )
                     }

                     CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
                        Text(
                           text = count.toString(),
                           style = MaterialTheme.typography.bodyMedium,
                           fontWeight = FontWeight.SemiBold,
                           modifier = Modifier.padding(end = 16.dp)
                        )
                     }
                  }
               }
            }
         }

         exportStatus?.let { (total, complete) ->
//            Log.i("Billy", "UI Data source export complete ${complete / total.toFloat()}")
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
