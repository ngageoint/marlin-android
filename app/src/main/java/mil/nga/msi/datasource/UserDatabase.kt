package mil.nga.msi.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.bookmark.BookmarkDao

@Database(
   version = UserDatabase.VERSION,
   entities = [
      Bookmark::class
   ]
)
@TypeConverters(
   DateTypeConverter::class
)
abstract class UserDatabase : RoomDatabase() {

   companion object {
      const val VERSION = 1
   }

   abstract fun bookmarkDao(): BookmarkDao
}