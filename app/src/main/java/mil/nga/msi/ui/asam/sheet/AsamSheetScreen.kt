package mil.nga.msi.ui.asam.sheet

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
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.asam.AsamViewModel

@Composable
fun AsamSheetScreen(
   reference: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: AsamViewModel = hiltViewModel()
) {
   viewModel.setAsamReference(reference)
   val asamWithBookmark by viewModel.asamWithBookmark.observeAsState()

   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.ASAM)

         asamWithBookmark?.let { AsamSummary(asamWithBookmark = it)}

         TextButton(
            onClick = { onDetails?.invoke() },
            modifier = Modifier.padding(horizontal = 16.dp)
         ) {
            Text("MORE DETAILS")
         }
      }
   }
}