package mil.nga.msi.ui.modu

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
import mil.nga.msi.datasource.modu.Modu
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ModuSummary(
   modu: Modu?
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         modu?.date?.let { date ->
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

      modu?.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         modu?.rigStatus?.let {
            Text(
               text = it.name,
               style = MaterialTheme.typography.bodyLarge,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
         modu?.specialStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium
            )
         }
      }
   }
}