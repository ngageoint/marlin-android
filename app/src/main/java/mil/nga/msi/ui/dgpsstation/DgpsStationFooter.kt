package mil.nga.msi.ui.dgpsstation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.ui.coordinate.CoordinateTextButton

@Composable
fun DgpsStationFooter(
   dgpsStationWithBookmark: DgpsStationWithBookmark,
   onBookmark: () -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (dgpsStation, bookmark) = dgpsStationWithBookmark

   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      DgpsStationLocation(dgpsStation.latLng, onCopyLocation)
      DgpsStationActions(
         bookmarked = bookmark != null,
         onBookmark,
         onShare,
         onZoom
      )
   }
}

@Composable
private fun DgpsStationLocation(
   latLng: LatLng,
   onCopyLocation: (String) -> Unit
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun DgpsStationActions(
   bookmarked: Boolean,
   onBookmark: () -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(onClick = { onBookmark() }) {
         Icon(
            imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Bookmark DGPS Station"
         )
      }
      IconButton(onClick = { onShare() }) {
         Icon(
            Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share DGPS Station"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(
            Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to DGPS Station"
         )
      }
   }
}