package mil.nga.msi.di

import android.app.Application
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.BaseMapType
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