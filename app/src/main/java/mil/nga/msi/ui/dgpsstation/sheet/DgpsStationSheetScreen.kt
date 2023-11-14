package mil.nga.msi.ui.dgpsstation.sheet

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel

@Composable
fun DgpsStationSheetScreen(
   key: DgpsStationKey,
   modifier: Modifier = Modifier,
   onDetails: () -> Unit,
   onShare: (DgpsStation) -> Unit,
   onBookmark: (DgpsStationWithBookmark) -> Unit,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   viewModel.setDgpsStationKey(key)
   val dgpsStationWithBookmark by viewModel.dgpsStationWithBookmark.observeAsState()

   Column(modifier = modifier) {
      DgpsStationContent(
         dgpsStationWithBookmark = dgpsStationWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStationWithBookmark: DgpsStationWithBookmark?,
   onDetails: () -> Unit,
   onShare: (DgpsStation) -> Unit,
   onBookmark: (DgpsStationWithBookmark) -> Unit,
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

      Row(horizontalArrangement = Arrangement.SpaceBetween) {
         TextButton(
            onClick = onDetails
         ) {
            Text("MORE DETAILS")
         }

         DataSourceActions(
            bookmarked = dgpsStationWithBookmark?.bookmark != null,
            onShare = { dgpsStationWithBookmark?.dgpsStation?.let { onShare(it) } },
            onBookmark = { dgpsStationWithBookmark?.let { onBookmark(it) } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}