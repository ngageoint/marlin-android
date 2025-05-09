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
   onDetails: (() -> Unit)? = null,
   onShare: ((NavigationalWarning) -> Unit)? = null,
   onBookmark: ((NavigationalWarningWithBookmark) -> Unit)? = null,
   onRoute: ((NavigationalWarning) -> Unit)? = null,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   viewModel.setWarningKey(key)
   val warningWithBookmark by viewModel.warningWithBookmark.observeAsState()

   Column(modifier = modifier) {
      NavigationalWarningSheetContent(
         navigationalWarningWithBookmark = warningWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark,
         onRoute = onRoute
      )
   }
}

@Composable
private fun NavigationalWarningSheetContent(
   navigationalWarningWithBookmark: NavigationalWarningWithBookmark?,
   onDetails: (() -> Unit)? = null,
   onShare: ((NavigationalWarning) -> Unit)? = null,
   onBookmark: ((NavigationalWarningWithBookmark) -> Unit)? = null,
   onRoute: ((NavigationalWarning) -> Unit)? = null
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
         onDetails?.let {
            TextButton(
               onClick = onDetails
            ) {
               Text("MORE DETAILS")
            }
         }

         onRoute?.let {
            TextButton(
               onClick = { navigationalWarningWithBookmark?.navigationalWarning?.let { onRoute(it) }}
            ) {
               Text("ADD TO ROUTE")
            }
         }

         DataSourceActions(
            bookmarked = navigationalWarningWithBookmark?.bookmark != null,
            onShare = onShare?.let { { navigationalWarningWithBookmark?.navigationalWarning?.let { onShare(it) } } },
            onBookmark = onBookmark?.let { { navigationalWarningWithBookmark?.let { onBookmark(it) } } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}