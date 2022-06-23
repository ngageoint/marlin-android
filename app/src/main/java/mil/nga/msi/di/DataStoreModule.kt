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
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.NavigationalWarningKey
import mil.nga.msi.type.UserPreferences
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
   fun provideUserPreferencesDataStore(application: Application): DataStore<UserPreferences> {
      val userPreferencesSerializer  = object : Serializer<UserPreferences> {
         override val defaultValue: UserPreferences =
            UserPreferences.newBuilder()
               .setMapLocation(MapLocation.getDefaultInstance())
               .putAllLastReadNavigationWarnings(
                  mapOf<String, NavigationalWarningKey>(
                     NavigationArea.HYDROARC.code to NavigationalWarningKey.newBuilder().build(),
                     NavigationArea.HYDROLANT.code to NavigationalWarningKey.newBuilder().build(),
                     NavigationArea.HYDROPAC.code to NavigationalWarningKey.newBuilder().build(),
                     NavigationArea.NAVAREA_IV.code to NavigationalWarningKey.newBuilder().build(),
                     NavigationArea.NAVAREA_XII.code to NavigationalWarningKey.newBuilder().build(),
                     NavigationArea.SPECIAL_WARNING.code to NavigationalWarningKey.newBuilder().build(),
                  )
               )
               .build()

         override suspend fun readFrom(input: InputStream): UserPreferences {
            try {
               return UserPreferences.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
               throw CorruptionException("Cannot read proto.", exception)
            }
         }

         override suspend fun writeTo(
            t: UserPreferences,
            output: OutputStream
         ) = t.writeTo(output)
      }

      return DataStoreFactory.create(
         serializer = userPreferencesSerializer,
         produceFile = { application.applicationContext.dataStoreFile("user_preferences.pb") },
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