package mil.nga.msi.ui.radiobeacon.sheet

import androidx.compose.foundation.layout.Column
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
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.radiobeacon.RadioBeaconSummary
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel

@Composable
fun RadioBeaconSheetScreen(
   key: RadioBeaconKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   viewModel.setRadioBeaconKey(key)
   val beaconWithBookmark by viewModel.radioBeaconWithBookmark.observeAsState()

   Column(modifier = modifier) {
      RadioBeaconContent(beaconWithBookmark) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun RadioBeaconContent(
   beaconWithBookmark: RadioBeaconWithBookmark?,
   onDetails: () -> Unit,
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

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}