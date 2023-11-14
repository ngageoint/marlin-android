package mil.nga.msi.ui.radiobeacon.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.radiobeacon.RadioBeaconSummary
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel

@Composable
fun RadioBeaconSheetScreen(
   key: RadioBeaconKey,
   modifier: Modifier = Modifier,
   onDetails: () -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onBookmark: (RadioBeaconWithBookmark) -> Unit,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   viewModel.setRadioBeaconKey(key)
   val beaconWithBookmark by viewModel.radioBeaconWithBookmark.observeAsState()

   Column(modifier = modifier) {
      RadioBeaconContent(
         beaconWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark
      )
   }
}

@Composable
private fun RadioBeaconContent(
   beaconWithBookmark: RadioBeaconWithBookmark?,
   onDetails: () -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onBookmark: (RadioBeaconWithBookmark) -> Unit,
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.RADIO_BEACON,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      beaconWithBookmark?.let {
         RadioBeaconSummary(
            beaconWithBookmark = it,
            modifier = Modifier.padding(bottom = 16.dp)
         )
      }

      Row(horizontalArrangement = Arrangement.SpaceBetween) {
         TextButton(
            onClick = onDetails
         ) {
            Text("MORE DETAILS")
         }

         DataSourceActions(
            bookmarked = beaconWithBookmark?.bookmark != null,
            onShare = { beaconWithBookmark?.radioBeacon?.let { onShare(it) } },
            onBookmark = { beaconWithBookmark?.let { onBookmark(it) } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}