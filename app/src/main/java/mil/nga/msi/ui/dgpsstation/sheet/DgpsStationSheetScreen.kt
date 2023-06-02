package mil.nga.msi.ui.dgpsstation.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel

@Composable
fun DgpsStationSheetScreen(
   key: DgpsStationKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   val dgps by viewModel.getDgpsStation(key.volumeNumber, key.featureNumber).observeAsState()

   Column(modifier = modifier) {
      DgpsStationContent(dgps = dgps) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun DgpsStationContent(
   dgps: DgpsStation?,
   onDetails: () -> Unit,
) {

   Column(modifier = Modifier.padding(vertical = 8.dp)) {
      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(48.dp)
      ) {
         Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawCircle(color = DataSource.DGPS_STATION.color)
         })

         Image(
            painter = painterResource(id = mil.nga.msi.R.drawable.ic_dgps_icon_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "DGPS Station icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "${dgps?.featureNumber?.toString().orEmpty()} ${dgps?.volumeNumber.orEmpty()}",
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         dgps?.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            dgps?.remarks?.let { remarks ->
               Text(
                  text = remarks,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }
         }
      }

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}