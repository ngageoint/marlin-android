package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.light.LightKey

val NavType.Companion.WMSCapabilities: NavType<WMSCapabilities?>
   get() = wmsCapabilities

   private val wmsCapabilities = object : NavType<WMSCapabilities?>(true) {
      override fun put(bundle: Bundle, key: String, value: WMSCapabilities?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): WMSCapabilities? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): WMSCapabilities? {
         return Json.decodeFromString(value)
      }
   }