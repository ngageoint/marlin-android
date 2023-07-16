package mil.nga.msi.ui.radiobeacon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.ui.bookmark.BookmarkNotes

@Composable
fun RadioBeaconSummary(
   beaconWithBookmark: RadioBeaconWithBookmark,
   modifier: Modifier = Modifier,
) {
   val (beacon, bookmark) = beaconWithBookmark

   Column(modifier = modifier) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "${beacon.featureNumber} ${beacon.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      beacon.name?.let { name ->
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
            text = beacon.sectionHeader,
            style = MaterialTheme.typography.bodyMedium,
         )
      }

      beacon.morseCode()?.let { code ->
         Text(
            text = beacon.morseLetter(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
         )

         MorseCode(
            text = code,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
         )
      }

      beacon.expandedCharacteristicWithoutCode()?.let {
         Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         beacon.stationRemark?.let { stationRemark ->
            Text(
               text = stationRemark,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 16.dp)
            )
         }
      }

      BookmarkNotes(
         notes = bookmark?.notes,
         modifier = Modifier.padding(top = 16.dp)
      )
   }
}

@Composable
private fun MorseCode(
   text: String,
   modifier: Modifier = Modifier,
) {
   Row(modifier = modifier) {
      text.split(" ").forEach { letter ->
         if (letter == "-" || letter == "•") {
            Box(
               modifier = Modifier
                  .padding(end = 8.dp)
                  .height(5.dp)
                  .width(if (letter == "-") 24.dp else 8.dp)
                  .background(MaterialTheme.colorScheme.onSurface)
            )
         }
      }
   }
}