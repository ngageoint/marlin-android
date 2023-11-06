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
fun TabsScreen(
   done: () -> Unit,
   viewModel: EmbarkViewModel = hiltViewModel()
) {
   val tabs by viewModel.tabs.observeAsState()
   val selectedTabs by viewModel.selectedTabs.observeAsState(emptyList())

   if (tabs != true) {
      Tabs(
         selectedTabs = selectedTabs,
         toggleTab = {
            viewModel.toggleTab(it)
         },
         done = {
            viewModel.setTabs()
         }
      )

   } else {
      LaunchedEffect(tabs) {
         done()
      }
   }
}

@Composable
private fun Tabs(
   selectedTabs: List<DataSource>,
   toggleTab: (DataSource) -> Unit,
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
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
            .padding(horizontal = 16.dp, vertical = verticalPadding)
      ) {
         Text(
            text = "Marlin Tabs",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            modifier =
            Modifier
               .align(CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Choose up to 4 dataset tabs for the tab bar. Other datasets will be accessible in the side navigation menu",
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
            items(DataSource.values().asList().filter { it.tab != null }) { dataSource ->
               Box(contentAlignment = Alignment.TopEnd) {
                  Card(
                     colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                     ),
                     modifier = Modifier.clickable { toggleTab(dataSource) }
                  ) {
                     Column(Modifier.padding(12.dp)) {
                        Box(
                           contentAlignment = Center,
                           modifier = Modifier.fillMaxSize()
                        ) {
                           val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                           Canvas(modifier = Modifier.size(38.dp), onDraw = {
                              drawCircle(color = onPrimaryColor)
                           })

                           Canvas(modifier = Modifier.size(35.dp), onDraw = {
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
                           text = dataSource.labelPlural,
                           style = MaterialTheme.typography.titleMedium,
                           textAlign = TextAlign.Center,
                           modifier = Modifier
                              .padding(top = 4.dp)
                              .align(CenterHorizontally),
                           color = MaterialTheme.colorScheme.onPrimary
                        )
                     }
                  }

                  if (selectedTabs.contains(dataSource)) {
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
               text = "Next",
               style = MaterialTheme.typography.titleMedium,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}