package mil.nga.msi.ui.dgpsstation.sheet

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel

@Composable
fun DgpsStationSheetScreen(
   key: DgpsStationKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   val dgpsStation by viewModel.getDgpsStation(key.volumeNumber, key.featureNumber).observeAsState()

   Column(modifier = modifier) {
      DgpsStationContent(dgpsStation = dgpsStation) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStation: DgpsStation?,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      DataSourceIcon(dataSource = DataSource.DGPS_STATION)
      DgpsStationSummary(dgpsStation = dgpsStation)

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}