package mil.nga.msi.ui.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BookmarkNotes(
   notes: String?,
   modifier: Modifier = Modifier
) {
   notes?.let {
      if (it.isNotBlank()) {
         Column(modifier = modifier) {
            Notes(notes = it)
         }
      }
   }
}

@Composable
private fun Notes(
   notes: String
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "BOOKMARK NOTES",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(bottom = 4.dp)
      )

      Text(
         text = notes,
         style = MaterialTheme.typography.bodyMedium
      )
   }
}