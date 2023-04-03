package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer

val NavType.Companion.Layer: NavType<Layer?>
   get() = layerType

   private val layerType = object : NavType<Layer?>(true) {
      override fun put(bundle: Bundle, key: String, value: Layer?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): Layer? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): Layer? {
         return Json.decodeFromString(value)
      }
   }