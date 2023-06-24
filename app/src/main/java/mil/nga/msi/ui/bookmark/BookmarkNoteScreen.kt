package mil.nga.msi.ui.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.repository.bookmark.BookmarkKey

@Composable
fun BookmarkNotesScreen(
   bookmark: BookmarkKey?,
   onDone: () -> Unit,
   viewModel: BookmarkViewModel = hiltViewModel()
) {
   var notes by remember { mutableStateOf(bookmark?.notes) }

   Column(Modifier.fillMaxWidth()) {
      Note(notes) { notes = it }

      Box(
         contentAlignment = Alignment.CenterEnd,
         modifier = Modifier.fillMaxWidth()
      ) {
         TextButton(
            onClick = {
               bookmark?.let { viewModel.saveBookmark(it, notes) }
               onDone()
            },
            Modifier.padding(bottom = 16.dp, end = 16.dp)
         ) {
            Text("Done")
         }
      }
   }
}

@Composable
fun Note(
   note: String?,
   onNoteChanged: (String) -> Unit,
) {
   val focusManager = LocalFocusManager.current

   Column(
      Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
   ) {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier.padding(vertical = 8.dp)
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               Icons.Default.Bookmark,
               modifier = Modifier.padding(end = 16.dp),
               contentDescription = "Bookmark"
            )
            Text(
               text = "Bookmark Notes",
               style = MaterialTheme.typography.titleMedium
            )
         }
      }

      TextField(
         value = note.orEmpty(),
         minLines = 3,
         onValueChange = { onNoteChanged(it) },
         keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
      )
   }
}