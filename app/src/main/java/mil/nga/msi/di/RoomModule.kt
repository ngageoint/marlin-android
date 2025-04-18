package mil.nga.msi.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.MsiDatabase
import mil.nga.msi.datasource.UserDatabase
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.bookmark.BookmarkDao
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationDao
import mil.nga.msi.datasource.layer.LayerDao
import mil.nga.msi.datasource.light.LightDao
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningDao
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersDao
import mil.nga.msi.datasource.port.PortDao
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao
import mil.nga.msi.datasource.route.RouteDao
import mil.nga.msi.di.migrations.room.roomMigration_1_2
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

   @Provides
   @Singleton
   fun provideMsiDatabase(application: Application): MsiDatabase {
      return Room.databaseBuilder(application.applicationContext, MsiDatabase::class.java, "msi")
         .fallbackToDestructiveMigration()
         .build()
   }



   @Provides
   @Singleton
   fun provideUserDatabase(application: Application): UserDatabase {
      return Room.databaseBuilder(application.applicationContext, UserDatabase::class.java, "user")
         .addMigrations(roomMigration_1_2)
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
      return database.navigationalWarningDao()
   }

   @Provides
   @Singleton
   fun provideLayerDao(database: MsiDatabase): LayerDao {
      return database.layerDao()
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

   @Provides
   @Singleton
   fun provideNoticeToMarinersDao(database: MsiDatabase): NoticeToMarinersDao {
      return database.noticeToMarinersDao()
   }

   @Provides
   @Singleton
   fun provideElectronicPublicationDao(database: MsiDatabase): ElectronicPublicationDao {
      return database.electronicPublicationDao()
   }

   @Provides
   @Singleton
   fun provideBookmarkDao(database: UserDatabase): BookmarkDao {
      return database.bookmarkDao()
   }

   @Provides
   @Singleton
   fun provideRouteDao(database: UserDatabase): RouteDao {
      return database.routeDao()
   }
}