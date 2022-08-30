package mil.nga.msi.di

import android.app.Application
import androidx.datastore.core.*
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.preferences.DataSource
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
   fun provideUserPreferencesDataStore(application: Application): DataStore<UserPreferences> {
      val userPreferencesSerializer  = object : Serializer<UserPreferences> {
         override val defaultValue: UserPreferences =
            UserPreferences.newBuilder()
               .setMapLayer(BaseMapType.NORMAL.value)
               .setMgrs(false)
               .setGars(false)
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
               .putAllMapped(
                  mapOf(
                     DataSource.ASAM.name to true,
                     DataSource.MODU.name to true,
                     DataSource.NAVIGATION_WARNING.name to true,
                     DataSource.LIGHT.name to true,
                     DataSource.PORT.name to true,
                     DataSource.RADIO_BEACON.name to true
                  )
               )
               .addAllTabs(
                  listOf(DataSource.ASAM, DataSource.MODU, DataSource.NAVIGATION_WARNING, DataSource.LIGHT).map { it.name }
               )
               .addAllNonTabs(listOf(DataSource.PORT, DataSource.RADIO_BEACON).map { it.name })
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