package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey

val NavType.Companion.GeoPackageFeature: NavType<GeoPackageFeatureKey?>
   get() = geoPackageFeatureKeyType

   private val geoPackageFeatureKeyType = object : NavType<GeoPackageFeatureKey?>(true) {
      override fun put(bundle: Bundle, key: String, value: GeoPackageFeatureKey?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): GeoPackageFeatureKey? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): GeoPackageFeatureKey? {
         return Json.decodeFromString(value)
      }
   }