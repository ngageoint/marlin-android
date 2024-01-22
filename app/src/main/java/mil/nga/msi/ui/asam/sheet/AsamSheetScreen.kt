package mil.nga.msi.ui.asam.sheet

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
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.asam.AsamViewModel
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon

@Composable
fun AsamSheetScreen(
   reference: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   onShare: ((Asam) -> Unit)? = null,
   onBookmark: ((AsamWithBookmark) -> Unit)? = null,
   onRoute: ((Asam) -> Unit)? = null,
   viewModel: AsamViewModel = hiltViewModel()
) {
   viewModel.setAsamReference(reference)
   val asamWithBookmark by viewModel.asamWithBookmark.observeAsState()

   Column(modifier = modifier) {
      AsamSheetContent(
         asamWithBookmark = asamWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark,
         onRoute = onRoute,
      )
   }
}

@Composable
private fun AsamSheetContent(
   asamWithBookmark: AsamWithBookmark?,
   onDetails: (() -> Unit)? = null,
   onShare: ((Asam) -> Unit)? = null,
   onBookmark: ((AsamWithBookmark) -> Unit)? = null,
   onRoute: ((Asam) -> Unit)? = null
) {
   Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DataSourceIcon(
         dataSource = DataSource.ASAM,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      asamWithBookmark?.let {
         AsamSummary(
            asamWithBookmark = it,
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
               onClick = { asamWithBookmark?.asam?.let { onRoute(it) }}
            ) {
               Text("ADD TO ROUTE")
            }
         }

         DataSourceActions(
            bookmarked = asamWithBookmark?.bookmark != null,
            onShare = onShare?.let { { asamWithBookmark?.asam?.let { onShare(it) } } },
            onBookmark = onBookmark?.let { { asamWithBookmark?.let { onBookmark(it) } } },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
         )
      }
   }
}