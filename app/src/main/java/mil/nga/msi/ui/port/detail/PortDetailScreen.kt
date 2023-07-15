package mil.nga.msi.ui.port.detail

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.location.generalDirection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.action.PortAction
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.port.PortViewModel
import mil.nga.msi.ui.theme.onSurfaceDisabled

fun Int.asNonZeroOrNull(): Int? {
   return if(this != 0) {
      this
   } else null
}

@Composable
fun PortDetailScreen(
   portNumber: Int,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: PortViewModel = hiltViewModel()
) {
   val location by viewModel.locationProvider.observeAsState()
   val portWithBookmark by viewModel.portWithBookmark.observeAsState()
   viewModel.setPortNumber(portNumber)

   Column {
      TopBar(
         title = "Port",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      PortDetailContent(
         portWithBookmark = portWithBookmark,
         tileProvider = viewModel.tileProvider,
         location = location,
         onZoom = { onAction(PortAction.Zoom(it.latLng)) },
         onShare = { onAction(PortAction.Share(it)) },
         onBookmark = { (port, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromPort(port)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(PortAction.Location(it)) }
      )
   }
}

@Composable
private fun PortDetailContent(
   portWithBookmark: PortWithBookmark?,
   tileProvider: TileProvider,
   location: Location?,
   onZoom: (Port) -> Unit,
   onShare: (Port) -> Unit,
   onBookmark: (PortWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (portWithBookmark != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .fillMaxWidth()
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            PortHeader(
               portWithBookmark = portWithBookmark,
               tileProvider = tileProvider,
               location = location,
               onZoom = { onZoom(portWithBookmark.port) },
               onShare = { onShare(portWithBookmark.port) },
               onBookmark = { onBookmark(portWithBookmark) },
               onCopyLocation = onCopyLocation
            )
            PortInformation(portWithBookmark.port)
         }
      }
   }
}

@Composable
private fun PortHeader(
   portWithBookmark: PortWithBookmark,
   tileProvider: TileProvider,
   location: Location?,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (port, bookmark) = portWithBookmark

   Card {
      Column {
         Surface(
            color = DataSource.PORT.color,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
         ) {
            Text(
               text = port.portName,
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.padding(16.dp)
            )
         }

         MapClip(
            latLng = LatLng(port.latitude, port.longitude),
            tileProvider = tileProvider
         )

         Column {
            Row(
               Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
               Column(
                  Modifier.weight(1f)
               ) {
                  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                     port.alternateName?.let {
                        Text(
                           text = it,
                           style = MaterialTheme.typography.bodyMedium,
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
                     CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Text(
                           text = "${String.format("%.2f", nmi)}, $direction",
                           style = MaterialTheme.typography.bodyMedium,
                           modifier = Modifier.padding(top = 4.dp)
                        )
                     }
                  }
               }
            }

            BookmarkNotes(
               notes = bookmark?.notes,
               modifier = Modifier.padding(horizontal = 16.dp)
            )

            DataSourceFooter(
               latLng = port.latLng,
               bookmarked = bookmark != null,
               onZoom = onZoom,
               onShare = onShare,
               onBookmark = onBookmark,
               onCopyLocation = onCopyLocation,
               modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
         }
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
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
            Text(
               text = title,
               style = MaterialTheme.typography.titleMedium,
            )
         }

         Card(
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
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = title,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = value,
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}