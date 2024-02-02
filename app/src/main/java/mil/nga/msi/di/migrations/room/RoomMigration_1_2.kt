package mil.nga.msi.di.migrations.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val roomMigration_1_2 = object : Migration(1, 2) {
   override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL("CREATE TABLE IF NOT EXISTS `routes` " +
              "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
              "`createdTime` INTEGER NOT NULL, " +
              "`name` TEXT NOT NULL, " +
              "`updatedTime` INTEGER NOT NULL, " +
              "`distanceMeters` REAL, " +
              "`geoJson` TEXT, " +
              "`maxLatitude` REAL, " +
              "`maxLongitude` REAL, " +
              "`minLatitude` REAL, " +
              "`minLongitude` REAL)")
      db.execSQL("CREATE TABLE IF NOT EXISTS `route_waypoints` " +
              "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
              "`route_id` INTEGER NOT NULL, " +
              "`data_source` TEXT NOT NULL, " +
              "`item_key` TEXT NOT NULL, " +
              "`json` TEXT, " +
              "`order` INTEGER)")
   }
}