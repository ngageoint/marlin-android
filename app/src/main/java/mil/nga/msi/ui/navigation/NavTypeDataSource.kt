package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.export.ExportDataSource

val NavType.Companion.NavTypeDataSources: NavType<List<ExportDataSource>?>
   get() = exportType

   private val exportType = object : NavType<List<ExportDataSource>?>(true) {
      override fun put(bundle: Bundle, key: String, value: List<ExportDataSource>?) {
         bundle.putParcelableArray(key, value?.toTypedArray())
      }

      override fun get(bundle: Bundle, key: String): List<ExportDataSource>? {
         return BundleCompat.getParcelableArray(bundle, key, ExportDataSource::class.java)?.map { it as ExportDataSource }?.toList()
      }

      override fun parseValue(value: String): List<ExportDataSource>? {
         return Json.decodeFromString(value)
      }
   }