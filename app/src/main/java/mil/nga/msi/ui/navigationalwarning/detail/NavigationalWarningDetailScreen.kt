package mil.nga.msi.ui.navigationalwarning.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningAction
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NavigationalWarningDetailScreen(
   key: NavigationalWarningKey,
   close: () -> Unit,
   onAction: (NavigationalWarningAction) -> Unit,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   val warning by viewModel.getNavigationalWarning(key).observeAsState()
   Column {
      TopBar(
         title = "Navigational Warning",
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      NavigationalWarningDetailContent(
         warning = warning,
         onShare = { onAction(NavigationalWarningAction.Share(warning.toString())) }
      )
   }
}

@Composable
private fun NavigationalWarningDetailContent(
   warning: NavigationalWarning?,
   onShare: () -> Unit
) {
   if (warning != null) {
      Surface(
         color = MaterialTheme.colors.screenBackground,
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            NavigationalWarningHeader(warning, onShare)
            NavigationalWarningText(warning.text)
         }
      }
   }
}

@Composable
private fun NavigationalWarningHeader(
   warning: NavigationalWarning,
   onShare: () -> Unit
) {
   val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

   Card {
      Column {
         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               Text(
                  text = dateFormat.format(warning.issueDate),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.overline,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            val identifier = "${warning.number}/${warning.year}"
            val subregions = warning.subregions?.joinToString(",")?.let { "($it)" }
            val header = listOfNotNull(warning.navigationArea.title, identifier, subregions).joinToString(" ")
            Text(
               text = header,
               style = MaterialTheme.typography.h6,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
            
            NavigationalWarningProperty(title = "Status", value = warning.status)
            NavigationalWarningProperty(title = "Authority", value = warning.authority)
            warning.cancelDate?.let { date ->
               NavigationalWarningProperty(title = "Cancel Date", value = dateFormat.format(date))
            }

            NavigationalWarningFooter(onShare)
         }
      }
   }
}

@Composable
private fun NavigationalWarningFooter(
   onShare: () -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.fillMaxWidth()
   ) {
      NavigationalWarningActions(onShare)
   }
}


@Composable
private fun NavigationalWarningActions(
   onShare: () -> Unit
) {
   IconButton(onClick = { onShare() }) {
      Icon(Icons.Default.Share,
         tint = MaterialTheme.colors.primary,
         contentDescription = "Share Navigational Warning"
      )
   }
}

@Composable
private fun NavigationalWarningText(
   text: String?
) {
   Column(Modifier.padding(vertical = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
         Text(
            text = "Text",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium
         )
      }

      Card(
         elevation = 4.dp,
         modifier = Modifier.padding(vertical = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            text?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(all = 16.dp)
               )
            }
         }
      }
   }
}

@Composable
private fun NavigationalWarningProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(Modifier.padding(vertical = 8.dp)) {
         Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = value,
               style = MaterialTheme.typography.body1
            )
         }
      }
   }
}