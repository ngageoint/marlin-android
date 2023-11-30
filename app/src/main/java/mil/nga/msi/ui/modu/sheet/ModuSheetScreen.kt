package mil.nga.msi.ui.modu.sheet

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.modu.ModuViewModel

@Composable
fun ModuSheetScreen(
   name: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   onShare: ((Modu) -> Unit)? = null,
   onBookmark: ((ModuWithBookmark) -> Unit)? = null,
   onRoute: ((Modu) -> Unit)? = null,
   viewModel: ModuViewModel = hiltViewModel()
) {
   viewModel.setName(name)
   val moduWithBookmark by viewModel.moduWithBookmark.observeAsState()
   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DataSourceIcon(
            dataSource = DataSource.MODU,
            modifier = Modifier.padding(bottom = 16.dp)
         )

         moduWithBookmark?.let {
            ModuSummary(
               moduWithBookmark = it,
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
                  onClick = { moduWithBookmark?.modu?.let { onRoute(it) }}
               ) {
                  Text("ADD TO ROUTE")
               }
            }

            DataSourceActions(
               bookmarked = moduWithBookmark?.bookmark != null,
               onShare = onShare?.let { { moduWithBookmark?.modu?.let { onShare(it) } } },
               onBookmark = onBookmark?.let { { moduWithBookmark?.let { onBookmark(it) } } },
               modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
         }
      }
   }
}