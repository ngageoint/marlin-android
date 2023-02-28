package mil.nga.msi.ui.location

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import mil.nga.msi.coordinate.DMS

@Composable
fun LocationText(
   dms: DMS,
   onCopiedToClipboard: (String) -> Unit
) {
   val text = dms.format()
   val clipboardManager: ClipboardManager = LocalClipboardManager.current

   Text(
      text = dms.format(),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.clickable {
         clipboardManager.setText(AnnotatedString.Builder(text).toAnnotatedString())
         onCopiedToClipboard(text)
      }
   )
}

@Composable
fun LocationTextButton(
   dms: DMS,
   onCopiedToClipboard: (String) -> Unit
) {
   val text = dms.format()
   val clipboardManager: ClipboardManager = LocalClipboardManager.current

   TextButton(
      colors = ButtonDefaults.textButtonColors(
         contentColor = MaterialTheme.colorScheme.primary
      ),
      onClick = {
         clipboardManager.setText(AnnotatedString(text))
         onCopiedToClipboard(text)
      }
   ) {
      Text(text = text)
   }
}