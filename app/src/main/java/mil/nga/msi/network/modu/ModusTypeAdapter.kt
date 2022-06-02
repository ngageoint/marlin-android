package mil.nga.msi.network.modu

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.RigStatus
import mil.nga.msi.datasource.modu.RigStatusConverter
import mil.nga.msi.network.nextDoubleOrNull
import mil.nga.msi.network.nextStringOrNull
import java.lang.UnsupportedOperationException
import java.text.SimpleDateFormat
import java.util.*

class ModusTypeAdapter: TypeAdapter<List<Modu>>() {
   override fun read(`in`: JsonReader): List<Modu> {
      val modus = mutableListOf<Modu>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return modus
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return modus
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "modu" -> {
               modus.addAll(readModus(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return modus
   }

   private fun readModus(`in`: JsonReader): List<Modu> {
      val modus = mutableListOf<Modu>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return modus
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readModu(`in`)?.let { modus.add(it) }
      }

      `in`.endArray()

      return modus
   }

   private fun readModu(`in`: JsonReader): Modu? {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

      var name: String? = null
      var date: Date? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var rigStatus: RigStatus? = null
      var specialStatus: String? = null
      var distance: Double? = null
      var position: String? = null
      var navigationArea: String? = null
      var region: String? = null
      var subregion: String? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "name" -> {
               name = `in`.nextString()
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
            "rigStatus" -> {
               rigStatus = `in`.nextStringOrNull()?.let {
                  RigStatusConverter().toRigStatus(it)
               }
            }
            "specialStatus" -> {
               specialStatus = `in`.nextStringOrNull()
            }
            "distance" -> {
               distance = `in`.nextDoubleOrNull()
            }
            "position" -> {
               position = `in`.nextStringOrNull()
            }
            "navArea" -> {
               navigationArea = `in`.nextStringOrNull()
            }
            "region" -> {
               region = `in`.nextStringOrNull()
            }
            "subregion" -> {
               subregion = `in`.nextStringOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (name != null && date != null && latitude != null && longitude != null) {
         Modu(name, date, latitude, longitude).apply {
            this.position = position
            this.navigationArea = navigationArea
            this.subregion = subregion
            this.rigStatus = rigStatus
            this.specialStatus = specialStatus
            this.distance = distance
            this.position = position
            this.navigationArea = navigationArea
            this.region = region
            this.subregion = subregion
         }
      } else { null }
   }

   override fun write(out: JsonWriter, value: List<Modu>) {
      throw UnsupportedOperationException()
   }
}