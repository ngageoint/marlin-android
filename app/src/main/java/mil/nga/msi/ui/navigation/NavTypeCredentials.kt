package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.preferences.Credentials

val NavType.Companion.NavTypeCredentials: NavType<Credentials?>
   get() = credentialsType

   private val credentialsType = object : NavType<Credentials?>(true) {
      override fun put(bundle: Bundle, key: String, value: Credentials?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): Credentials? {
         return BundleCompat.getParcelable(bundle, key, Credentials::class.java)
      }

      override fun parseValue(value: String): Credentials? {
         return Json.decodeFromString(value)
      }
   }