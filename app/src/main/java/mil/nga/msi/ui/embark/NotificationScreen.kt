package mil.nga.msi.ui.embark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
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
   var height by remember { mutableStateOf(0) }
   val scrollState = rememberScrollState()

   val notificationPermissionState = rememberPermissionState(
      android.Manifest.permission.POST_NOTIFICATIONS
   )

   if (notificationPermissionState.status.isGranted) {
      done()
   }

   Surface(color = MaterialTheme.colors.primary) {
      Column(
         Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
            text = "Allow Notifications",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            modifier =
            Modifier
               .align(Alignment.CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Would you like to receive alerts when new data is available",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
         )

         Box(
            Modifier
               .padding(vertical = 96.dp)
               .align(Alignment.CenterHorizontally),
         ) {
            Icon(
               Icons.Default.NotificationsActive,
               modifier = Modifier.size(200.dp),
               tint = Color.Black,
               contentDescription = "Notifications icon"
            )
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