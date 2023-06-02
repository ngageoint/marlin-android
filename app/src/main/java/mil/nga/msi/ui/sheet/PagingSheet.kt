package mil.nga.msi.ui.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.dgpsstation.sheet.DgpsStationSheetScreen
import mil.nga.msi.ui.geopackage.sheet.GeoPackageFeatureSheetScreen
import mil.nga.msi.ui.light.sheet.LightSheetScreen
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.navigationalwarning.sheet.NavigationalWarningSheetScreen
import mil.nga.msi.ui.port.sheet.PortSheetScreen
import mil.nga.msi.ui.radiobeacon.sheet.RadioBeaconSheetScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagingSheet(
   mapAnnotations: List<MapAnnotation>,
   onDetails: (MapAnnotation) -> Unit,
   viewModel: PagingSheetViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val pagerState = androidx.compose.foundation.pager.rememberPagerState(
      pageCount = { mapAnnotations.size }
   )
   var badgeColor = remember(pagerState.currentPage) {
      mapAnnotations[pagerState.currentPage].key.type.route.color
   }

   Row(Modifier.height(280.dp)) {
      Box(
         Modifier
            .width(6.dp)
            .fillMaxHeight()
            .background(badgeColor)
      )

      Box {
         HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
         ) {
            val annotation = mapAnnotations[pagerState.currentPage]
            viewModel.annotationProvider.setMapAnnotation(annotation)
            badgeColor = annotation.key.type.route.color

            Column(modifier = Modifier.fillMaxWidth()) {
               when (annotation.key.type) {
                  MapAnnotation.Type.ASAM -> AsamPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.MODU -> ModuPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.LIGHT -> LightPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.PORT -> PortPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.RADIO_BEACON -> RadioBeaconPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.DGPS_STATION -> DgpsStationPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.NAVIGATIONAL_WARNING -> NavigationWarningPage(annotation.key.id) { onDetails(annotation) }
                  MapAnnotation.Type.GEOPACKAGE -> GeoPackageFeaturePage(annotation.key.id) { onDetails(annotation) }
               }
            }
         }

         Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
               .height(60.dp)
               .fillMaxWidth()
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
                  tint = if (previousEnabled) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.38f),
                  contentDescription = "Previous Page"
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "${pagerState.currentPage + 1} of ${mapAnnotations.size}",
                  style = MaterialTheme.typography.bodyLarge,
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
                  tint = MaterialTheme.colorScheme.primary,
                  contentDescription = "Next Page"
               )
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
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun ModuPage(
   name: String,
   onDetails: () -> Unit,
) {
   ModuSheetScreen(
      name,
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
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
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun PortPage(
   id: String,
   onDetails: () -> Unit,
) {
   id.toIntOrNull()?.let { portNumber ->
      PortSheetScreen(
         portNumber,
         onDetails = { onDetails() },
         modifier = Modifier.fillMaxHeight()
      )
   }
}

@Composable
private fun RadioBeaconPage(
   id: String,
   onDetails: () -> Unit,
) {
   val key = RadioBeaconKey.fromId(id)
   RadioBeaconSheetScreen(
      key = key,
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun DgpsStationPage(
   id: String,
   onDetails: () -> Unit,
) {
   val key = DgpsStationKey.fromId(id)
   DgpsStationSheetScreen(
      key,
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun NavigationWarningPage(
   id: String,
   onDetails: () -> Unit,
) {
   val key = NavigationalWarningKey.fromId(id)
   NavigationalWarningSheetScreen(
      key,
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun GeoPackageFeaturePage(
   id: String,
   onDetails: () -> Unit,
) {
   val key = GeoPackageFeatureKey.fromId(id)
   GeoPackageFeatureSheetScreen(
      key,
      onDetails = { onDetails() },
      modifier = Modifier.fillMaxHeight()
   )
}