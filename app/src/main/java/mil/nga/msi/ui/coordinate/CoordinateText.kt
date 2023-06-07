package mil.nga.msi.ui.coordinate

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.CoordinateSystem

@Composable
fun CoordinateText(
   latLng: LatLng,
   onCopiedToClipboard: (String) -> Unit,
   viewModel: CoordinateViewModel = hiltViewModel()
) {
   val coordinateSystem by viewModel.coordinateSystem.observeAsState(CoordinateSystem.DMS)
   val clipboardManager: ClipboardManager = LocalClipboardManager.current
   val text = coordinateSystem.format(latLng)

   Text(
      text = text,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.clickable {
         clipboardManager.setText(AnnotatedString.Builder(text).toAnnotatedString())
         onCopiedToClipboard(text)
      }
   )
}