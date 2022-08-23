package mil.nga.msi.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.light.sheet.LightSheetScreen
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.port.sheet.PortSheetScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagingSheet(
   mapAnnotations: List<MapAnnotation>,
   onDetails: (MapAnnotation) -> Unit,
) {
   val scope = rememberCoroutineScope()
   var badgeColor by remember { mutableStateOf(Color.Transparent) }

   Row(Modifier.height(280.dp)) {
      Box(
         Modifier
            .width(6.dp)
            .fillMaxHeight()
            .background(badgeColor)
      )

      Column {
         val pagerState = rememberPagerState()

         Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
         ) {
            val previousEnabled = pagerState.currentPage > 0
            IconButton(
               enabled = previousEnabled,
               onClick = {
                  scope.launch {
                     pagerState.animateScrollToPage(pagerState.currentPage - 1)
                  }
               })
            {
               Icon(
                  Icons.Default.ChevronLeft,
                  tint = if (previousEnabled) MaterialTheme.colors.primary else Color.Black.copy(alpha = LocalContentAlpha.current),
                  contentDescription = "Previous Page"
               )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = "${pagerState.currentPage + 1} of ${mapAnnotations.size}",
                  style = MaterialTheme.typography.body1,
                  modifier = Modifier
                     .padding(8.dp)
               )
            }

            IconButton(onClick = {
               scope.launch {
                  pagerState.animateScrollToPage(pagerState.currentPage + 1)
               }
            }) {
               Icon(
                  Icons.Default.ChevronRight,
                  tint = MaterialTheme.colors.primary,
                  contentDescription = "Next Page"
               )
            }
         }

         HorizontalPager(
            count = mapAnnotations.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier.fillMaxWidth(),
         ) { page ->
            val annotation = mapAnnotations[page]
            badgeColor = annotation.key.type.route.color

            Column(modifier = Modifier.fillMaxWidth()) {
               when (annotation.key.type) {
                  MapAnnotation.Type.ASAM -> AsamPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.MODU -> ModuPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.LIGHT -> LightPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.PORT -> PortPage(annotation.key.id) { onDetails(annotation) }
               }
            }
         }
      }
   }

}

@Composable
private fun AsamPage(
   reference: String,
   onDetails: () -> Unit,
) {
   AsamSheetScreen(
      reference,
      onDetails = { onDetails() }
   )
}

@Composable
private fun ModuPage(
   name: String,
   onDetails: () -> Unit,
) {
   ModuSheetScreen(
      name,
      onDetails = { onDetails() }
   )
}

@Composable
private fun LightPage(
   id: String,
   onDetails: () -> Unit,
) {
   val key = LightKey.fromId(id)
   LightSheetScreen(
      key,
      onDetails = { onDetails() }
   )
}

@Composable
private fun PortPage(
   id: String,
   onDetails: () -> Unit,
) {
   PortSheetScreen(
      id,
      onDetails = { onDetails() }
   )
}