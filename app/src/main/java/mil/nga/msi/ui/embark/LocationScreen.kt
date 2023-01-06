package mil.nga.msi.ui.embark

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun LocationScreen(
   done: () -> Unit,
   viewModel: EmbarkViewModel = hiltViewModel()
) {
   val location by viewModel.location.observeAsState()

   if (location != true) {
      Location {
         viewModel.setLocation()
      }
   } else {
      LaunchedEffect(location) {
         done()
      }
   }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Location(
   done: () -> Unit,
) {
   var height by remember { mutableStateOf(0) }
   val scrollState = rememberScrollState()
   val locationPermissionState = rememberMultiplePermissionsState(
      listOf(
         android.Manifest.permission.ACCESS_FINE_LOCATION,
         android.Manifest.permission.ACCESS_COARSE_LOCATION
      )
   )

   if (locationPermissionState.permissions.any { it.status == PermissionStatus.Granted }) {
      done()
   }

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
            .padding(vertical = 48.dp, horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
      ) {
         Text(
            text = "Enable Location",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            modifier =
            Modifier
               .align(Alignment.CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Marlin can show your location on the map and provide location aware filtering. Would you like to allow Marlin to access your location?",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
         )

         Box(Modifier.weight(1f)) {
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
               Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                     .padding(vertical = 96.dp)
                     .fillMaxSize()
               ) {
                  Icon(
                     Icons.Default.NearMe,
                     modifier = Modifier.size(200.dp),
                     tint = Color.Black,
                     contentDescription = "Location icon"
                  )
               }
            }
         }

         Button(
            onClick = {
               locationPermissionState.launchMultiplePermissionRequest()
            },
            shape = RoundedCornerShape(38.dp),
            modifier = Modifier
               .align(Alignment.CenterHorizontally)
               .padding(vertical = 16.dp)
         ) {
            Text(
               text = "Yes, Enable My Location",
               style = MaterialTheme.typography.subtitle1,
               fontSize = 18.sp,
               modifier = Modifier.padding(8.dp)
            )
         }

         TextButton(
            onClick = { done() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
         ) {
            Text(
               text = "Not Now",
               style = MaterialTheme.typography.subtitle1,
               fontSize = 18.sp,
               color = MaterialTheme.colors.onPrimary
            )
         }
      }
   }
}