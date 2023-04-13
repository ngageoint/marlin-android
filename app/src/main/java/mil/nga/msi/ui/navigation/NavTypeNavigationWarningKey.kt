package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey

val NavType.Companion.NavTypeNavigationalWarningKey: NavType<NavigationalWarningKey?>
   get() = navigationalWarningKeyType

   private val navigationalWarningKeyType = object : NavType<NavigationalWarningKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: NavigationalWarningKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): NavigationalWarningKey? {
         return BundleCompat.getParcelable(bundle, key, NavigationalWarningKey::class.java)
      }

      override fun parseValue(value: String): NavigationalWarningKey? {
         return Json.decodeFromString(value)
      }
   }