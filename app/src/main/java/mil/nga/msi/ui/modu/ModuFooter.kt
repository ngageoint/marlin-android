package mil.nga.msi.ui.modu

import androidx.compose.foundation.layout.Arrangement
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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.coordinate.CoordinateTextButton

@Composable
fun ModuFooter(
   modu: Modu,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      ModuLocation(modu.latLng, onCopyLocation)
      ModuActions(modu.bookmarked, onZoom, onShare, onBookmark)
   }
}

@Composable
private fun ModuLocation(
   latLng: LatLng,
   onCopyLocation: (String) -> Unit,
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun ModuActions(
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
            contentDescription = "Bookmark MODU"
         )
      }
      IconButton(onClick = { onShare() }) {
         Icon(
            Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share MODU"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(
            Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to MODU"
         )
      }
   }
}