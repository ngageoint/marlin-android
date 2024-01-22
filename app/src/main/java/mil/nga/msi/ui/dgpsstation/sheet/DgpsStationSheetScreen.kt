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
   onDetails: (() -> Unit)? = null,
   onShare: ((DgpsStation) -> Unit)? = null,
   onBookmark: ((DgpsStationWithBookmark) -> Unit)? = null,
   onRoute: ((DgpsStation) -> Unit)? = null,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   viewModel.setDgpsStationKey(key)
   val dgpsStationWithBookmark by viewModel.dgpsStationWithBookmark.observeAsState()

   Column(modifier = modifier) {
      DgpsStationContent(
         dgpsStationWithBookmark = dgpsStationWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark,
         onRoute = onRoute
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStationWithBookmark: DgpsStationWithBookmark?,
   onDetails: (() -> Unit)? = null,
   onShare: ((DgpsStation) -> Unit)? = null,
   onBookmark: ((DgpsStationWithBookmark) -> Unit)? = null,
   onRoute: ((DgpsStation) -> Unit)? = null
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
         onDetails?.let {
            TextButton(
               onClick = onDetails
            ) {
               Text("MORE DETAILS")
            }
         }

         onRoute?.let {
            TextButton(
               onClick = { dgpsStationWithBookmark?.dgpsStation?.let { onRoute(it) }}
            ) {
               Text("ADD TO ROUTE")
            }
         }

         DataSourceActions(
            bookmarked = dgpsStationWithBookmark?.bookmark != null,
            onShare = onShare?.let { { dgpsStationWithBookmark?.dgpsStation?.let { onShare(it) } } },
            onBookmark = onBookmark?.let { { dgpsStationWithBookmark?.let { onBookmark(it) } } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}