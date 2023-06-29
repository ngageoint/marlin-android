package mil.nga.msi.ui.datasource

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
import mil.nga.msi.ui.coordinate.CoordinateTextButton

@Composable
fun DataSourceFooter(
   latLng: LatLng,
   bookmarked: Boolean,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      Location(latLng, onCopyLocation)
      Actions(
         bookmarked,
         onShare,
         onZoom,
         onBookmark
      )
   }
}

@Composable
private fun Location(
   latLng: LatLng,
   onCopyLocation: (String) -> Unit
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun Actions(
   bookmarked: Boolean,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit
) {
   Row {
      IconButton(onClick = { onBookmark() }) {
         Icon(
            imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Bookmark ASAM"
         )
      }
      IconButton(onClick = { onShare() }) {
         Icon(
            Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(
            Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}