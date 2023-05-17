package mil.nga.msi.ui.navigationalwarning.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningState
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningViewModel
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningHeader

@Composable
fun NavigationalWarningSheetScreen(
   key: NavigationalWarningKey,
   onDetails: (() -> Unit)? = null,
   viewModel: NavigationalWarningViewModel = hiltViewModel()
) {
   val warning by viewModel.getNavigationalWarning(key).observeAsState()
   warning?.let {
      Column {
         NavigationalWarningContent(warning = it) {
            onDetails?.invoke()
         }
      }
   }
}

@Composable
private fun NavigationalWarningContent(
   warning: NavigationalWarningState,
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
         NavigationalWarningHeader(
            state = warning,
            showMap = false
         )
      }

      TextButton(
         onClick = { onDetails() },
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Text("MORE DETAILS")
      }
   }
}