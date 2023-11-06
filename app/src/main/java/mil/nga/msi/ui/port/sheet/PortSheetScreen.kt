package mil.nga.msi.ui.port.sheet

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
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.port.PortSummary
import mil.nga.msi.ui.port.PortViewModel

@Composable
fun PortSheetScreen(
   portNumber: Int,
   modifier: Modifier = Modifier,
   onDetails: () -> Unit,
   onShare: (Port) -> Unit,
   onBookmark: (PortWithBookmark) -> Unit,
   viewModel: PortViewModel = hiltViewModel()
) {
   viewModel.setPortNumber(portNumber)
   val location by viewModel.locationProvider.observeAsState()
   val portWithBookmark by viewModel.portWithBookmark.observeAsState()

   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = DataSource.PORT,
            modifier = Modifier.padding(bottom = 16.dp)
         )

         portWithBookmark?.let {
            PortSummary(
               portWithBookmark = it,
               location = location,
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
               bookmarked = portWithBookmark?.bookmark != null,
               onShare = { portWithBookmark?.port?.let { onShare(it) } },
               onBookmark = { portWithBookmark?.let { onBookmark(it) } },
               modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
         }
      }
   }
}