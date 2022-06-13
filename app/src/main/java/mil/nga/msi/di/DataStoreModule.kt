package mil.nga.msi.di

import android.app.Application
import androidx.datastore.core.*
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.BaseMapType
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

   @Singleton
   @Provides
   fun providePreferencesDataStore(application: Application): DataStore<Preferences> {
      return PreferenceDataStoreFactory.create(
         produceFile = { application.preferencesDataStoreFile("user_preferences") },
         migrations = listOf(defaultMigration)
      )
   }

   @Singleton
   @Provides
   fun provideMapLocationDataStore(application: Application): DataStore<MapLocation> {
      val mapLocationSerializer  = object : Serializer<MapLocation> {
         override val defaultValue: MapLocation = MapLocation.getDefaultInstance()

         override suspend fun readFrom(input: InputStream): MapLocation {
            try {
               return MapLocation.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
               throw CorruptionException("Cannot read proto.", exception)
            }
         }

         override suspend fun writeTo(
            t: MapLocation,
            output: OutputStream
         ) = t.writeTo(output)
      }

      return DataStoreFactory.create(
         serializer = mapLocationSerializer,
         produceFile = { application.applicationContext.dataStoreFile("map_location.pb") },
         corruptionHandler = null
      )
   }
}

private val defaultMigration = object : DataMigration<Preferences> {
   override suspend fun shouldMigrate(currentData: Preferences) = true
   override suspend fun cleanUp() {}

   override suspend fun migrate(currentData: Preferences): Preferences {
      val migration = currentData.toMutablePreferences()
      migration[UserPreferencesRepository.BASE_LAYER_KEY] = BaseMapType.NORMAL.value
      return migration
   }
}