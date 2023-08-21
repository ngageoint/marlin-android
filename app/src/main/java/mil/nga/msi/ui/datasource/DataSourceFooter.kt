package mil.nga.msi.ui.datasource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.ui.coordinate.CoordinateTextButton

@Composable
fun DataSourceActions(
   bookmarked: Boolean,
   modifier: Modifier = Modifier,
   latLng: LatLng? = null,
   onZoom: (() -> Unit)? = null,
   onShare: (() -> Unit)? = null,
   onBookmark: (() -> Unit)? = null,
   onCopyLocation: ((String) -> Unit)? = null,
) {
   Column(modifier = modifier) {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.SpaceBetween,
         modifier = Modifier.fillMaxWidth()
      ) {
         if (latLng != null) {
            Location(latLng, onCopyLocation)
         }

         Actions(
            bookmarked,
            onShare = onShare,
            onZoom = onZoom,
            onBookmark = onBookmark
         )
      }
   }
}

@Composable
private fun Location(
   latLng: LatLng,
   onCopyLocation: ((String) -> Unit)? = null
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = {
         onCopyLocation?.invoke(it)
      }
   )
}

@Composable
private fun Actions(
   bookmarked: Boolean,
   onZoom: (() -> Unit)? = null,
   onShare: (() -> Unit)? = null,
   onBookmark: (() -> Unit)? = null,
) {
   Row(
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.fillMaxWidth()
   ) {
      if (onBookmark != null) {
         IconButton(onClick = { onBookmark() }) {
            Icon(
               imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Bookmark Data Source"
            )
         }
      }

      if (onShare != null) {
         IconButton(onClick = { onShare() }) {
            Icon(
               Icons.Default.Share,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Share Data Source"
            )
         }
      }

      if (onZoom != null) {
         IconButton(onClick = { onZoom() }) {
            Icon(
               Icons.Default.GpsFixed,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Zoom to Data Source"
            )
         }
      }
   }
}