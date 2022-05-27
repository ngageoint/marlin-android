package mil.nga.msi.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mil.nga.msi.datasource.asam.AsamDao

@Database(
   version = MsiDatabase.VERSION,
   entities = []
)
@TypeConverters
abstract class MsiDatabase : RoomDatabase() {

   companion object {
      const val VERSION = 1
   }

   abstract fun asamDao(): AsamDao
}