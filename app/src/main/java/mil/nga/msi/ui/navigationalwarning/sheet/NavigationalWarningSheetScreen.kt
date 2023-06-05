package mil.nga.msi.ui.navigationalwarning.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningState
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningProperty
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NavigationalWarningSheetScreen(
   key: NavigationalWarningKey,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   val warning by viewModel.getNavigationalWarning(key).observeAsState()
   Column(modifier = modifier) {
      NavigationalWarningContent(warning = warning) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun NavigationalWarningContent(
   warning: NavigationalWarningState?,
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
            drawCircle(color = DataSource.NAVIGATION_WARNING.color)
         })

         Image(
            painter = painterResource(id = R.drawable.ic_round_warning_24),
            modifier = Modifier.size(24.dp),
            contentDescription = "Navigation Warning icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         warning?.let { state ->
            NavigationalWarningHeader(
               state = state,
            )
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

@Composable
private fun NavigationalWarningHeader(
   state: NavigationalWarningState
) {
   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

   Column {
      Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = dateFormat.format(state.warning.issueDate),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }

         val identifier = "${state.warning.number}/${state.warning.year}"
         val subregions = state.warning.subregions?.joinToString(",")?.let { "($it)" }
         val header = listOfNotNull(state.warning.navigationArea.title, identifier, subregions).joinToString(" ")
         Text(
            text = header,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )

         NavigationalWarningProperty(title = "Status", value = state.warning.status)
         NavigationalWarningProperty(title = "Authority", value = state.warning.authority)
         state.warning.cancelDate?.let { date ->
            NavigationalWarningProperty(title = "Cancel Date", value = dateFormat.format(date))
         }
      }
   }
}