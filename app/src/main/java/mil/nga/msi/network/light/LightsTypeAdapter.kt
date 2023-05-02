package mil.nga.msi.network.light

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull

data class LightResponse(val lights: List<Light> = emptyList())

class LightsTypeAdapter: TypeAdapter<LightResponse>() {
   override fun read(`in`: JsonReader): LightResponse {
      val lights = mutableListOf<Light>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return LightResponse()
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return LightResponse()
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "ngalol" -> {
               lights.addAll(readLights(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return LightResponse(lights)
   }

   private fun readLights(`in`: JsonReader): List<Light> {
      val lights = mutableListOf<Light>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return lights
      }

      `in`.beginArray()

      var previousRegionHeading: String? = null
      var previousSubregionHeading: String? = null
      var previousLocalHeading: String? = null
      while (`in`.hasNext()) {
         readLight(`in`)?.let {
            it.regionHeading = it.regionHeading ?: previousRegionHeading
            it.subregionHeading = it.subregionHeading ?: previousSubregionHeading
            it.localHeading = it.localHeading ?: previousLocalHeading
            it.sectionHeader = "${it.geopoliticalHeading ?: ""}${it.regionHeading ?: ""}"

            if (previousRegionHeading != it.regionHeading) {
               previousRegionHeading = it.regionHeading
               previousSubregionHeading = null
               previousLocalHeading = null
            } else if (previousSubregionHeading != it.subregionHeading) {
               previousSubregionHeading = it.subregionHeading
               previousLocalHeading = null
            } else if (previousLocalHeading != it.localHeading) {
               previousLocalHeading = it.localHeading
            }

            lights.add(it)
         }
      }

      `in`.endArray()

      return lights
   }

   private fun readLight(`in`: JsonReader): Light? {
      var volumeNumber: String? = null
      var featureNumber: String? = null
      var characteristicNumber: Int? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var internationalFeature: String? = null
      var aidType: String? = null
      var geopoliticalHeading: String? = null
      var regionHeading: String? = null
      var subregionHeading: String? = null
      var localHeading: String? = null
      var name: String? = null
      var position: String? = null
      var characteristic: String? = null
      var heightFeet: Float? = null
      var heightMeters: Float? = null
      var range: String? = null
      var structure: String? = null
      var remarks: String? = null
      var postNote: String? = null
      var precedingNote: String? = null
      var noticeNumber: Int? = null
      var removeFromList: String? = null
      var deleteFlag: String? = null
      var noticeWeek: String? = null
      var noticeYear: String? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "volumeNumber" -> {
               volumeNumber = `in`.nextStringOrNull()
            }
            "featureNumber" -> {
               val value = `in`.nextStringOrNull()
               if (value != null) {
                  val values = value.split("\n")
                  featureNumber = values.getOrNull(0)
                  internationalFeature = values.getOrNull(1)
               }
            }
            "charNo" -> {
               characteristicNumber = `in`.nextIntOrNull()
            }
            "position" -> {
               position = `in`.nextStringOrNull()
               if (position != null) {
                  val dms = DMS.from(position)
                  val latLng = dms?.toLatLng()
                  latitude = latLng?.latitude
                  longitude = latLng?.longitude
               }
            }
            "internationalFeature" -> {
               internationalFeature = `in`.nextStringOrNull()
            }
            "aidType" -> {
               aidType = `in`.nextStringOrNull()
            }
            "geopoliticalHeading" -> {
               geopoliticalHeading = `in`.nextStringOrNull()
            }
            "regionHeading" -> {
               regionHeading = `in`.nextStringOrNull()
            }
            "subregionHeading" -> {
               subregionHeading = `in`.nextStringOrNull()
            }
            "localHeading" -> {
               localHeading = `in`.nextStringOrNull()
            }
            "name" -> {
               name = `in`.nextStringOrNull()
            }
            "characteristic" -> {
               characteristic = `in`.nextStringOrNull()
            }
            "heightFeetMeters" -> {
               `in`.nextStringOrNull()?.let { heightFeetMeters ->
                  val values = heightFeetMeters.split("\n").mapNotNull { it.toFloatOrNull() }
                  heightFeet = values.getOrNull(0)?.toFloat()
                  heightMeters = values.getOrNull(1)?.toFloat()
               }
            }
            "range" -> {
               range = `in`.nextStringOrNull()
            }
            "structure" -> {
               structure = `in`.nextStringOrNull()
            }
            "remarks" -> {
               remarks = `in`.nextStringOrNull()
            }
            "postNote" -> {
               postNote = `in`.nextStringOrNull()
            }
            "precedingNote" -> {
               precedingNote = `in`.nextStringOrNull()
            }
            "noticeNumber" -> {
               noticeNumber = `in`.nextIntOrNull()
            }
            "removeFromList" -> {
               removeFromList = `in`.nextStringOrNull()
            }
            "deleteFlag" -> {
               deleteFlag = `in`.nextStringOrNull()
            }
            "noticeWeek" -> {
               noticeWeek = `in`.nextStringOrNull()
            }
            "noticeYear" -> {
               noticeYear = `in`.nextStringOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (volumeNumber != null && featureNumber != null && characteristicNumber != null && noticeYear != null && noticeWeek != null && latitude != null && longitude != null) {
         Light(Light.compositeKey(volumeNumber, featureNumber, characteristicNumber), volumeNumber, featureNumber, characteristicNumber, noticeYear, noticeWeek, latitude, longitude).apply {
            this.internationalFeature = internationalFeature
            this.aidType = aidType
            this.geopoliticalHeading = geopoliticalHeading
            this.regionHeading = regionHeading
            this.subregionHeading = subregionHeading
            this.localHeading = localHeading
            this.precedingNote = precedingNote
            this.postNote = postNote
            this.name = name
            this.position = position
            this.characteristic = characteristic
            this.heightFeet = heightFeet
            this.heightMeters = heightMeters
            this.range = range
            this.structure = structure
            this.remarks = remarks
            this.postNote = postNote
            this.noticeNumber = noticeNumber
            this.removeFromList = removeFromList
            this.deleteFlag = deleteFlag
            this.noticeWeek = noticeWeek
            this.noticeYear = noticeYear
         }
      } else null
   }

   override fun write(out: JsonWriter, value: LightResponse) {
      throw UnsupportedOperationException()
   }
}