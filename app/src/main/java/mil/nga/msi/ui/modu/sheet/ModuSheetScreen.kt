package mil.nga.msi.ui.modu.sheet

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.modu.ModuViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModuSheetScreen(
   id: String,
   onDetails: (() -> Unit)? = null,
   viewModel: ModuViewModel = hiltViewModel()
) {
   val modu by viewModel.getModu(id).observeAsState()
   modu?.let {
      ModuContent(modu = it) {
         onDetails?.invoke()
      }
   }
}

@Composable
private fun ModuContent(
   modu: Modu,
   onDetails: () -> Unit,
) {
   Column(modifier = Modifier.padding(bottom = 8.dp)) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            modu.date.let { date ->
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

         Text(
            text = modu.name,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            modu.rigStatus?.let {
               Text(
                  text = it.name,
                  style = MaterialTheme.typography.body2,
                  modifier = Modifier.padding(top = 4.dp)
               )
            }
            modu.specialStatus?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.body2
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