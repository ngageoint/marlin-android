package mil.nga.msi.ui.map.filter

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun MapFilterScreen(
   close: () -> Unit,
   viewModel: MapFilterViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val dataSources by viewModel.dataSources.observeAsState(emptyList())

   Column(
      Modifier
         .fillMaxSize()
         .background(MaterialTheme.colors.screenBackground)
         .verticalScroll(scrollState)
   ) {
      TopBar(
         title = MapRoute.Filter.shortTitle,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      DataSources(dataSources = dataSources)
   }
}

@Composable
private fun DataSources(
   dataSources: List<MapFilterViewModel.DataSourceModel>
) {
   var expanded by remember { mutableStateOf<Map<DataSource, Boolean>>(emptyMap()) }

   Column() {
      dataSources.forEach { dataSourceModel ->
         DataSource(
            dataSourceModel = dataSourceModel,
            expand = expanded[dataSourceModel.dataSource] ?: false,
            onExpand = { expand ->
               expanded = expanded.toMutableMap().apply {
                  put(dataSourceModel.dataSource, expand)
               }
            }
         )

         Spacer(modifier = Modifier
            .height(8.dp)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.12f)))
      }
   }
}

@Composable
private fun DataSource(
   dataSourceModel: MapFilterViewModel.DataSourceModel,
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

   Column(Modifier.background(dataSourceModel.dataSource.color)) {
      Column(
         modifier = Modifier
            .padding(start = 8.dp)
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
               .height(72.dp)
               .background(MaterialTheme.colors.background)
               .clickable { onExpand(!expand) }
         ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               val bitmap = AppCompatResources.getDrawable(LocalContext.current, dataSourceModel.dataSource.icon)!!.toBitmap().asImageBitmap()
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
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                  Text(
                     text = mainRouteFor(dataSourceModel.dataSource).title,
                     style = MaterialTheme.typography.body2,
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
                           .background(MaterialTheme.colors.secondary)
                     ) {
                        Text(
                           text = dataSourceModel.numberOfFilters.toString(),
                           style = MaterialTheme.typography.body2,
                           modifier = Modifier.padding(horizontal = 8.dp),
                           color = MaterialTheme.colors.onPrimary
                        )
                     }
                  }

                  Icon(
                     imageVector = Icons.Default.ExpandMore,
                     tint = MaterialTheme.colors.primary,
                     modifier = Modifier.rotate(angle),
                     contentDescription = "Expand Filter"
                  )
               }
            }
         }

         Column(
            Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colors.background)
               .animateContentSize()
         ) {
            if (expand) {
               Filter(dataSource = dataSourceModel.dataSource)
            }
         }
      }
   }
}