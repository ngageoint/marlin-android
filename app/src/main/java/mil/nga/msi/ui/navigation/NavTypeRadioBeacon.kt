package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey

val NavType.Companion.RadioBeacon: NavType<RadioBeaconKey?>
   get() = radioBeaconKeyType

   private val radioBeaconKeyType = object : NavType<RadioBeaconKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: RadioBeaconKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): RadioBeaconKey? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): RadioBeaconKey? {
         return Json.decodeFromString(value)
      }
   }