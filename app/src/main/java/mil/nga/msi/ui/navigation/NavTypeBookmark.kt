package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.bookmark.BookmarkKey

val NavType.Companion.NavTypeBookmark: NavType<BookmarkKey?>
   get() = bookmarkTypeKey

   private val bookmarkTypeKey = object : NavType<BookmarkKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: BookmarkKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): BookmarkKey? {
         return BundleCompat.getParcelable(bundle, key, BookmarkKey::class.java)
      }

      override fun parseValue(value: String): BookmarkKey? {
         return Json.decodeFromString(value)
      }
   }