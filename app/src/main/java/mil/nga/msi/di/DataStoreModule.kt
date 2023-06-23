package mil.nga.msi.di

import android.app.Application
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.AsamFilter
import mil.nga.msi.datasource.filter.LightFilter
import mil.nga.msi.datasource.filter.ModuFilter
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.di.migrations.dataStoreMigration_1_2
import mil.nga.msi.di.migrations.dataStoreMigration_2_3
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.sort.SortDirection
import mil.nga.msi.type.*
import mil.nga.msi.ui.map.BaseMapType
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {
   @Singleton
   @Provides
   fun provideUserPreferencesDataStore(application: Application): DataStore<UserPreferences> {
      val userPreferencesSerializer = object : Serializer<UserPreferences> {
         override val defaultValue: UserPreferences =
            UserPreferences.newBuilder()
               .setVersion(VERSION)
               .setMap(
                  MapPreferences.newBuilder()
                     .setMapLayer(BaseMapType.NORMAL.value)
                     .setMapLocation(MapLocation.getDefaultInstance())
                     .setMgrs(false)
                     .setGars(false)
               )
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
                     DataSource.RADIO_BEACON.name to true,
                     DataSource.DGPS_STATION.name to true
                  )
               )
               .addAllTabs(
                  listOf(DataSource.ASAM, DataSource.MODU, DataSource.NAVIGATION_WARNING).map { it.name }
               )
               .addAllNonTabs(
                  listOf(
                     DataSource.PORT,
                     DataSource.RADIO_BEACON,
                     DataSource.DGPS_STATION,
                     DataSource.ELECTRONIC_PUBLICATION,
                     DataSource.NOTICE_TO_MARINERS,
                     DataSource.BOOKMARK
                  ).map { it.name })
               .putAllSort(sortDefaults)
               .putAllFilters(filterDefaults)
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
         migrations = listOf(
            dataStoreMigration_1_2,
            dataStoreMigration_2_3
         ),
         corruptionHandler = null
      )
   }

   companion object {
      const val VERSION = 3

      val filterDefaults = mapOf(
         DataSource.ASAM.name to Filters.newBuilder()
            .addFilters(
               Filter.newBuilder()
                  .setParameter(
                     listOfNotNull(
                        AsamFilter.parameters.find { it.parameter == "date" }
                     ).map {
                        FilterParameter.newBuilder()
                           .setName(it.parameter)
                           .setTitle(it.title)
                           .setType(it.type.name)
                           .build()
                     }.first()
                  )
               .setValue("last 365 days")
               .setComparator(ComparatorType.WITHIN.name)
            )
         .build()
      )

      val sortDefaults = mapOf(
         DataSource.ASAM.name to Sort.newBuilder()
            .setSection(false)
            .addAllList(
               listOfNotNull(
                  AsamFilter.parameters.find { it.parameter == "date" }
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.DESC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build(),

         DataSource.MODU.name to Sort.newBuilder()
            .setSection(false)
            .addAllList(
               listOfNotNull(
                  ModuFilter.parameters.find { it.parameter == "date" }
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.DESC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build(),

         DataSource.LIGHT.name to Sort.newBuilder()
            .setSection(true)
            .addAllList(
               listOfNotNull(
                  LightFilter.parameters.find { it.parameter == "section_header" },
                  LightFilter.parameters.find { it.parameter == "feature_number" },
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.ASC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build(),

         DataSource.PORT.name to Sort.newBuilder()
            .setSection(true)
            .addAllList(
               listOfNotNull(
                  LightFilter.parameters.find { it.parameter == "port_number" }
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.DESC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build(),

         DataSource.RADIO_BEACON.name to Sort.newBuilder()
            .setSection(true)
            .addAllList(
               listOfNotNull(
                  LightFilter.parameters.find { it.parameter == "geopolitical_heading" },
                  LightFilter.parameters.find { it.parameter == "feature_number" }
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.ASC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build(),

         DataSource.DGPS_STATION.name to Sort.newBuilder()
            .setSection(true)
            .addAllList(
               listOfNotNull(
                  LightFilter.parameters.find { it.parameter == "geopolitical_heading" },
                  LightFilter.parameters.find { it.parameter == "feature_number" }
               ).map {
                  SortParameter.newBuilder()
                     .setDirection(SortDirection.ASC.name)
                     .setName(it.parameter)
                     .setTitle(it.title)
                     .setType(it.type.name)
                     .build()
               }
            ).build()
      )
   }
}

