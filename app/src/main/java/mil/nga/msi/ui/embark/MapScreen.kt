package mil.nga.msi.ui.embark

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource

@Composable
fun MapScreen(
   done: () -> Unit,
   viewModel: EmbarkViewModel = hiltViewModel()
) {
   val map by viewModel.map.observeAsState()
   val selected by viewModel.selectedMap.observeAsState(emptyMap())

   if (map != true) {
      Map(
         selected = selected,
         toggleMap = {
            viewModel.toggleMap(it)
         },
         done = {
            viewModel.setMap()
            viewModel.setEmbark()
         }
      )
   } else {
      LaunchedEffect(map) {
         done()
      }
   }
}

@Composable
private fun Map(
   selected: Map<DataSource, Boolean>,
   toggleMap: (DataSource) -> Unit,
   done: () -> Unit,
) {
   var height by remember { mutableIntStateOf(0) }
   val verticalPadding = when (LocalConfiguration.current.orientation) {
      Configuration.ORIENTATION_PORTRAIT -> 32.dp
      else -> 0.dp
   }

   Surface(color = MaterialTheme.colorScheme.primary) {
      Column(
         Modifier
            .fillMaxSize()
            .background(
               brush = Brush.verticalGradient(
                  startY = height * .37f,
                  colors = listOf(
                     MaterialTheme.colorScheme.primary,
                     MaterialTheme.colorScheme.secondary
                  )
               )
            )
            .padding(vertical = verticalPadding, horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
      ) {
         Text(
            text = "Marlin Map",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            modifier =
            Modifier
               .align(CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Choose what datasets you want to see on the map.  This can always be changed via the navigation menu later.",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(CenterHorizontally)
         )

         LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
            modifier = Modifier
               .padding(vertical = 8.dp)
               .weight(1f)
         ) {
            items(DataSource.entries.filter { it.tab != null && it.mappable }) { dataSource ->
               Box(contentAlignment = Alignment.TopEnd) {
                  Card(
                     colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                     ),
                     modifier = Modifier.clickable { toggleMap(dataSource) }
                  ) {
                     Column(Modifier.padding(12.dp)) {
                        Box(
                           contentAlignment = Center,
                           modifier = Modifier.fillMaxSize()
                        ) {
                           val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                           Canvas(modifier = Modifier.size(46.dp), onDraw = {
                              drawCircle(color = onPrimaryColor)
                           })

                           Canvas(modifier = Modifier.size(43.dp), onDraw = {
                              drawCircle(color = dataSource.color)
                           })

                           Icon(
                              imageVector = ImageVector.vectorResource(id = dataSource.icon),
                              tint = MaterialTheme.colorScheme.onPrimary,
                              modifier = Modifier.align(Center),
                              contentDescription = "${dataSource.label} icon"
                           )
                        }

                        Text(
                           text = dataSource.tab?.title ?: "",
                           style = MaterialTheme.typography.titleMedium,
                           textAlign = TextAlign.Center,
                           modifier = Modifier
                              .padding(top = 4.dp)
                              .align(CenterHorizontally),
                           color = MaterialTheme.colorScheme.onPrimary
                        )
                     }
                  }

                  if (selected[dataSource] == true) {
                     Box(
                        contentAlignment = Center,
                        modifier = Modifier.offset(x = (8).dp, y = (-8).dp)
                     ) {
                        val secondaryColor = MaterialTheme.colorScheme.secondary
                        val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                        Canvas(modifier = Modifier.size(24.dp), onDraw = {
                           drawCircle(color = onPrimaryColor)
                        })

                        Canvas(modifier = Modifier.size(21.dp), onDraw = {
                           drawCircle(color = secondaryColor)
                        })

                        Icon(
                           imageVector = Icons.Default.Check,
                           tint = MaterialTheme.colorScheme.onPrimary,
                           modifier = Modifier.size(16.dp),
                           contentDescription = "${dataSource.labelPlural} selected"
                        )
                     }
                  }
               }
            }
         }

         Button(
            onClick = { done() },
            shape = RoundedCornerShape(38.dp),
            modifier = Modifier
               .align(CenterHorizontally)
               .padding(bottom = 8.dp)
         ) {
            Text(
               text = "Take Me To Marlin",
               style = MaterialTheme.typography.titleMedium,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}