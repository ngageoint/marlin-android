package mil.nga.msi.ui.modu.sheet

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
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.modu.ModuViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModuSheetScreen(
   id: String,
   onDetails: (() -> Unit)? = null,
   modifier: Modifier = Modifier,
   viewModel: ModuViewModel = hiltViewModel()
) {
   val modu by viewModel.getModu(id).observeAsState()
   modu?.let {
      Column(modifier = modifier) {
         ModuContent(modu = it) {
            onDetails?.invoke()
         }
      }
   }
}

@Composable
private fun ModuContent(
   modu: Modu,
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