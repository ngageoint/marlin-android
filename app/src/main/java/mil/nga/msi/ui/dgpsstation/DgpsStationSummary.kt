package mil.nga.msi.ui.dgpsstation

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation

@Composable
fun DgpsStationSummary(
   dgpsStation: DgpsStation?
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "${dgpsStation?.featureNumber?.toString().orEmpty()} ${dgpsStation?.volumeNumber.orEmpty()}",
         fontWeight = FontWeight.SemiBold,
         style = MaterialTheme.typography.labelSmall,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis
      )
   }

   dgpsStation?.name?.let { name ->
      Text(
         text = name,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )
   }

   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      dgpsStation?.remarks?.let { remarks ->
         Text(
            text = remarks,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
         )
      }
   }
}