package mil.nga.msi.ui.dgpsstation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.R
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun DgpsStationDetailScreen(
   key: DgpsStationKey,
   close: () -> Unit,
   onAction: (DgpsStationAction) -> Unit,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val dgpsStation by viewModel.getDgpsStation(key.volumeNumber, key.featureNumber).observeAsState()
   Column {
      TopBar(
         title = dgpsStation?.name ?: "",
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      RadioBeaconDetailContent(
         dgpsStation = dgpsStation,
         baseMap = baseMap,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(DgpsStationAction.Zoom(it)) },
         onShare = { onAction(DgpsStationAction.Share(it.toString())) },
         onCopyLocation = { onAction(DgpsStationAction.Location(it)) }
      )
   }
}

@Composable
private fun RadioBeaconDetailContent(
   dgpsStation: DgpsStation?,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: (Point) -> Unit,
   onShare: (DgpsStation) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (dgpsStation != null) {
      Surface(
         color = MaterialTheme.colors.screenBackground,
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            DgpsStationHeader(dgpsStation, baseMap, tileProvider, onZoom, onShare, onCopyLocation)
            DgpsStationInformation(dgpsStation)
         }
      }
   }
}

@Composable
private fun DgpsStationHeader(
   dgpsStation: DgpsStation,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: (Point) -> Unit,
   onShare: (DgpsStation) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card {
      Column {
         MapClip(
            latLng = LatLng(dgpsStation.latitude, dgpsStation.longitude),
            icon = R.drawable.asam_map_marker_24dp,
            baseMap = baseMap
         )

         Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = "${dgpsStation.featureNumber} ${dgpsStation.volumeNumber}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.overline,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            dgpsStation.name?.let { name ->
               Text(
                  text = name,
                  style = MaterialTheme.typography.h6,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
               )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = dgpsStation.sectionHeader,
                  style = MaterialTheme.typography.body2
               )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               dgpsStation.remarks?.let { remarks ->
                  Text(
                     text = remarks,
                     style = MaterialTheme.typography.body2,
                     modifier = Modifier.padding(top = 8.dp)
                  )
               }
            }

            DgpsStationFooter(
               dgpsStation,
               onZoom = { onZoom(Point(dgpsStation.latitude, dgpsStation.longitude))},
               onShare = { onShare(dgpsStation) },
               onCopyLocation)
         }
      }
   }
}

@Composable
private fun DgpsStationFooter(
   dgpsStation: DgpsStation,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      DgpsStationLocation(dgpsStation.dms, onCopyLocation)
      DgpsStationActions(onZoom, onShare)
   }
}

@Composable
private fun DgpsStationLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun DgpsStationActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Radio Beacon"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Radio Beacon"
         )
      }
   }
}


@Composable
private fun DgpsStationInformation(
   dgpsStation: DgpsStation
) {
   CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.subtitle1,
         modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
      )
   }

   Card(Modifier.fillMaxWidth()) {
      val information = dgpsStation.information()
      if (information.any { entry -> entry.value?.toString().orEmpty().isNotEmpty() }) {

         Column(Modifier.padding(8.dp)) {
            information.forEach { entry ->
               DgpsStationProperty(title = entry.key, value = entry.value)
            }
         }
      }
   }
}

@Composable
private fun DgpsStationProperty(
   title: String,
   value: Any?
) {
   if (value?.toString()?.isNotBlank() == true) {
      Column(Modifier.padding(bottom = 16.dp)) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = title,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
               text = value.toString().trim(),
               style = MaterialTheme.typography.body1
            )
         }
      }
   }
}