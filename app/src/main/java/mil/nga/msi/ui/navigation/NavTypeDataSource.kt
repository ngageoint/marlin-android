package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.export.ExportDataSource

val NavType.Companion.NavTypeDataSource: NavType<ExportDataSource?>
   get() = exportType

   private val exportType = object : NavType<ExportDataSource?>(true) {
      override fun put(bundle: Bundle, key: String, value: ExportDataSource?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): ExportDataSource? {
         return BundleCompat.getParcelable(bundle, key, ExportDataSource::class.java)
      }

      override fun parseValue(value: String): ExportDataSource? {
         return Json.decodeFromString(value)
      }
   }