package mil.nga.msi.ui.asam.sheet

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
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.asam.AsamViewModel

@Composable
fun AsamSheetScreen(
   reference: String,
   modifier: Modifier = Modifier,
   onDetails: (() -> Unit)? = null,
   viewModel: AsamViewModel = hiltViewModel()
) {
   val asam by viewModel.getAsam(reference).observeAsState()

   Column(modifier = modifier) {
      Column(Modifier.padding(vertical = 8.dp)) {
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

         AsamSummary(asam = asam)

         TextButton(
            onClick = { onDetails?.invoke() },
            modifier = Modifier.padding(horizontal = 16.dp)
         ) {
            Text("MORE DETAILS")
         }
      }
   }
}