package mil.nga.msi.ui.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
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
import mil.nga.msi.ui.route.sheet.RouteSheetScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSheet(
   onDetails: (MapAnnotation) -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (BookmarkKey) -> Unit,
   viewModel: DataSourceSheetViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val mapAnnotations by viewModel.mapAnnotations.observeAsState(emptyList())
   val pagerState = androidx.compose.foundation.pager.rememberPagerState(
      pageCount = { mapAnnotations.size }
   )

   var badgeColor = remember(pagerState.currentPage, mapAnnotations) {
      mapAnnotations.getOrNull(pagerState.currentPage)?.key?.type?.color ?: Color.Transparent
   }

   Row(Modifier.height(400.dp)) {
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
            badgeColor = annotation.key.type.color

            Column(modifier = Modifier.fillMaxWidth()) {
               when (annotation.key.type) {
                  MapAnnotation.Type.ASAM -> {
                     AsamPage(
                        reference = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (asam, bookmark) ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromAsam(asam)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.MODU -> {
                     ModuPage(
                        name = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (modu, bookmark) ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromModu(modu)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.LIGHT -> {
                     LightPage(
                        id = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (light, bookmark) ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromLight(light)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.PORT -> {
                     PortPage(
                        id = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (port, bookmark)  ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromPort(port)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.RADIO_BEACON -> {
                     RadioBeaconPage(
                        id = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (beacon, bookmark)  ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromRadioBeacon(beacon)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.DGPS_STATION -> {
                     DgpsStationPage(
                        id = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (dgpsStation, bookmark)  ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromDgpsStation(dgpsStation)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.NAVIGATIONAL_WARNING -> {
                     NavigationWarningPage(
                        id = annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onShare = { onShare(it) },
                        onBookmark = { (warning, bookmark)  ->
                           if (bookmark == null) {
                              val key = BookmarkKey.fromNavigationalWarning(warning)
                              onBookmark(key)
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.GEOPACKAGE -> {
                     GeoPackageFeaturePage(
                        annotation.key.id,
                        onDetails = { onDetails(annotation) },
                        onBookmark = { key, bookmark ->
                           if (bookmark == null) {
                              onBookmark(BookmarkKey(key.id(), DataSource.GEOPACKAGE))
                           } else {
                              viewModel.deleteBookmark(bookmark)
                           }
                        }
                     )
                  }
                  MapAnnotation.Type.ROUTE -> {
                     RoutePage(
                        annotation.key.id,
                        onDetails = { onDetails(annotation) },
                     )
                  }
               }
            }
         }

         if (pagerState.pageCount > 1) {
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
}

@Composable
private fun AsamPage(
   reference: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (AsamWithBookmark) -> Unit
) {
   AsamSheetScreen(
      reference,
      onDetails = { onDetails() },
      onShare = { onShare("Share ASAM Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun ModuPage(
   name: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (ModuWithBookmark) -> Unit
) {
   ModuSheetScreen(
      name,
      onDetails = { onDetails() },
      onShare = { onShare("Share MODU Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun LightPage(
   id: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (LightWithBookmark) -> Unit
) {
   val key = LightKey.fromId(id)
   LightSheetScreen(
      key,
      onDetails = { onDetails() },
      onShare = { onShare("Share Light Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun PortPage(
   id: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (PortWithBookmark) -> Unit
) {
   id.toIntOrNull()?.let { portNumber ->
      PortSheetScreen(
         portNumber,
         onDetails = { onDetails() },
         onShare = { onShare("Share Port Information" to it.toString()) },
         onBookmark = onBookmark,
         modifier = Modifier.fillMaxHeight()
      )
   }
}

@Composable
private fun RadioBeaconPage(
   id: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (RadioBeaconWithBookmark) -> Unit
) {
   val key = RadioBeaconKey.fromId(id)
   RadioBeaconSheetScreen(
      key = key,
      onDetails = { onDetails() },
      onShare = { onShare("Share Radio Beacon Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun DgpsStationPage(
   id: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (DgpsStationWithBookmark) -> Unit
) {
   DgpsStationSheetScreen(
      key =  DgpsStationKey.fromId(id),
      onDetails = { onDetails() },
      onShare = { onShare("Share DGPS Station Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun NavigationWarningPage(
   id: String,
   onDetails: () -> Unit,
   onShare: (Pair<String, String>) -> Unit,
   onBookmark: (NavigationalWarningWithBookmark) -> Unit
) {
   val key = NavigationalWarningKey.fromId(id)
   NavigationalWarningSheetScreen(
      key,
      onDetails = { onDetails() },
      onShare = { onShare("Share Navigational Warning Information" to it.toString()) },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun GeoPackageFeaturePage(
   id: String,
   onDetails: () -> Unit,
   onBookmark: (GeoPackageFeatureKey, Bookmark?) -> Unit
) {
   val key = GeoPackageFeatureKey.fromId(id)
   GeoPackageFeatureSheetScreen(
      key,
      onDetails = { onDetails() },
      onBookmark = onBookmark,
      modifier = Modifier.fillMaxHeight()
   )
}

@Composable
private fun RoutePage(
   id: String,
   onDetails: () -> Unit,
) {
   id.toLongOrNull()?.let { routeId ->
      RouteSheetScreen(
         routeId,
         onDetails = { onDetails() },
         modifier = Modifier.fillMaxHeight()
      )
   }
}