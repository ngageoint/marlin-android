package mil.nga.msi.ui.electronicpublication

import android.text.format.Formatter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationWithBookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes

@Composable
fun ElectronicPublicationSummary(
   publicationWithBookmark: ElectronicPublicationWithBookmark
) {
   val (publication, bookmark) = publicationWithBookmark

   val fileType = publication.fileExtension?.toUpperCase(Locale.current) ?: "Unknown file type"

   val fileSize = publication.fileSize?.let {size ->
      Formatter.formatShortFileSize(LocalContext.current, size)
   } ?: "Unknown size"

   val uploadTime = publication.uploadTime?.let {
      "Uploaded ${ElectronicPublication.DATE_TIME_FORMATTER.format(publication.uploadTime)}"
   } ?: "Unknown upload time"

   Column(modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 16.dp)
   ) {
      Text(
         style = MaterialTheme.typography.titleMedium,
         text = publication.sectionDisplayName ?: ""
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            style = MaterialTheme.typography.bodyMedium,
            text = "$fileType - $fileSize"
         )
         Text(
            style = MaterialTheme.typography.bodySmall,
            text = uploadTime
         )
      }

      BookmarkNotes(
         notes = bookmark?.notes,
         modifier = Modifier.padding(top = 16.dp)
      )
   }
}