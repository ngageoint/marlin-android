package mil.nga.msi.ui.asam.sheet

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
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsamSheetScreen(
   reference: String,
   onDetails: (() -> Unit)? = null,
   viewModel: AsamViewModel = hiltViewModel()
) {
   val asam by viewModel.getAsam(reference).observeAsState()
   asam?.let {
      AsamContent(asam = it) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun AsamContent(
   asam: Asam,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(bottom = 8.dp)) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            asam.date.let { date ->
               val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
               Text(
                  text = dateFormat.format(date),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.overline,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }
         }

         val header = listOfNotNull(asam.hostility, asam.victim).joinToString(": ")
         Text(
            text = header,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            asam.description?.let {
               Text(
                  text = it,
                  maxLines = 5,
                  overflow = TextOverflow.Ellipsis,
                  style = MaterialTheme.typography.body2,
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