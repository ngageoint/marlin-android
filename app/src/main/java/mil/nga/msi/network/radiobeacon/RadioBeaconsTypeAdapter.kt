package mil.nga.msi.network.radiobeacon

import android.util.Log
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.network.nextStringOrNull
import java.lang.UnsupportedOperationException

class RadioBeaconsTypeAdapter: TypeAdapter<List<RadioBeacon>>() {
   override fun read(`in`: JsonReader): List<RadioBeacon> {
      val beacons = mutableListOf<RadioBeacon>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return beacons
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return beacons
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "ngalol" -> {
               beacons.addAll(readRadioBeacons(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return beacons
   }

   private fun readRadioBeacons(`in`: JsonReader): List<RadioBeacon> {
      val beacons = mutableListOf<RadioBeacon>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return beacons
      }

      `in`.beginArray()

      var previousRegionHeading: String? = null
      while (`in`.hasNext()) {
         readRadioBeacon(`in`)?.let {
            it.regionHeading = it.regionHeading ?: previousRegionHeading
            it.sectionHeader = "${it.geopoliticalHeading ?: ""}${it.regionHeading ?: ""})"

            if (previousRegionHeading != it.regionHeading) {
               previousRegionHeading = it.regionHeading
            }

            beacons.add(it)
         }
      }

      `in`.endArray()

      return beacons
   }

   private fun readRadioBeacon(`in`: JsonReader): RadioBeacon? {
      var volumeNumber: String? = null
      var featureNumber: String? = null
      var latitude: Double? = null
      var longitude: Double? = null
      var aidType: String? = null
      var geopoliticalHeading: String? = null
      var regionHeading: String? = null
      var name: String? = null
      var position: String? = null
      var characteristic: String? = null
      var range: String? = null
      var sequenceText: String? = null
      var frequency: String? = null
      var stationRemark: String? = null
      var postNote: String? = null
      var noticeNumber: String? = null
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
               featureNumber = `in`.nextStringOrNull()
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
            "characteristic" -> {
               characteristic = `in`.nextStringOrNull()
            }
            "range" -> {
               range = `in`.nextStringOrNull()
            }
            "sequenceText" -> {
               sequenceText = `in`.nextStringOrNull()
            }
            "frequency" -> {
               frequency = `in`.nextStringOrNull()
            }
            "stationRemark" -> {
               stationRemark = `in`.nextStringOrNull()
            }
            "postNote" -> {
               postNote = `in`.nextStringOrNull()
            }
            "noticeNumber" -> {
               noticeNumber = `in`.nextStringOrNull()
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
         RadioBeacon(volumeNumber, featureNumber, noticeYear, noticeWeek, latitude, longitude).apply {
            this.aidType = aidType
            this.geopoliticalHeading = geopoliticalHeading
            this.regionHeading = regionHeading
            this.precedingNote = precedingNote
            this.postNote = postNote
            this.name = name
            this.position = position
            this.characteristic = characteristic
            this.range = range
            this.sequenceText = sequenceText
            this.frequency = frequency
            this.stationRemark = stationRemark
            this.postNote = postNote
            this.noticeNumber = noticeNumber
            this.removeFromList = removeFromList
            this.deleteFlag = deleteFlag
            this.noticeWeek = noticeWeek
            this.noticeYear = noticeYear
         }
      } else null
   }

   override fun write(out: JsonWriter, value: List<RadioBeacon>) {
      throw UnsupportedOperationException()
   }
}