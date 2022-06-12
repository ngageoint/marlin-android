package mil.nga.msi.ui.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagingSheet(
   mapAnnotations: List<MapAnnotation>,
   onDetails: (MapAnnotation) -> Unit,
) {
   Column {
      val pagerState = rememberPagerState()
      Text(
         text = "${pagerState.currentPage + 1} of ${mapAnnotations.size}",
         style = MaterialTheme.typography.body2,
         modifier = Modifier
            .padding(8.dp)
            .align(Alignment.CenterHorizontally),
      )

      HorizontalPagerIndicator(
         pagerState = pagerState,
         modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 16.dp),
      )

      HorizontalPager(
         count = mapAnnotations.size,
         state = pagerState,
         contentPadding = PaddingValues(horizontal = 0.dp),
         modifier = Modifier.fillMaxWidth(),
      ) { page ->
         Column(modifier = Modifier.fillMaxWidth()) {
            val annotation = mapAnnotations[page]
            when (annotation.key.type) {
               MapAnnotation.Type.ASAM -> AsamPage(annotation.key.id) { onDetails.invoke(annotation) }
               MapAnnotation.Type.MODU -> ModuPage(name = annotation.key.id) { onDetails.invoke(annotation) }
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
