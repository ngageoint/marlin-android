package mil.nga.msi.ui.navigationalwarning

import android.util.Log
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
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningProperty
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NavigationalWarningSummary(
   navigationWarningWithBookmark: NavigationalWarningWithBookmark,
   modifier: Modifier = Modifier
) {
   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

   val (warning, bookmark) = navigationWarningWithBookmark
   Column(modifier = modifier) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = dateFormat.format(warning.issueDate),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      val identifier = "${warning.number}/${warning.year}"
      val subregions = warning.subregions?.joinToString(",")?.let { "($it)" }
      val header = listOfNotNull(warning.navigationArea.title, identifier, subregions).joinToString(" ")
      Text(
         text = header,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      NavigationalWarningProperty(title = "Status", value = warning.status)
      NavigationalWarningProperty(title = "Authority", value = warning.authority)
      warning.cancelDate?.let { date ->
         NavigationalWarningProperty(title = "Cancel Date", value = dateFormat.format(date))
      }

      Log.i("billy", "bookmark: $bookmark")
      BookmarkNotes(
         notes = bookmark?.notes,
         modifier = Modifier.padding(top = 16.dp)
      )
   }
}