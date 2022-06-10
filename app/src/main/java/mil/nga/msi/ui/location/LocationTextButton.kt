package mil.nga.msi.ui.location

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import mil.nga.msi.coordinate.DMS

@Composable
fun LocationTextButton(
   dms: DMS,
   onCopiedToClipboard: (String) -> Unit
) {
   val text = dms.format()
   val clipboardManager: ClipboardManager = LocalClipboardManager.current

   TextButton(
      onClick = {
         clipboardManager.setText(AnnotatedString.Builder(text).toAnnotatedString())
         onCopiedToClipboard(text)
      }
   ) {
      Text(text = text)
   }
}