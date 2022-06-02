package mil.nga.msi.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao

@Database(
   version = MsiDatabase.VERSION,
   entities = [
      Asam::class,
      Modu::class
   ]
)
@TypeConverters(DateTypeConverter::class)
abstract class MsiDatabase : RoomDatabase() {

   companion object {
      const val VERSION = 1
   }

   abstract fun asamDao(): AsamDao
   abstract fun moduDao(): ModuDao
}