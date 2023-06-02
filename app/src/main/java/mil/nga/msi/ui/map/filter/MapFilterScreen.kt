package mil.nga.msi.ui.map.filter

import android.location.Location
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.filter.Filter
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.mainRouteFor

@Composable
fun MapFilterScreen(
   close: () -> Unit,
   viewModel: MapFilterViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val dataSources by viewModel.dataSources.observeAsState(emptyList())
   val location by viewModel.locationPolicy.bestLocationProvider.observeAsState()

   Column {
      TopBar(
         title = MapRoute.Filter.shortTitle,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      Surface(
         color = MaterialTheme.colorScheme.surfaceVariant
      ) {
         Column(
            Modifier
               .fillMaxSize()
               .verticalScroll(scrollState)
         ) {
            DataSources(dataSources = dataSources, location = location)
         }
      }
   }
}

@Composable
private fun DataSources(
   dataSources: List<MapFilterViewModel.DataSourceModel>,
   location: Location?
) {
   var expanded by remember { mutableStateOf<Map<DataSource, Boolean>>(emptyMap()) }

   dataSources.forEach { dataSourceModel ->
      Column(Modifier.padding(bottom = 16.dp)) {
         DataSource(
            dataSourceModel = dataSourceModel,
            location = location,
            expand = expanded[dataSourceModel.dataSource] ?: false,
            onExpand = { expand ->
               expanded = expanded.toMutableMap().apply {
                  put(dataSourceModel.dataSource, expand)
               }
            }
         )
      }
   }
}

@Composable
private fun DataSource(
   dataSourceModel: MapFilterViewModel.DataSourceModel,
   location: Location?,
   expand: Boolean,
   onExpand: (Boolean) -> Unit
) {
   val angle: Float by animateFloatAsState(
      targetValue = if (expand) 180F else 0F,
      animationSpec = tween(
         durationMillis = 250,
         easing = FastOutSlowInEasing
      )
   )

   Surface(
      color = dataSourceModel.dataSource.color
   ) {
      Surface(
         modifier = Modifier.padding(start = 8.dp).fillMaxWidth()
      ) {
         Column(Modifier.fillMaxWidth()) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .fillMaxWidth()
                  .height(72.dp)
                  .clickable { onExpand(!expand) }
            ) {
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  val bitmap = AppCompatResources.getDrawable(
                     LocalContext.current,
                     dataSourceModel.dataSource.icon
                  )!!.toBitmap().asImageBitmap()
                  Icon(
                     bitmap = bitmap,
                     modifier = Modifier.padding(start = 8.dp),
                     contentDescription = "Navigation Tab Icon"
                  )
               }

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
                        text = mainRouteFor(dataSourceModel.dataSource).title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                     )
                  }

                  Row(modifier = Modifier.padding(end = 16.dp)) {
                     if (dataSourceModel.numberOfFilters > 0) {
                        Box(
                           contentAlignment = Alignment.Center,
                           modifier = Modifier
                              .padding(end = 16.dp)
                              .clip(CircleShape)
                              .height(24.dp)
                              .background(MaterialTheme.colorScheme.primary)
                        ) {
                           Text(
                              text = dataSourceModel.numberOfFilters.toString(),
                              style = MaterialTheme.typography.bodyMedium,
                              modifier = Modifier.padding(horizontal = 8.dp),
                              color = MaterialTheme.colorScheme.onPrimary
                           )
                        }
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

            Column(
               Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.background)
                  .animateContentSize()
            ) {
               if (expand) {
                  Filter(dataSource = dataSourceModel.dataSource, location = location)
               }
            }
         }
      }
   }
}