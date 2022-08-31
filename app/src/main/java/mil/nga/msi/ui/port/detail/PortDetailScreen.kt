package mil.nga.msi.ui.port.detail

import android.location.Location
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
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.location.generalDirection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.port.PortAction
import mil.nga.msi.ui.port.PortViewModel
import mil.nga.msi.ui.theme.screenBackground

fun Int.asNonZeroOrNull(): Int? {
   return if(this != 0) {
      this
   } else null
}

@Composable
fun PortDetailScreen(
   portNumber: Int,
   close: () -> Unit,
   onAction: (PortAction) -> Unit,
   viewModel: PortViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val location by viewModel.locationProvider.observeAsState()
   val port by viewModel.getPort(portNumber).observeAsState()

   Column {
      TopBar(
         title = "Port",
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      PortDetailContent(
         port = port,
         baseMap = baseMap,
         tileProvider = viewModel.tileProvider,
         location = location,
         onZoom = { onAction(PortAction.Zoom(it)) },
         onShare = { onAction(PortAction.Share(port.toString())) },
         onCopyLocation = { onAction(PortAction.Location(it)) }
      )
   }
}

@Composable
private fun PortDetailContent(
   port: Port?,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   location: Location?,
   onZoom: (Point) -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (port != null) {
      Surface(
         color = MaterialTheme.colors.screenBackground,
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .fillMaxWidth()
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            PortHeader(
               port = port,
               baseMap = baseMap,
               tileProvider = tileProvider,
               location = location,
               onZoom = onZoom,
               onShare = onShare,
               onCopyLocation = onCopyLocation
            )
            PortInformation(port)
         }
      }
   }
}

@Composable
private fun PortHeader(
   port: Port,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   location: Location?,
   onZoom: (Point) -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card {
      Column {
         MapClip(
            latLng = LatLng(port.latitude, port.longitude),
            baseMap = baseMap,
            tileProvider = tileProvider
         )

         Column {
            Row(
               Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
               Column(
                  Modifier.weight(1f)
               ) {
                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                     Text(
                        text = port.portName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                     )
                  }

                  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                     port.alternateName?.let {
                        Text(
                           text = it,
                           style = MaterialTheme.typography.body2,
                           modifier = Modifier.padding(top = 4.dp)
                        )
                     }
                  }
               }

               location?.let { location ->
                  Row {
                     val portLocation = Location("port").apply {
                        latitude = port.latitude
                        longitude = port.longitude
                     }

                     val distance = location.distanceTo(portLocation) / 1000
                     val direction = location.generalDirection(portLocation)
                     val nmi = distance * 0.539957
                     CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                           text = "${String.format("%.2f", nmi)}, $direction",
                           style = MaterialTheme.typography.body2,
                           modifier = Modifier.padding(top = 4.dp)
                        )
                     }
                  }
               }
            }

            PortFooter(
               port = port,
               onShare = onShare,
               onZoom = { onZoom(Point(port.latitude, port.longitude)) },
               onCopyLocation = onCopyLocation
            )
         }
      }
   }
}

@Composable
private fun PortFooter(
   port: Port,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      PortLocation(port.dms, onCopyLocation)
      PortActions(onZoom, onShare)
   }
}

@Composable
private fun PortLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun PortActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Port"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Port"
         )
      }
   }
}

@Composable
private fun PortInformation(
   port: Port
) {
   PortSection(
      title = "Name and Location",
      section = port.nameSection()
   )

   PortSection(
      title = "Depths",
      section = port.depthSection()
   )

   PortSection(
      title = "Maximum Vessel Size",
      section =  port.vesselSection()
   )

   PortSection(
      title = "Physical Environment",
      section = port.environmentSection()
   )

   PortSection(
      title = "Approach",
      section = port.approachSection()
   )

   PortSection(
      title = "Pilots, Tugs, Communications",
      section = port.pilotSection()
   )

   PortSection(
      title = "Facilities",
      section = port.facilitySection()
   )

   PortSection(
      title = "Cranes",
      section = port.craneSection()
   )

   PortSection(
      title = "Services and Supplies",
      section = port.serviceSection()
   )
}

@Composable
private fun PortSection(
   title: String,
   section: Map<String, String?>
) {
   if (section.any { entry -> entry.value?.isNotEmpty() == true }) {
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
               text = title,
               style = MaterialTheme.typography.subtitle1,
            )
         }

         Card(
            elevation = 4.dp,
            modifier = Modifier.padding(vertical = 8.dp)
         ) {
            Column() {
               section.forEach { entry ->
                  PortProperty(title = entry.key, value = entry.value)
               }
            }
         }
      }
   }
}

@Composable
private fun PortProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = title,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
               text = value,
               style = MaterialTheme.typography.body1
            )
         }
      }
   }
}