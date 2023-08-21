package mil.nga.msi.ui.geopackage

import androidx.compose.foundation.layout.Column
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
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes

@Composable
fun GeoPackageFeatureSummary(
   name: String,
   table: String,
   modifier: Modifier = Modifier,
   bookmark: Bookmark? = null
) {
   Column(modifier = modifier) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      Text(
         text = table,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
      )

      BookmarkNotes(
         notes = bookmark?.notes,
         modifier = Modifier.padding(top = 16.dp)
      )
   }
}