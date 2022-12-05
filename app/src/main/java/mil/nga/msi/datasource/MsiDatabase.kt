package mil.nga.msi.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationDao
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightDao
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortDao
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao

@Database(
   version = MsiDatabase.VERSION,
   entities = [
      Asam::class,
      DgpsStation::class,
      ElectronicPublication::class,
      Light::class,
      Modu::class,
      NavigationalWarning::class,
      Port::class,
      RadioBeacon::class,
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
   abstract fun dgpsStationDao(): DgpsStationDao
   abstract fun electronicPublicationDao(): ElectronicPublicationDao
   abstract fun lightDao(): LightDao
   abstract fun moduDao(): ModuDao
   abstract fun navigationalWarning(): NavigationalWarningDao
   abstract fun portDao(): PortDao
   abstract fun radioBeaconDao(): RadioBeaconDao
}