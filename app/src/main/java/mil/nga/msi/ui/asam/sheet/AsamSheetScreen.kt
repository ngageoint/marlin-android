package mil.nga.msi.ui.asam.sheet

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
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsamSheetScreen(
   reference: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: AsamViewModel = hiltViewModel()
) {
   val asam by viewModel.getAsam(reference).observeAsState()
   Column(modifier = modifier) {
      AsamContent(asam = asam) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun AsamContent(
   asam: Asam?,
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
            drawCircle(color = DataSource.ASAM.color)
         })

         Image(
            painter = painterResource(id = R.drawable.ic_asam_24dp),
            modifier = Modifier.size(24.dp),
            contentDescription = "ASAM icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            asam?.date?.let { date ->
               val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
               Text(
                  text = dateFormat.format(date),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         }

         val header = listOfNotNull(asam?.hostility, asam?.victim)
         if (header.isNotEmpty()) {
            Text(
               text = header.joinToString(": "),
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }


         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            asam?.description?.let {
               Text(
                  text = it,
                  maxLines = 5,
                  overflow = TextOverflow.Ellipsis,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }
         }
      }

      TextButton(
         onClick = { onDetails() },
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Text("MORE DETAILS")
      }
   }
}