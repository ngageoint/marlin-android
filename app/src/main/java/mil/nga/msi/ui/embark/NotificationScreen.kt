package mil.nga.msi.ui.embark

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
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
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun NotificationScreen(
   done: () -> Unit,
   viewModel: EmbarkViewModel = hiltViewModel()
) {
   val notification by viewModel.notification.observeAsState()

   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      LaunchedEffect(notification) {
         done()
      }
   } else if (notification != true) {
      Notification {
         viewModel.setNotification()
      }
   } else {
      LaunchedEffect(notification) {
         done()
      }
   }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Notification(
   done: () -> Unit,
) {
   var height by remember { mutableIntStateOf(0) }
   val scrollState = rememberScrollState()

   val notificationPermissionState = rememberPermissionState(
      android.Manifest.permission.POST_NOTIFICATIONS
   )

   if (notificationPermissionState.status.isGranted) {
      done()
   }

   Surface(color = MaterialTheme.colorScheme.primary) {
      Column(
         Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
               brush = Brush.verticalGradient(
                  startY = height * .37f,
                  colors = listOf(
                     MaterialTheme.colorScheme.primary,
                     MaterialTheme.colorScheme.secondary
                  )
               )
            )
            .padding(vertical = 48.dp, horizontal = 32.dp)
            .onGloballyPositioned { coordinates ->
               height = coordinates.size.height
            }
      ) {
         Text(
            text = "Allow Notifications",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            modifier =
            Modifier
               .align(Alignment.CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Would you like to receive alerts when new data is available",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
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
                     Icons.Default.NotificationsActive,
                     modifier = Modifier.size(200.dp),
                     tint = Color.Black,
                     contentDescription = "Notifications icon"
                  )
               }
            }
         }

         Button(
            onClick = {
               notificationPermissionState.launchPermissionRequest()
            },
            shape = RoundedCornerShape(38.dp),
            modifier = Modifier
               .align(Alignment.CenterHorizontally)
               .padding(vertical = 16.dp)
         ) {
            Text(
               text = "Yes, Enable Notifications",
               style = MaterialTheme.typography.titleMedium,
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
               style = MaterialTheme.typography.titleMedium,
               fontSize = 18.sp,
               color = MaterialTheme.colorScheme.onPrimary
            )
         }
      }
   }
}