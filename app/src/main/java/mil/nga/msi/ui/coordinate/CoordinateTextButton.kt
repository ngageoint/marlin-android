package mil.nga.msi.ui.coordinate

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.CoordinateSystem

@Composable
fun CoordinateTextButton(
   latLng: LatLng,
   onCopiedToClipboard: (String) -> Unit,
   viewModel: CoordinateViewModel = hiltViewModel()
) {
   val coordinateSystem by viewModel.coordinateSystem.observeAsState(CoordinateSystem.DMS)
   val clipboardManager: ClipboardManager = LocalClipboardManager.current
   val text = coordinateSystem.format(latLng)

   TextButton(
      colors = ButtonDefaults.textButtonColors(
         contentColor = MaterialTheme.colorScheme.tertiary
      ),
      onClick = {
         clipboardManager.setText(AnnotatedString(text))
         onCopiedToClipboard(text)
      }
   ) {
      Text(text = text)
   }
}