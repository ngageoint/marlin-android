package mil.nga.msi.network.navigationalwarning

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull
import java.text.SimpleDateFormat
import java.util.*

class NavigationalWarningsTypeAdapter: TypeAdapter<List<NavigationalWarning>>() {
   override fun read(`in`: JsonReader): List<NavigationalWarning> {
      val warnings = mutableListOf<NavigationalWarning>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return warnings
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return warnings
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "broadcast-warn" -> {
               warnings.addAll(readNavigationalWarnings(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return warnings
   }

   private fun readNavigationalWarnings(`in`: JsonReader): List<NavigationalWarning> {
      val warnings = mutableListOf<NavigationalWarning>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return warnings
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readNavigationalWarning(`in`)?.let { warnings.add(it) }
      }

      `in`.endArray()

      return warnings
   }

   private fun readNavigationalWarning(`in`: JsonReader): NavigationalWarning? {
      val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)

      var number: Int? = null
      var year: Int? = null
      var issueDate: Date? = null
      var navigationArea: NavigationArea? = null
      var subregions: List<String>? = null
      var text: String? = null
      var status: String? = null
      var authority: String? = null
      var cancelDate: Date? = null
      var cancelNavigationArea: String? = null
      var cancelYear: Int? = null
      var cancelNumber: Int? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "number" -> {
               number = `in`.nextIntOrNull()
            }
            "year" -> {
               year = `in`.nextIntOrNull()
            }
            "issueDate" -> {
               val dateString = `in`.nextStringOrNull()
               if (dateString != null) {
                  issueDate = try {
                     dateFormat.parse(dateString)
                  } catch (e: Exception) {
                     null
                  }
               }
            }
            "cancelDate" -> {
               val dateString = `in`.nextStringOrNull()
               if (dateString != null) {
                  cancelDate = try {
                     dateFormat.parse(dateString)
                  } catch (e: Exception) { null }
               }
            }
            "navArea" -> {
               navigationArea = `in`.nextStringOrNull()?.let {
                  NavigationArea.fromCode(it)
               }
            }
            "subregion" -> {
               subregions = `in`.nextStringOrNull()?.split(",") ?: emptyList()
            }
            "text" -> {
               text = `in`.nextStringOrNull()
            }
            "status" -> {
               status = `in`.nextStringOrNull()
            }
            "authority" -> {
               authority = `in`.nextStringOrNull()
            }
            "cancelNavArea" -> {
               cancelNavigationArea = `in`.nextStringOrNull()
            }
            "cancelMsgYear" -> {
               cancelYear = `in`.nextIntOrNull()
            }
            "cancelMsgNumber" -> {
               cancelNumber = `in`.nextIntOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (number != null && year != null && navigationArea != null && issueDate != null) {
         NavigationalWarning(number, year, navigationArea, issueDate).apply {
            this.subregions = subregions
            this.text = text
            this.status = status
            this.authority = authority
            this.cancelDate = cancelDate
            this.cancelNavigationArea = cancelNavigationArea
            this.cancelYear = cancelYear
            this.cancelNumber = cancelNumber
         }
      } else { null }
   }

   override fun write(out: JsonWriter, value: List<NavigationalWarning>) {
      throw UnsupportedOperationException()
   }
}