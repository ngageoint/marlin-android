package mil.nga.msi.ui.light.sheet

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
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.light.LightState
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.light.LightViewModel

@Composable
fun LightSheetScreen(
   key: LightKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: LightViewModel = hiltViewModel()
) {
   viewModel.setLightKey(key)
   val lightState by viewModel.lightState.observeAsState()

   Column(modifier = modifier) {
      LightContent(lightState) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun LightContent(
   lightState: LightState?,
   onDetails: () -> Unit,
) {
   Column(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      if (lightState?.lightWithBookmark != null) {
         DataSourceIcon(dataSource = DataSource.LIGHT)

         Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            LightSummary(lightWithBookmark = lightState.lightWithBookmark)
         }

         TextButton(
            onClick = { onDetails() }
         ) {
            Text("MORE DETAILS")
         }
      }
   }
}