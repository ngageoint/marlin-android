package mil.nga.msi.ui.navigationalwarning.sheet

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
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningSummary
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel

@Composable
fun NavigationalWarningSheetScreen(
   key: NavigationalWarningKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   viewModel.setWarningKey(key)
   val warningState by viewModel.warningState.observeAsState()

   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = DataSource.NAVIGATION_WARNING,
            modifier = Modifier.padding(bottom = 16.dp)
         )

         warningState?.let {
            NavigationalWarningSummary(
               navigationWarningWithBookmark = it.warningWithBookmark
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