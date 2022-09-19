package mil.nga.msi.ui.port.sheet

import android.location.Location
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.ui.location.generalDirection
import mil.nga.msi.ui.port.PortViewModel

@Composable
fun PortSheetScreen(
   id: String,
   onDetails: (() -> Unit)? = null,
   modifier: Modifier = Modifier,
   viewModel: PortViewModel = hiltViewModel()
) {
   val location by viewModel.locationProvider.observeAsState()

   id.toIntOrNull()?.let { portNumber ->
      val port by viewModel.getPort(portNumber).observeAsState()
      port?.let {
         Column(modifier = modifier) {
            PortContent(port = it, location = location) {
               onDetails?.invoke()
            }
         }
      }
   }
}

@Composable
private fun PortContent(
   port: Port,
   location: Location?,
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
            drawCircle(color = DataSource.PORT.color)
         })

         Image(
            painter = painterResource(id = mil.nga.msi.R.drawable.ic_baseline_anchor_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "Port icon",
         )
      }

      Column(
         modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
               text = port.portName,
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            port.alternateName?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(top = 4.dp)
               )
            }
         }

         location?.let { location ->
            Row(Modifier.padding(top = 4.dp)) {
               val portLocation = Location("port").apply {
                  latitude = port.latitude
                  longitude = port.longitude
               }

               val distance = location.distanceTo(portLocation) / 1000
               val direction = location.generalDirection(portLocation)
               val nmi = distance * 0.539957
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                  Text(
                     text = "${String.format("%.2f", nmi)}, $direction",
                     style = MaterialTheme.typography.body2,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }
      }

      TextButton(
         onClick = { onDetails() },
         Modifier.padding(start = 8.dp, top = 8.dp)
      ) {
         Text("MORE DETAILS")
      }
   }
}