package mil.nga.msi.ui.radiobeacon.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel

@Composable
fun RadioBeaconSheetScreen(
   key: RadioBeaconKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   val beacon by viewModel.getRadioBeacon(key.volumeNumber, key.featureNumber).observeAsState()

   Column(modifier = modifier) {
      RadioBeaconContent(beacon = beacon) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun RadioBeaconContent(
   beacon: RadioBeacon?,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(48.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = DataSource.RADIO_BEACON.color)
         })

         Image(
            painter = painterResource(id = R.drawable.ic_baseline_settings_input_antenna_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "Radio Beacon icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "${beacon?.featureNumber.orEmpty()} ${beacon?.volumeNumber.orEmpty()}",
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         beacon?.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
            )
         }

         beacon?.sectionHeader?.let { sectionHeader ->
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = sectionHeader,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
         }

         beacon?.morseCode()?.let { code ->
            Text(
               text = beacon.morseLetter(),
               style = MaterialTheme.typography.titleLarge,
               modifier = Modifier.padding(top = 4.dp)
            )

            MorseCode(
               text = code,
               modifier = Modifier.padding(top = 4.dp)
            )
         }

         beacon?.expandedCharacteristicWithoutCode()?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 0.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            beacon?.stationRemark?.let { stationRemark ->
               Text(
                  text = stationRemark,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }
         }
      }

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
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