package mil.nga.msi.ui.map.filter

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapRoute

@Composable
fun MapFilterScreen(
   onTap: (DataSource) -> Unit,
   close: () -> Unit,
   viewModel: MapFilterViewModel = hiltViewModel()
) {
   val dataSources by viewModel.dataSources.observeAsState(emptyList())

   Column(Modifier.fillMaxSize()) {
      TopBar(
         title = MapRoute.Filter.shortTitle,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      DataSources(
         dataSources = dataSources,
         onTap = onTap
      )
   }
}

@Composable
private fun DataSources(
   dataSources: List<MapFilterViewModel.DataSourceModel>,
   onTap: (DataSource) -> Unit
) {
   Column() {
      dataSources.forEach { dataSourceModel ->
         DataSource(
            dataSourceModel = dataSourceModel,
            onTap = { onTap(dataSourceModel.dataSource) }
         )
      }
   }
}

@Composable
private fun DataSource(
   dataSourceModel: MapFilterViewModel.DataSourceModel,
   onTap: () -> Unit
) {
   Row(
      modifier = Modifier
         .fillMaxWidth()
         .height(72.dp)
         .background(MaterialTheme.colors.background)
         .clickable { onTap() }
   ) {
      Box(
         modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(dataSourceModel.dataSource.color)
      )

      Column(
         verticalArrangement = Arrangement.Bottom,
         modifier = Modifier
            .fillMaxSize()) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .fillMaxWidth()
               .weight(1f)
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
                     text = dataSourceModel.dataSource.route.title,
                     style = MaterialTheme.typography.body2,
                     fontWeight = FontWeight.Medium
                  )
               }
            }

            if (dataSourceModel.numberOfFilters > 0) {
               Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                     .padding(end = 16.dp)
                     .clip(CircleShape)
                     .height(24.dp)
                     .background(MaterialTheme.colors.primary)
               ) {
                  Text(
                     text = dataSourceModel.numberOfFilters.toString(),
                     style = MaterialTheme.typography.body2,
                     modifier = Modifier.padding(horizontal = 8.dp),
                     color = MaterialTheme.colors.onPrimary
                  )
               }
            }
         }

         Divider(Modifier.fillMaxWidth())
      }
   }
}