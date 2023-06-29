package mil.nga.msi.ui.light.sheet

import android.util.Log
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
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.datasource.light.LightsWithBookmark
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.light.LightViewModel

@Composable
fun LightSheetScreen(
   key: LightKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: LightViewModel = hiltViewModel()
) {
   Log.i("billy", "key is $key")

   viewModel.setLightKey(key)
   val lightsWithBookmark by viewModel.lightsWithBookmark.observeAsState()
   Log.i("billy", "light is $lightsWithBookmark")

   Column(modifier = modifier) {
      LightContent(lightsWithBookmark) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun LightContent(
   lightsWithBookmark: LightsWithBookmark?,
   onDetails: () -> Unit,
) {
   Column(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      lightsWithBookmark?.let {
         val lightWithBookmark = LightWithBookmark(
            light = lightsWithBookmark.lights.first(),
            bookmark = lightsWithBookmark.bookmark
         )

         DataSourceIcon(dataSource = DataSource.LIGHT)

         Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            LightSummary(lightWithBookmark = lightWithBookmark)
         }

         TextButton(
            onClick = { onDetails() }
         ) {
            Text("MORE DETAILS")
         }
      }
   }
}