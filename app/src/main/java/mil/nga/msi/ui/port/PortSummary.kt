package mil.nga.msi.ui.port

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.location.generalDirection
import java.util.Locale

@Composable
fun PortSummary(
   portWithBookmark: PortWithBookmark,
   location: Location?,
   modifier: Modifier = Modifier
) {
   val (port, bookmark) = portWithBookmark

   Column(modifier = modifier) {
      Row(Modifier.fillMaxWidth()) {
         Column(Modifier.weight(1f)) {
            Text(
               text = port.portName,
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               port.alternateName?.let {
                  Text(
                     text = it,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }

            BookmarkNotes(
               notes = bookmark?.notes,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

         location?.let { location ->
            Row(Modifier.padding(top = 4.dp)) {
               val portLocation = Location("port").apply {
                  latitude = port.latitude
                  longitude = port.longitude
               }

               val distance = location.distanceTo(portLocation) / 1000
               val direction = location.generalDirection(portLocation)
               val nmi = distance * 0.539957
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "${String.format(Locale.getDefault(), "%.2f", nmi)}, $direction",
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }
      }
   }
}