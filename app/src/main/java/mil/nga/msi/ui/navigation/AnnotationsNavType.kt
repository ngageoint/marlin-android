package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.map.MapAnnotation

class AnnotationsNavType : NavType<List<MapAnnotation>>(
   isNullableAllowed = false
) {
   override fun put(bundle: Bundle, key: String, value: List<MapAnnotation>) {
      bundle.putParcelableArray(key, value.toTypedArray())
   }

   override fun get(bundle: Bundle, key: String): List<MapAnnotation> {
      val mapAnnotations = bundle.getParcelableArray(key)?.toList() as? List<MapAnnotation>
      return mapAnnotations!!
   }

   override fun parseValue(value: String): List<MapAnnotation> {
      return Json.decodeFromString(value)
   }
}