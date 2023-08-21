package mil.nga.msi.ui.port.sheet

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
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.port.PortSummary
import mil.nga.msi.ui.port.PortViewModel

@Composable
fun PortSheetScreen(
   portNumber: Int,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: PortViewModel = hiltViewModel()
) {
   viewModel.setPortNumber(portNumber)
   val location by viewModel.locationProvider.observeAsState()
   val port by viewModel.portWithBookmark.observeAsState()

   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = DataSource.ASAM,
            modifier = Modifier.padding(bottom = 16.dp)
         )

         port?.let {
            PortSummary(
               portWithBookmark = it,
               location = location,
               modifier = Modifier.padding(bottom = 16.dp)
            )
         }

         TextButton(
            onClick = { onDetails?.invoke() }
         ) {
            Text("MORE DETAILS")
         }
      }
   }
}