package mil.nga.msi.ui.embark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

   Surface(
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.onGloballyPositioned { coordinates ->
         height = coordinates.size.height
      }
   ) {
      Column(
         Modifier
            .fillMaxSize()
            .background(
               brush = Brush.verticalGradient(
                  startY = height * .37f,
                  colors = listOf(
                     MaterialTheme.colorScheme.primary,
                     MaterialTheme.colorScheme.secondary
                  )
               )
            )
            .verticalScroll(scrollState)
            .padding(vertical = 48.dp, horizontal = 32.dp)

      ) {
         Text(
            text = "Welcome to Marlin",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
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
               style = MaterialTheme.typography.titleMedium,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}