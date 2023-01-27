package mil.nga.msi.network.noticetomariners

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull

class NoticeToMarinersGraphicsTypeAdapter: TypeAdapter<List<NoticeToMarinersGraphics>>() {
   override fun read(`in`: JsonReader): List<NoticeToMarinersGraphics> {
      val graphics = mutableListOf<NoticeToMarinersGraphics>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return graphics
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return graphics
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "ntmGraphics" -> {
               graphics.addAll(readNoticeToMariners(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return graphics
   }


   fun readNoticeToMariners(`in`: JsonReader): List<NoticeToMarinersGraphics> {
      val graphics = mutableListOf<NoticeToMarinersGraphics>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return graphics
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readNoticeToMarinerGraphic(`in`)?.let {
            graphics.add(it)
         }
      }

      `in`.endArray()

      return graphics
   }

   private fun readNoticeToMarinerGraphic(`in`: JsonReader): NoticeToMarinersGraphics? {
      var noticeNumber: Int? = null
      var noticeYear: Int? = null
      var noticeWeek: Int? = null
      var chartNumber: String? = null
      var priceCategory: String? = null
      var subregion: Int? = null
      var graphicType: String? = null
      var seqNum: Int? = null
      var fileName: String? = null
      var fileSize: Int? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "noticeNumber" -> {
               noticeNumber = `in`.nextIntOrNull()
            }
            "noticeYear" -> {
               noticeYear = `in`.nextIntOrNull()
            }
            "noticeWeek" -> {
               noticeWeek = `in`.nextIntOrNull()
            }
            "chartNumber" -> {
               chartNumber = `in`.nextStringOrNull()
            }
            "priceCategory" -> {
               priceCategory = `in`.nextStringOrNull()
            }
            "subregion" -> {
               subregion = `in`.nextIntOrNull()
            }
            "graphicType" -> {
               graphicType = `in`.nextStringOrNull()
            }
            "seqNum" -> {
               seqNum = `in`.nextIntOrNull()
            }
            "fileName" -> {
               fileName = `in`.nextStringOrNull()
            }
            "fileSize" -> {
               fileSize = `in`.nextStringOrNull()?.toIntOrNull()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (noticeNumber != null) {
         NoticeToMarinersGraphics(noticeNumber).apply {
            this.noticeYear = noticeYear
            this.noticeWeek = noticeWeek
            this.chartNumber = chartNumber
            this.priceCategory = priceCategory
            this.subregion = subregion
            this.graphicType = graphicType
            this.seqNum = seqNum
            this.fileName = fileName
            this.fileSize = fileSize
         }
      } else { null }
   }

   override fun write(out: JsonWriter, value: List<NoticeToMarinersGraphics>) {
      throw UnsupportedOperationException()
   }
}