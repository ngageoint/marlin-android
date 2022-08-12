package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.light.LightKey

val NavType.Companion.LightKey: NavType<LightKey?>
   get() = lightKeyType

   private val lightKeyType = object : NavType<LightKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: LightKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): LightKey? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): LightKey? {
         return Json.decodeFromString(value)
      }
   }