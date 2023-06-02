package mil.nga.msi.network.asam

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.nextDoubleOrNull
import mil.nga.msi.network.nextStringOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AsamResponse(val asams: List<Asam> = emptyList())

class AsamsTypeAdapter: TypeAdapter<AsamResponse>() {

   override fun read(`in`: JsonReader): AsamResponse {

      val asams = mutableListOf<Asam>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return AsamResponse()
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return AsamResponse()
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "asam" -> {
               asams.addAll(readAsams(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return AsamResponse(asams)
   }

   private fun readAsams(`in`: JsonReader): List<Asam> {
      val asams = mutableListOf<Asam>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return asams
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readAsam(`in`)?.let { asams.add(it) }
      }

      `in`.endArray()

      return asams
   }

   private fun readAsam(`in`: JsonReader): Asam? {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

      var reference: String? = null
      var date: Date? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var position: String? = null
      var navigationArea: String? = null
      var subregion: String? = null
      var description: String? = null
      var hostility: String? = null
      var victim: String? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "reference" -> {
               reference = `in`.nextString()
            }
            "date" -> {
               val dateString = `in`.nextStringOrNull()
               if (dateString != null) {
                  date = try {
                     dateFormat.parse(dateString)
                  } catch (e: Exception) { null }
               }
            }
            "latitude" -> {
               latitude = `in`.nextDoubleOrNull()
            }
            "longitude" -> {
               longitude = `in`.nextDoubleOrNull()
            }
            "position" -> {
               position = `in`.nextStringOrNull()
            }
            "navArea" -> {
               navigationArea = `in`.nextStringOrNull()
            }
            "subreg" -> {
               subregion = `in`.nextStringOrNull()
            }
            "hostility" -> {
               hostility = `in`.nextStringOrNull()
            }
            "victim" -> {
               victim = `in`.nextStringOrNull()
            }
            "description" -> {
               description = `in`.nextStringOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (reference != null && date != null && latitude != null && longitude != null) {
        Asam(reference, date, latitude, longitude).apply {
           this.position = position
           this.navigationArea = navigationArea
           this.subregion = subregion
           this.description = description
           this.hostility = hostility
           this.victim = victim
        }
      } else null
   }

   override fun write(out: JsonWriter, value: AsamResponse) {
      throw UnsupportedOperationException()
   }
}