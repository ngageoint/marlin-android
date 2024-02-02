package mil.nga.msi.ui.light.sheet

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
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.light.LightViewModel

@Composable
fun LightSheetScreen(
   key: LightKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   onShare: ((Light) -> Unit)? = null,
   onBookmark: ((LightWithBookmark) -> Unit)? = null,
   onRoute: ((Light) -> Unit)? = null,
   viewModel: LightViewModel = hiltViewModel()
) {
   viewModel.setLightKey(key)
   val lightState by viewModel.lightState.observeAsState()

   Column(modifier = modifier) {
      LightContent(
         lightWithBookmark = lightState?.lightWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark,
         onRoute = onRoute
      )
   }
}

@Composable
private fun LightContent(
   lightWithBookmark: LightWithBookmark?,
   onDetails: (() -> Unit)? = null,
   onShare: ((Light) -> Unit)? = null,
   onBookmark: ((LightWithBookmark) -> Unit)? = null,
   onRoute: ((Light) -> Unit)? = null,
) {
   Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
   ) {
      if (lightWithBookmark != null) {
         DataSourceIcon(
            dataSource = DataSource.LIGHT,
            modifier = Modifier.padding(bottom = 16.dp)
         )

         LightSummary(
            lightWithBookmark = lightWithBookmark,
            modifier = Modifier.padding(bottom = 16.dp)
         )

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
                  onClick = { onRoute(lightWithBookmark.light) }
               ) {
                  Text("ADD TO ROUTE")
               }
            }

            DataSourceActions(
               bookmarked = lightWithBookmark.bookmark != null,
               onShare = onShare?.let { { onShare(lightWithBookmark.light) } },
               onBookmark = onBookmark?.let { { onBookmark(lightWithBookmark) } },
               modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
         }
      }
   }
}