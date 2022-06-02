package mil.nga.msi.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.MsiDatabase
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.modu.ModuDao
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
}