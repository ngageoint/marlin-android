package mil.nga.msi.ui.navigationalwarning.sheet

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
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningSummary
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel

@Composable
fun NavigationalWarningSheetScreen(
   key: NavigationalWarningKey,
   modifier: Modifier = Modifier,
   onDetails: () -> Unit,
   onShare: (NavigationalWarning) -> Unit,
   onBookmark: (NavigationalWarningWithBookmark) -> Unit,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   viewModel.setWarningKey(key)
   val warningState by viewModel.warningState.observeAsState()

   Column(modifier = modifier) {
      NavigationalWarningSheetContent(
         navigationalWarningWithBookmark = warningState?.warningWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark
      )
   }
}

@Composable
private fun NavigationalWarningSheetContent(
   navigationalWarningWithBookmark: NavigationalWarningWithBookmark?,
   onDetails: () -> Unit,
   onShare: (NavigationalWarning) -> Unit,
   onBookmark: (NavigationalWarningWithBookmark) -> Unit,
) {
   Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.NAVIGATION_WARNING,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      navigationalWarningWithBookmark?.let {
         NavigationalWarningSummary(
            navigationWarningWithBookmark = it,
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
            bookmarked = navigationalWarningWithBookmark?.bookmark != null,
            onShare = { navigationalWarningWithBookmark?.navigationalWarning?.let { onShare(it) } },
            onBookmark = { navigationalWarningWithBookmark?.let { onBookmark(it) } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}