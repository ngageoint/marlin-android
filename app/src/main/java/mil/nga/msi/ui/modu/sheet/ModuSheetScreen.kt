package mil.nga.msi.ui.modu.sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.modu.ModuViewModel

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

      ModuSummary(modu = modu)

      TextButton(
         onClick = { onDetails() },
         modifier = Modifier.padding(horizontal = 16.dp)
      ) {
         Text("MORE DETAILS")
      }
   }
}