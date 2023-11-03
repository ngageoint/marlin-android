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
   onDetails: () -> Unit,
   onShare: (Light) -> Unit,
   onBookmark: (LightWithBookmark) -> Unit,
   viewModel: LightViewModel = hiltViewModel()
) {
   viewModel.setLightKey(key)
   val lightState by viewModel.lightState.observeAsState()

   Column(modifier = modifier) {
      LightContent(
         lightWithBookmark = lightState?.lightWithBookmark,
         onDetails = onDetails,
         onShare = onShare,
         onBookmark = onBookmark
      )
   }
}

@Composable
private fun LightContent(
   lightWithBookmark: LightWithBookmark?,
   onDetails: () -> Unit,
   onShare: (Light) -> Unit,
   onBookmark: (LightWithBookmark) -> Unit,
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
            TextButton(
               onClick = onDetails
            ) {
               Text("MORE DETAILS")
            }

            DataSourceActions(
               bookmarked = lightWithBookmark.bookmark != null,
               onShare = { onShare(lightWithBookmark.light) },
               onBookmark = { onBookmark(lightWithBookmark) },
               modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
         }
      }
   }
}