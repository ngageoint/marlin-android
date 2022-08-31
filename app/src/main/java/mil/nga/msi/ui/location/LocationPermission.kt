package mil.nga.msi.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(
   locationPermissionState: PermissionState
) {
   val shouldShowRationale by remember { mutableStateOf(locationPermissionState.status.shouldShowRationale) }

   val lifecycleOwner = LocalLifecycleOwner.current
   DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
         if (event == Lifecycle.Event.ON_START) {
            if (!shouldShowRationale) {
               locationPermissionState.launchPermissionRequest()
            }
         }
      }
      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
         lifecycleOwner.lifecycle.removeObserver(observer)
      }
   }

   when {
      shouldShowRationale && locationPermissionState.status.shouldShowRationale -> {
         LocationPermissionDeniedDialog(
            permissionState = locationPermissionState
         ) {
            locationPermissionState.launchPermissionRequest()
         }
      }
   }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionDeniedDialog(
   permissionState: PermissionState,
   onConfirm: (() -> Unit)? = null
) {
   var openDialog by remember { mutableStateOf(!permissionState.status.isGranted) }

   if (openDialog) {
      AlertDialog(
         onDismissRequest = { },
         title = {
            Text(
               text = "Marlin Location Services",
               style = MaterialTheme.typography.h6
            )
         },
         text = {
            Text(text = "Marlin will use your location to determine the navigation area you currently reside to show you the most relevant navigational warnings")
         },
         buttons = {
            Row(
               horizontalArrangement = Arrangement.End,
               modifier = Modifier.fillMaxWidth()
            ) {
               TextButton(
                  onClick = {
                     openDialog = false
                     onConfirm?.invoke()
                  }
               ) {
                  Text("OK")
               }
            }
         }
      )
   }
}