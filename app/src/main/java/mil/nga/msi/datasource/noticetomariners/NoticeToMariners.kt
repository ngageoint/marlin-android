package mil.nga.msi.datasource.noticetomariners

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "notice_to_mariners")
data class NoticeToMariners(
   @PrimaryKey
   @ColumnInfo(name = "ods_entry_id")
   val odsEntryId: Int
) {
   @ColumnInfo(name = "ods_key")
   var odsKey: String? = null

   @ColumnInfo(name = "ods_content_id")
   var odsContentId: String? = null

   @ColumnInfo(name = "publication_id")
   var publicationId: Int? = null

   @ColumnInfo(name = "notice_number")
   var noticeNumber: Int? = null

   @ColumnInfo(name = "title")
   var title: String? = null

   @ColumnInfo(name = "section_order")
   var sectionOrder: Int? = null

   @ColumnInfo(name = "limited_dist")
   var limitedDist: Boolean? = null

   @ColumnInfo(name = "internal_path")
   var internalPath: String? = null

   @ColumnInfo(name = "filename_base")
   var filenameBase: String? = null

   @ColumnInfo(name = "file_extension")
   var fileExtension: String? = null

   @ColumnInfo(name = "file_size")
   var fileSize: Int? = null

   @ColumnInfo(name = "is_full_publication")
   var isFullPublication: Boolean? = null

   @ColumnInfo(name = "upload_time")
   var uploadTime: Instant? = null

   @ColumnInfo(name = "last_modified")
   var lastModified: Instant? = null

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
              "  Filename Base: $filenameBase\n" +
              "  File Extension: $fileExtension\n" +
              "  File Size: $fileSize\n" +
              "  Is Full Publication: $isFullPublication\n" +
              "  Upload Time: $uploadTime\n" +
              "  Last Modified: $lastModified"
   }
}