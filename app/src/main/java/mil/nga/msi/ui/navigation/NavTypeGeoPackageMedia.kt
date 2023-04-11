package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey

val NavType.Companion.GeoPackageMedia: NavType<GeoPackageMediaKey?>
   get() = geoPackageMediaKeyType

   private val geoPackageMediaKeyType = object : NavType<GeoPackageMediaKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: GeoPackageMediaKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): GeoPackageMediaKey? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): GeoPackageMediaKey? {
         return Json.decodeFromString(value)
      }
   }