package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.bookmark.BookmarkRoute
import java.lang.UnsupportedOperationException

sealed class Action {
   class Bookmark(val key: BookmarkKey): Action() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
      }
   }

   open fun navigate(navController: NavController) {
      throw UnsupportedOperationException()
   }
}