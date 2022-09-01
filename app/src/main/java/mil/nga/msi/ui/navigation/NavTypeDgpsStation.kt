package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.dgpsstation.DgpsStationKey

val NavType.Companion.DgpsStation: NavType<DgpsStationKey?>
   get() = dgpsStationKeyType

   private val dgpsStationKeyType = object : NavType<DgpsStationKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: DgpsStationKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): DgpsStationKey? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): DgpsStationKey? {
         return Json.decodeFromString(value)
      }
   }