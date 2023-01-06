package mil.nga.msi.ui.embark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.ui.settings.EndorsementDisclaimer
import mil.nga.msi.ui.settings.LegalDisclaimer
import mil.nga.msi.ui.settings.LiabilityDisclaimer
import mil.nga.msi.ui.settings.SecurityPolicy

@Composable
fun DisclaimerScreen(
   done: () -> Unit,
   viewModel: EmbarkViewModel = hiltViewModel()
) {
   val disclaimer by viewModel.disclaimer.observeAsState()

   if (disclaimer != true) {
      Disclaimer {
         viewModel.setDisclaimer()
      }
   } else {
      LaunchedEffect(disclaimer) {
         done()
      }
   }
}

@Composable
private fun Disclaimer(
   done: () -> Unit,
) {
   var height by remember { mutableStateOf(0) }
   val scrollState = rememberScrollState()

   Surface(color = MaterialTheme.colors.primary) {
      Column(
         Modifier
            .fillMaxSize()
            .background(
               brush = Brush.verticalGradient(
                  startY = height * .37f,
                  colors = listOf(
                     MaterialTheme.colors.primary,
                     MaterialTheme.colors.secondary
                  )
               )
            )
            .verticalScroll(scrollState)
            .padding(vertical = 48.dp, horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
      ) {
         Text(
            text = "Welcome to Marlin",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            modifier =
            Modifier
               .align(Alignment.CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         LegalDisclaimer()
         SecurityPolicy()
         LiabilityDisclaimer()
         EndorsementDisclaimer()

         Button(
            onClick = { done() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(38.dp)
         ) {
            Text(
               text = "Accept",
               style = MaterialTheme.typography.subtitle1,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}