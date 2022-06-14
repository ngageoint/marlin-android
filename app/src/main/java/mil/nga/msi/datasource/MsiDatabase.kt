package mil.nga.msi.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao

@Database(
   version = MsiDatabase.VERSION,
   entities = [
      Asam::class,
      Modu::class,
      NavigationalWarning::class
   ]
)
@TypeConverters(
   DateTypeConverter::class,
   StringListTypeConverter::class
)
abstract class MsiDatabase : RoomDatabase() {

   companion object {
      const val VERSION = 1
   }

   abstract fun asamDao(): AsamDao
   abstract fun moduDao(): ModuDao
   abstract fun navigationalWarning(): NavigationalWarningDao
}