package mil.nga.msi.ui.radiobeacon.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel

@Composable
fun RadioBeaconSheetScreen(
   key: RadioBeaconKey,
   onDetails: (() -> Unit)? = null,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   val beacon by viewModel.getRadioBeacon(key.volumeNumber, key.featureNumber).observeAsState()
   beacon?.let {
      RadioBeaconContent(beacon = it) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun RadioBeaconContent(
   beacon: RadioBeacon,
   onDetails: () -> Unit,
) {
   Column(
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .padding(vertical = 8.dp, horizontal = 16.dp)
   ) {
      Column {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "${beacon.featureNumber} ${beacon.volumeNumber}",
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.overline,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         beacon.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = beacon.sectionHeader,
               style = MaterialTheme.typography.body2
            )
         }

         beacon.morseCode()?.let { code ->
            Text(
               text = beacon.morseLetter(),
               style = MaterialTheme.typography.h6,
               modifier = Modifier.padding(top = 4.dp)
            )

            MorseCode(
               text = code,
               modifier = Modifier.padding(top = 4.dp)
            )
         }

         beacon.expandedCharacteristicWithoutCode()?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 0.dp)
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            beacon.stationRemark?.let { stationRemark ->
               Text(
                  text = stationRemark,
                  style = MaterialTheme.typography.body2,
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
                  .background(MaterialTheme.colors.onSurface)
            )
         }
      }
   }
}