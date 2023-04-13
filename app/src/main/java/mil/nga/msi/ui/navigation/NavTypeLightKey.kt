package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.light.LightKey

val NavType.Companion.NavTypeLightKey: NavType<LightKey?>
   get() = lightKeyType

   private val lightKeyType = object : NavType<LightKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: LightKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): LightKey? {
         return BundleCompat.getParcelable(bundle, key, LightKey::class.java)
      }

      override fun parseValue(value: String): LightKey? {
         return Json.decodeFromString(value)
      }
   }