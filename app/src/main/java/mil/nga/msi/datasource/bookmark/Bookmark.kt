package mil.nga.msi.datasource.bookmark

import androidx.room.ColumnInfo
import java.util.Date

abstract class Bookmark(
   @ColumnInfo(name = "bookmarked")
   open var bookmarked: Boolean = false,

   @ColumnInfo(name = "bookmark_timestamp")
   open var bookmarkDate: Date? = null,

   @ColumnInfo(name = "bookmark_notes")
   open var bookmarkNotes: String? = null
) {
   abstract val bookmarkId: String
}