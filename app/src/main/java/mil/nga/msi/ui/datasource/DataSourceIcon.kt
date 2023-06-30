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
   modifier: Modifier = Modifier
) {
   Column(modifier = modifier) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier.size(48.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = dataSource.color)
         })

         val imageResourceId = when(dataSource) {
            DataSource.ASAM -> R.drawable.ic_asam_24dp
            DataSource.DGPS_STATION -> R.drawable.ic_dgps_icon_24
            DataSource.LIGHT -> R.drawable.ic_baseline_lightbulb_24
            DataSource.MODU -> R.drawable.ic_modu_24dp
            DataSource.NAVIGATION_WARNING -> R.drawable.ic_round_warning_24
            else -> 1
         }

         Image(
            painter = painterResource(id = imageResourceId),
            modifier = Modifier.size(24.dp),
            contentDescription = "Data source icon"
         )
      }
   }
}