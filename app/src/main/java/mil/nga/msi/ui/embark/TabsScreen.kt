package mil.nga.msi.ui.embark

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
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
   var height by remember { mutableStateOf(0) }

   Surface(color = MaterialTheme.colors.primary) {
      Column(
         Modifier
            .fillMaxSize()
            .background(
               brush = Brush.verticalGradient(
                  startY = height * .37f,
                  colors = listOf(
                     MaterialTheme.colors.primary,
                     MaterialTheme.colors.secondary
                  )
               )
            )
            .padding(vertical = 48.dp, horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
      ) {
         Text(
            text = "Marlin Tabs",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            modifier =
            Modifier
               .align(CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Choose up to 4 dataset tabs for the tab bar. Other datasets will be accessible in the side navigation menu",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.align(CenterHorizontally)
         )

         LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.padding(vertical = 32.dp)
         ) {
            items(DataSource.values().asList()) { dataSource ->
               Box(contentAlignment = Alignment.TopEnd) {
                  Card(
                     backgroundColor = MaterialTheme.colors.secondary,
                     modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clickable { toggleTab(dataSource) }
                  ) {
                     Column(Modifier.padding(12.dp)) {
                        Box(
                           contentAlignment = Center,
                           modifier = Modifier.fillMaxSize()
                        ) {
                           val onPrimaryColor = MaterialTheme.colors.onPrimary
                           Canvas(modifier = Modifier.size(48.dp), onDraw = {
                              drawCircle(color = onPrimaryColor)
                           })

                           Canvas(modifier = Modifier.size(45.dp), onDraw = {
                              drawCircle(color = dataSource.color)
                           })

                           Icon(
                              imageVector = ImageVector.vectorResource(id = dataSource.icon),
                              tint = MaterialTheme.colors.onPrimary,
                              modifier = Modifier.align(Center),
                              contentDescription = "${dataSource.route.shortTitle} icon"
                           )
                        }

                        Text(
                           text = dataSource.route.shortTitle,
                           style = MaterialTheme.typography.subtitle1,
                           textAlign = TextAlign.Center,
                           modifier = Modifier
                              .padding(top = 4.dp)
                              .align(CenterHorizontally),
                           color = MaterialTheme.colors.onPrimary
                        )
                     }
                  }

                  if (selectedTabs.contains(dataSource)) {
                     Box(
                        contentAlignment = Center,
                        modifier = Modifier.offset(x = (4).dp, y = (-4).dp)
                     ) {
                        val secondaryColor = MaterialTheme.colors.secondary
                        val onPrimaryColor = MaterialTheme.colors.onPrimary
                        Canvas(modifier = Modifier.size(24.dp), onDraw = {
                           drawCircle(color = onPrimaryColor)
                        })

                        Canvas(modifier = Modifier.size(21.dp), onDraw = {
                           drawCircle(color = secondaryColor)
                        })

                        Icon(
                           imageVector = Icons.Default.Check,
                           tint = MaterialTheme.colors.onPrimary,
                           modifier = Modifier.size(16.dp),
                           contentDescription = "${dataSource.route.shortTitle} selected"
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
               .padding(top = 32.dp)
         ) {
            Text(
               text = "Next",
               style = MaterialTheme.typography.subtitle1,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}