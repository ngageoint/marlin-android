package mil.nga.msi.network.noticetomariners

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.datasource.noticetomariners.Correction
import mil.nga.msi.network.nextBooleanOrNull
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull
import java.time.Instant

data class ChartCorrectionResponse(val chartCorrections: List<ChartCorrection> = emptyList())

class ChartCorrectionsTypeAdapter: TypeAdapter<ChartCorrectionResponse>() {
   override fun read(`in`: JsonReader): ChartCorrectionResponse {
      val corrections = mutableListOf<ChartCorrection>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return ChartCorrectionResponse()
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return ChartCorrectionResponse()
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "chartCorr" -> {
               corrections.addAll(readChartCorrections(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return ChartCorrectionResponse(corrections)
   }

   private fun readChartCorrections(`in`: JsonReader): List<ChartCorrection> {
      val corrections = mutableListOf<ChartCorrection>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return corrections
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readChartCorrection(`in`)?.let {
            corrections.add(it)
         }
      }

      `in`.endArray()

      return corrections
   }

   private fun readChartCorrection(`in`: JsonReader): ChartCorrection? {
      var chartId: Int? = null
      var chartNumber: String? = null
      var internationalNumber: String? = null
      var starred: Boolean? = null
      var limitedDistribution: Boolean? = null
      var correctionType: String? = null
      var currentNoticeNumber: String? = null
      var noticeAction: String? = null
      var editionNumber: String? = null
      var editionDate: String? = null
      var lastNoticeNumber: String? = null
      var authority: String? = null
      var region: String? = null
      var subregion: String? = null
      var portCode: Int? = null
      var classification: String? = null
      var priceCategory: String? = null
      val date: Instant? = null
      var location: String? = null
      val corrections = mutableListOf<Correction>()

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "chartId" -> {
               chartId = `in`.nextIntOrNull()
            }
            "chartNumber" -> {
               chartNumber = `in`.nextStringOrNull()
            }
            "intlNumber" -> {
               internationalNumber = `in`.nextStringOrNull()
            }
            "starred" -> {
               starred = `in`.nextBooleanOrNull()
            }
            "limDist" -> {
               limitedDistribution = `in`.nextBooleanOrNull()
            }
            "correctionType" -> {
               correctionType = `in`.nextStringOrNull()
            }
            "currNoticeNum" -> {
               currentNoticeNumber = `in`.nextStringOrNull()
            }
            "noticeAction" -> {
               noticeAction = `in`.nextStringOrNull()
            }
            "editionNumber" -> {
               editionNumber = `in`.nextStringOrNull()
            }
            "editionDate" -> {
               editionDate = `in`.nextStringOrNull()
            }
            "lastNoticeNum" -> {
               lastNoticeNumber = `in`.nextStringOrNull()
            }
            "correctionText" -> {
               corrections.addAll(readCorrections(`in`))
            }
            "authority" -> {
               authority = `in`.nextStringOrNull()
            }
            "region" -> {
               region = `in`.nextStringOrNull()
            }
            "subregion" -> {
               subregion = `in`.nextStringOrNull()
            }
            "portCode" -> {
               portCode = `in`.nextIntOrNull()
            }
            "classification" -> {
               classification = `in`.nextStringOrNull()
            }
            "priceCategory" -> {
               priceCategory = `in`.nextStringOrNull()
            }
            "location" -> {
               location = `in`.nextStringOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (chartId != null && chartNumber != null) {
         ChartCorrection(
            chartId = chartId,
            chartNumber = chartNumber,
            internationalNumber = internationalNumber,
            starred = starred,
            limitedDistribution = limitedDistribution,
            correctionType = correctionType,
            currentNoticeNumber = currentNoticeNumber,
            noticeAction = noticeAction,
            editionNumber = editionNumber,
            editionDate = editionDate,
            lastNoticeNumber = lastNoticeNumber,
            corrections = corrections,
            authority = authority,
            region = region,
            subregion = subregion,
            portCode = portCode,
            classification = classification,
            priceCategory = priceCategory,
            date = date,
            location = location
         )
      } else null
   }

   private fun readCorrections(`in`: JsonReader): List<Correction> {
      val corrections = mutableListOf<Correction>()

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return corrections
      }

      `in`.beginObject()

      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "correction" -> {
               if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
                  `in`.skipValue()
                  return corrections
               }

               `in`.beginArray()

               while (`in`.hasNext()) {
                  readCorrection(`in`)?.let {
                     corrections.add(it)
                  }
               }

               `in`.endArray()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return corrections
   }

   private fun readCorrection(`in`: JsonReader): Correction? {
      var action: String? = null
      var text: String? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "action" -> {
               action = `in`.nextStringOrNull()
            }
            "text" -> {
               text = `in`.nextStringOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return Correction(
         action = action,
         text= text
      )
   }

   override fun write(out: JsonWriter, value: ChartCorrectionResponse) {
      throw UnsupportedOperationException()
   }
}