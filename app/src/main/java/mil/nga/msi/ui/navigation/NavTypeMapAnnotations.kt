package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.map.cluster.MapAnnotation

val NavType.Companion.MapAnnotationsType: NavType<List<MapAnnotation>?>
   get() = mapAnnotationsType

   private val mapAnnotationsType = object : NavType<List<MapAnnotation>?>(true) {
      override fun put(bundle: Bundle, key: String, value: List<MapAnnotation>?) {
         bundle.putParcelableArray(key, value?.toTypedArray())
      }

      override fun get(bundle: Bundle, key: String): List<MapAnnotation>? {
         return BundleCompat.getParcelable(bundle, key, List::class.java)?.map { it as MapAnnotation}?.toList()
      }

      override fun parseValue(value: String): List<MapAnnotation>? {
         return Json.decodeFromString(value)
      }
   }