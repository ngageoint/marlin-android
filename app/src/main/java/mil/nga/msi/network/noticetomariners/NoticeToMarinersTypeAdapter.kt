package mil.nga.msi.network.noticetomariners

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.network.nextBooleanOrNull
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextStringOrNull
import mil.nga.msi.parseAsInstant
import java.time.Instant

class NoticeToMarinersTypeAdapter: TypeAdapter<List<NoticeToMariners>>() {
   override fun read(`in`: JsonReader): List<NoticeToMariners> {
      val noticeToMariners = mutableListOf<NoticeToMariners>()
      if (`in`.peek() == JsonToken.NULL) {
         `in`.nextNull()
         return noticeToMariners
      }

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return noticeToMariners
      }

      `in`.beginObject()
      while (`in`.hasNext()) {
         when(`in`.nextName()) {
            "pubs" -> {
               noticeToMariners.addAll(readNoticeToMariners(`in`))
            }
            else -> `in`.skipValue()
         }
      }
      `in`.endObject()

      return noticeToMariners
   }

   private fun readNoticeToMariners(`in`: JsonReader): List<NoticeToMariners> {
      val noticeToMariners = mutableListOf<NoticeToMariners>()

      if (`in`.peek() != JsonToken.BEGIN_ARRAY) {
         `in`.skipValue()
         return noticeToMariners
      }

      `in`.beginArray()

      while (`in`.hasNext()) {
         readNoticeToMariner(`in`)?.let {
            noticeToMariners.add(it)
         }
      }

      `in`.endArray()

      return noticeToMariners
   }

   private fun readNoticeToMariner(`in`: JsonReader): NoticeToMariners? {
      var odsEntryId: Int? = null
      var odsKey: String? = null
      var odsContentId: String? = null
      var publicationId: Int? = null
      var noticeNumber: Int? = null
      var title: String? = null
      var sectionOrder: Int? = null
      var limitedDist: Boolean? = null
      var internalPath: String? = null
      var filenameBase: String? = null
      var fileExtension: String? = null
      var fileSize: Int? = null
      var isFullPublication: Boolean? = null
      var uploadTime: Instant? = null
      var lastModified: Instant? = null

      if (`in`.peek() != JsonToken.BEGIN_OBJECT) {
         `in`.skipValue()
         return null
      }

      `in`.beginObject()

      while(`in`.hasNext()) {
         when(`in`.nextName()) {
            "odsEntryId" -> {
               odsEntryId = `in`.nextIntOrNull()
            }
            "odsKey" -> {
               odsKey = `in`.nextStringOrNull()
            }
            "odsContentId" -> {
               odsContentId = `in`.nextStringOrNull()
            }
            "publicationIdentifier" -> {
               publicationId = `in`.nextIntOrNull()
            }
            "noticeNumber" -> {
               noticeNumber = `in`.nextIntOrNull()
            }
            "title" -> {
               title = `in`.nextStringOrNull()
            }
            "sectionOrder" -> {
               sectionOrder = `in`.nextIntOrNull()
            }
            "limitedDist" -> {
               limitedDist = `in`.nextBooleanOrNull()
            }
            "internalPath" -> {
               internalPath = `in`.nextStringOrNull()
            }
            "filenameBase" -> {
               filenameBase = `in`.nextStringOrNull()
            }
            "fileExtension" -> {
               fileExtension = `in`.nextStringOrNull()
            }
            "fileSize" -> {
               fileSize = `in`.nextIntOrNull()
            }
            "isFullPublication" -> {
               isFullPublication = `in`.nextBooleanOrNull()
            }
            "uploadTime" -> {
               uploadTime = `in`.nextStringOrNull()?.parseAsInstant()
            }
            "lastModified" -> {
               lastModified = `in`.nextStringOrNull()?.parseAsInstant()
            }
            else -> `in`.skipValue()
         }
      }

      `in`.endObject()

      return if (odsEntryId != null && noticeNumber != null) {
         NoticeToMariners(odsEntryId, noticeNumber).apply {
            this.odsKey = odsKey
            this.odsContentId = odsContentId
            this.publicationId = publicationId
            this.title = title
            this.sectionOrder = sectionOrder
            this.limitedDist = limitedDist
            this.internalPath = internalPath
            this.filenameBase = filenameBase
            this.fileExtension = fileExtension
            this.fileSize = fileSize
            this.isFullPublication = isFullPublication
            this.uploadTime = uploadTime
            this.lastModified = lastModified
         }
      } else { null }
   }

   override fun write(out: JsonWriter, value: List<NoticeToMariners>) {
      throw UnsupportedOperationException()
   }
}