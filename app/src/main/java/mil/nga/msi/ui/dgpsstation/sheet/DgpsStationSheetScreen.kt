package mil.nga.msi.ui.dgpsstation.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel

@Composable
fun DgpsStationSheetScreen(
   key: DgpsStationKey,
   onDetails: (() -> Unit)? = null,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   val dgps by viewModel.getDgpsStation(key.volumeNumber, key.featureNumber).observeAsState()
   dgps?.let {
      DgpsStationContent(dgps = it) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun DgpsStationContent(
   dgps: DgpsStation,
   onDetails: () -> Unit,
) {

   Column(
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
   ) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "${dgps.featureNumber} ${dgps.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.overline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      dgps.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         dgps.remarks?.let { remarks ->
            Text(
               text = remarks,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 8.dp)
            )
         }
      }

      TextButton(
         onClick = { onDetails() }
      ) {
         Text("MORE DETAILS")
      }
   }
}