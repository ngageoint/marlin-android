package mil.nga.msi.datasource.noticetomariners

import android.content.Context
import android.os.Environment
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Locale

@Entity(tableName = "notice_to_mariners")
data class NoticeToMariners(
   @PrimaryKey
   @ColumnInfo(name = "ods_entry_id")
   val odsEntryId: Int,

   @ColumnInfo(name = "ods_key")
   val odsKey: String,

   @ColumnInfo(name = "notice_number")
   val noticeNumber: Int,

   @ColumnInfo(name = "filename")
   val filename: String,
) {
   @ColumnInfo(name = "ods_content_id")
   var odsContentId: String? = null

   @ColumnInfo(name = "publication_id")
   var publicationId: Int? = null

   @ColumnInfo(name = "title")
   var title: String? = null

   @ColumnInfo(name = "section_order")
   var sectionOrder: Int? = null

   @ColumnInfo(name = "limited_dist")
   var limitedDist: Boolean? = null

   @ColumnInfo(name = "internal_path")
   var internalPath: String? = null

   @ColumnInfo(name = "file_size")
   var fileSize: Int? = null

   @ColumnInfo(name = "is_full_publication")
   var isFullPublication: Boolean? = null

   @ColumnInfo(name = "upload_time")
   var uploadTime: Instant? = null

   @ColumnInfo(name = "last_modified")
   var lastModified: Instant? = null

   fun span(): Pair<String, String> {
      return span(noticeNumber)
   }

   override fun toString(): String {
      return "Notice To Mariners\n\n" +
              "  ods Entry Id: $odsEntryId\n" +
              "  ods Key: $odsKey\n" +
              "  ods ContentId: $odsContentId\n" +
              "  Publication Id: $publicationId\n" +
              "  Notice Number: $noticeNumber\n" +
              "  Title: $title\n" +
              "  Section Order: $sectionOrder\n" +
              "  Limited Distribution: $limitedDist\n" +
              "  Internal Path: $internalPath\n" +
              "  Filename: $filename\n" +
              "  File Size: $fileSize\n" +
              "  Is Full Publication: $isFullPublication\n" +
              "  Upload Time: $uploadTime\n" +
              "  Last Modified: $lastModified"
   }

   companion object {
      fun cachePath(context: Context, filename: String): Path {
         return Paths.get(context.cacheDir.absolutePath, "notice_to_mariners", "publications", filename)
      }

      fun externalFilesPath(context: Context, filename: String): Path {
         return Paths.get(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath, "notice_to_mariners", "publications", filename)
      }

      fun span(noticeNumber: Int): Pair<String, String> {
         val calendar = Calendar.getInstance()
         calendar.set(Calendar.YEAR, noticeNumber.toString().take(4).toInt())
         calendar.set(Calendar.WEEK_OF_YEAR, noticeNumber.toString().takeLast(2).toInt())
         while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
         }

         val start = SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)
         calendar.add(Calendar.DAY_OF_WEEK, 6)
         val end = SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)

         return start to end
      }
   }
}