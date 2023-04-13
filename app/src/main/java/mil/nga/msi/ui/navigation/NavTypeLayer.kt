package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer

val NavType.Companion.NavTypeLayer: NavType<Layer?>
   get() = layerType

   private val layerType = object : NavType<Layer?>(true) {
      override fun put(bundle: Bundle, key: String, value: Layer?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): Layer? {
         return BundleCompat.getParcelable(bundle, key, Layer::class.java)
      }

      override fun parseValue(value: String): Layer? {
         return Json.decodeFromString(value)
      }
   }