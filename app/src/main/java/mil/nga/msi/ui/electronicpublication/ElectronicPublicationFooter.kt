package mil.nga.msi.ui.electronicpublication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark

@Composable
fun ElectronicPublicationFooter(
   publicationWithBookmark: ElectronicPublicationWithBookmark,
   onAction: (PublicationAction) -> Unit
) {
   val (publication, bookmark) = publicationWithBookmark

   Row(modifier = Modifier.fillMaxWidth(),
      Arrangement.End
   ) {
      IconButton(onClick = { onAction(PublicationAction.Bookmark(publicationWithBookmark)) }) {
         Icon(
            imageVector = if (bookmark != null) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Bookmark electronic publication"
         )
      }

      if (publication.isDownloaded) {
         IconButton(onClick = { onAction(PublicationAction.View(publication)) }) {
            Icon(
               Icons.Default.Preview,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Preview electronic publication"
            )
         }

         IconButton(onClick = { onAction(PublicationAction.Delete(publication)) }) {
            Icon(
               Icons.Default.Delete,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Remove local copy of electronic publication"
            )
         }
      } else if (publication.isDownloading) {
         val progress = publication.fileSize?.let {
            publication.downloadedBytes.toDouble() / publication.fileSize
         } ?: -1.0
         if (progress > -1) {
            LinearProgressIndicator(
               progress = { progress.toFloat() },
               modifier = Modifier.align(Alignment.CenterVertically),
            )
         } else {
            LinearProgressIndicator(
               modifier = Modifier.align(Alignment.CenterVertically)
            )
         }
         IconButton(onClick = { onAction(PublicationAction.CancelDownload(publication)) }) {
            Icon(
               Icons.Default.Cancel,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Cancel Download electronic publication"
            )
         }
      } else {
         IconButton(onClick = { onAction(PublicationAction.Download(publication)) }) {
            Icon(
               Icons.Default.Download,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Download electronic publication"
            )
         }
      }
   }
}