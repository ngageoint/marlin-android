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
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
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
   viewModel.setDgpsStationKey(key)
   val dgpsStation by viewModel.dgpsStationWithBookmark.observeAsState()

   Column(modifier = modifier) {
      DgpsStationContent(dgpsStation) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStationWithBookmark: DgpsStationWithBookmark?,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.DGPS_STATION,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      dgpsStationWithBookmark?.let {
         DgpsStationSummary(
            dgpsStationWithBookmark = it,
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