package mil.nga.msi.ui.modu.sheet

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.modu.ModuViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ModuSheetScreen(
   id: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: ModuViewModel = hiltViewModel()
) {
   val modu by viewModel.getModu(id).observeAsState()
   Column(modifier = modifier) {
      ModuContent(modu = modu) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun ModuContent(
   modu: Modu?,
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
            drawCircle(color = DataSource.MODU.color)
         })

         Image(
            painter = painterResource(id = R.drawable.ic_modu_24dp),
            modifier = Modifier.size(24.dp),
            contentDescription = "MODO icon",
         )
      }

      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            modu?.date?.let { date ->
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

         modu?.name?.let { name ->
            Text(
               text = name,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            modu?.rigStatus?.let {
               Text(
                  text = it.name,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 4.dp)
               )
            }
            modu?.specialStatus?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.bodyMedium
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