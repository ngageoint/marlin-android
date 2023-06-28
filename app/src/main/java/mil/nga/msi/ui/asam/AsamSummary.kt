package mil.nga.msi.ui.asam

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
import mil.nga.msi.datasource.asam.Asam
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AsamSummary(
   asam: Asam?
) {
   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            asam?.date?.let { date ->
               val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
               Text(
                  text = dateFormat.format(date),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         }

         val header = listOfNotNull(asam?.hostility, asam?.victim)
         if (header.isNotEmpty()) {
            Text(
               text = header.joinToString(": "),
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }


         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            asam?.description?.let {
               Text(
                  text = it,
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }

            asam?.bookmarkNotes?.let { notes ->
               Text(
                  text = "Bookmark Notes",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
               )

               Text(
                  text = notes,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }
      }
   }
}