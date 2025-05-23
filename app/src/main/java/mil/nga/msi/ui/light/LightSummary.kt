package mil.nga.msi.ui.light

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
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes

@Composable
fun LightSummary(
   lightWithBookmark: LightWithBookmark,
   modifier: Modifier = Modifier
) {
   val (light, bookmark) = lightWithBookmark

   Column(modifier = modifier) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "${light.featureNumber} ${light.internationalFeature.orEmpty()} ${light.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      light.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = light.sectionHeader,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
         )

         light.structure?.let { structure ->
            Text(
               text = structure,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      BookmarkNotes(notes = bookmark?.notes)
   }
}