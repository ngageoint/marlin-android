package mil.nga.msi.ui.datasource

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource

@Composable
fun DataSourceIcon(
   dataSource: DataSource,
   modifier: Modifier = Modifier,
   iconSize: Int = 48
) {
   Column(modifier = modifier) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier.size(iconSize.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = dataSource.color)
         })

         val imageResourceId = when(dataSource) {
            DataSource.ASAM -> R.drawable.ic_asam_24dp
            DataSource.BOOKMARK -> R.drawable.ic_outline_bookmark_border_24
            DataSource.DGPS_STATION -> R.drawable.ic_dgps_icon_24
            DataSource.GEOPACKAGE -> R.drawable.ic_round_place_24
            DataSource.ELECTRONIC_PUBLICATION -> R.drawable.ic_description_24dp
            DataSource.LIGHT -> R.drawable.ic_baseline_lightbulb_24
            DataSource.MODU -> R.drawable.ic_modu_24dp
            DataSource.NAVIGATION_WARNING -> R.drawable.ic_round_warning_24
            DataSource.NOTICE_TO_MARINERS -> R.drawable.ic_baseline_campaign_24
            DataSource.PORT -> R.drawable.ic_baseline_anchor_24
            DataSource.RADIO_BEACON -> R.drawable.ic_baseline_settings_input_antenna_24
            DataSource.ROUTE -> R.drawable.ic_outline_directions_24dp
         }

         Image(
            painter = painterResource(id = imageResourceId),
            modifier = Modifier.size((iconSize / 2).dp),
            contentDescription = "Data source icon"
         )
      }
   }
}