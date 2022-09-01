package mil.nga.msi.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.MsiDatabase
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import mil.nga.msi.datasource.light.LightDao
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import mil.nga.msi.datasource.port.PortDao
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

   @Provides
   @Singleton
   fun provideDatabase(application: Application): MsiDatabase {
      return Room.databaseBuilder(application.applicationContext, MsiDatabase::class.java, "msi")
         .fallbackToDestructiveMigration()
         .build()
   }

   @Provides
   @Singleton
   fun provideAsamDao(database: MsiDatabase): AsamDao {
      return database.asamDao()
   }

   @Provides
   @Singleton
   fun provideModuDao(database: MsiDatabase): ModuDao {
      return database.moduDao()
   }

   @Provides
   @Singleton
   fun provideNavigationalWarningDao(database: MsiDatabase): NavigationalWarningDao {
      return database.navigationalWarning()
   }

   @Provides
   @Singleton
   fun provideLightDao(database: MsiDatabase): LightDao {
      return database.lightDao()
   }

   @Provides
   @Singleton
   fun providePortDao(database: MsiDatabase): PortDao {
      return database.portDao()
   }

   @Provides
   @Singleton
   fun provideRadioBeaconDao(database: MsiDatabase): RadioBeaconDao {
      return database.radioBeaconDao()

   }

   @Provides
   @Singleton
   fun provideDgpsStationDao(database: MsiDatabase): DgpsStationDao {
      return database.dgpsStationDao()
   }
}