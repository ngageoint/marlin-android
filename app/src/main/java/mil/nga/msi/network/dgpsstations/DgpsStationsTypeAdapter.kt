package mil.nga.msi.network.dgpsstations

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull

class DgpsStationsTypeAdapter: TypeAdapter<List<DgpsStation>>() {
   override fun read(`in`: JsonReader): List<DgpsStation> {
      val dgpsStations = mutableListOf<DgpsStation>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return dgpsStations
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return dgpsStations
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "ngalol" -> {
               dgpsStations.addAll(readDgpsStations(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return dgpsStations
   }

   private fun readDgpsStations(`in`: JsonReader): List<DgpsStation> {
      val dgpsStations = mutableListOf<DgpsStation>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return dgpsStations
      }

      `in`.beginArray()

      var previousRegionHeading: String? = null
      while (`in`.hasNext()) {
         readDgpsStation(`in`)?.let {
            it.regionHeading = it.regionHeading ?: previousRegionHeading
            it.sectionHeader = "${it.geopoliticalHeading ?: ""}${it.regionHeading ?: ""})"

            if (previousRegionHeading != it.regionHeading) {
               previousRegionHeading = it.regionHeading
            }

            dgpsStations.add(it)
         }
      }

      `in`.endArray()

      return dgpsStations
   }

   private fun readDgpsStation(`in`: JsonReader): DgpsStation? {
      var volumeNumber: String? = null
      var featureNumber: Int? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var aidType: String? = null
      var geopoliticalHeading: String? = null
      var regionHeading: String? = null
      var name: String? = null
      var position: String? = null
      var range: Int? = null
      var transferRate: Int? = null
      var stationId: String? = null
      var frequency: Int? = null
      var remarks: String? = null
      var postNote: String? = null
      var noticeNumber: Int? = null
      var precedingNote: String? = null
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
               featureNumber = `in`.nextIntOrNull()
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
            "aidType" -> {
               aidType = `in`.nextStringOrNull()
            }
            "geopoliticalHeading" -> {
               geopoliticalHeading = `in`.nextStringOrNull()
            }
            "regionHeading" -> {
               regionHeading = `in`.nextStringOrNull()
            }
            "name" -> {
               name = `in`.nextStringOrNull()
            }
            "range" -> {
               range = `in`.nextIntOrNull()
            }
            "stationId" -> {
               stationId = `in`.nextStringOrNull()
            }
            "transferRate" -> {
               transferRate = `in`.nextIntOrNull()
            }
            "frequency" -> {
               frequency = `in`.nextIntOrNull()
            }
            "remarks" -> {
               remarks = `in`.nextStringOrNull()
            }
            "postNote" -> {
               postNote = `in`.nextStringOrNull()
            }
            "noticeNumber" -> {
               noticeNumber = `in`.nextIntOrNull()
            }
            "precedingNote" -> {
               precedingNote = `in`.nextStringOrNull()
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

      return if (volumeNumber != null && featureNumber != null && noticeYear != null && noticeWeek != null && latitude != null && longitude != null) {
         DgpsStation(volumeNumber, featureNumber, noticeYear, noticeWeek, latitude, longitude).apply {
            this.aidType = aidType
            this.geopoliticalHeading = geopoliticalHeading
            this.regionHeading = regionHeading
            this.precedingNote = precedingNote
            this.postNote = postNote
            this.name = name
            this.position = position
            this.range = range
            this.frequency = frequency
            this.remarks = remarks
            this.transferRate = transferRate
            this.stationId = stationId
            this.postNote = postNote
            this.noticeNumber = noticeNumber
            this.removeFromList = removeFromList
            this.deleteFlag = deleteFlag
            this.noticeWeek = noticeWeek
            this.noticeYear = noticeYear
         }
      } else null
   }

   override fun write(out: JsonWriter, value: List<DgpsStation>) {
      throw UnsupportedOperationException()
   }
}