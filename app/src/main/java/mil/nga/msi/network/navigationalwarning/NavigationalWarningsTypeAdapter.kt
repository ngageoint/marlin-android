package mil.nga.msi.network.navigationalwarning

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.location.NavTextParser
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull
import java.text.SimpleDateFormat
import java.util.*

data class NavigationalWarningResponse(val warnings: List<NavigationalWarning> = emptyList())

class NavigationalWarningsTypeAdapter: TypeAdapter<NavigationalWarningResponse>() {
   override fun read(`in`: JsonReader): NavigationalWarningResponse {
      val warnings = mutableListOf<NavigationalWarning>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return NavigationalWarningResponse()
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return NavigationalWarningResponse()
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

      return NavigationalWarningResponse(warnings)
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
      var subregions: List<String> = emptyList()
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
         NavigationalWarning(NavigationalWarning.compositeKey(number, year, navigationArea), number, year, navigationArea, issueDate).apply {
            this.subregions = subregions
            this.text = text
            this.status = status
            this.authority = authority
            this.cancelDate = cancelDate
            this.cancelNavigationArea = cancelNavigationArea
            this.cancelYear = cancelYear
            this.cancelNumber = cancelNumber
            this.position = text?.let { NavTextParser().parseToMappedLocation(it) }?.locations()
         }
      } else { null }
   }

   override fun write(out: JsonWriter, value: NavigationalWarningResponse) {
      throw UnsupportedOperationException()
   }
}