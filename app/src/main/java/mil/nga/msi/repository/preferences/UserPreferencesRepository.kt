package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.map.BaseMapType
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
   private val userPreferencesDataStore: DataStore<UserPreferences>,
   private val preferencesDataStore: DataStore<Preferences>,
) {
   val baseMapType: Flow<BaseMapType> = preferencesDataStore.data.map { preferences ->
      val value = preferences[BASE_LAYER_KEY]
      BaseMapType.fromValue(value)
   }

   suspend fun setBaseMapType(baseMapType: BaseMapType) {
      preferencesDataStore.edit { preferences ->
         preferences[BASE_LAYER_KEY] = baseMapType.value
      }
   }

   val mgrs: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
      preferences[MGRS_KEY] == true
   }

   suspend fun setMGRS(enabled: Boolean) {
      preferencesDataStore.edit { preferences ->
         preferences[MGRS_KEY] = enabled
      }
   }

   val mapLocation = userPreferencesDataStore.data.map { it.mapLocation }

   suspend fun setMapLocation(mapLocation: MapLocation) {
      userPreferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.mapLocation.toBuilder()
            .setLatitude(mapLocation.latitude)
            .setLongitude(mapLocation.longitude)
            .setZoom(mapLocation.zoom)
            .build()

         builder.build()
      }
   }

   val lastReadNavigationalWarnings = userPreferencesDataStore.data.map { it.lastReadNavigationWarningsMap }

   suspend fun setLastReadNavigationalWarning(navigationArea: NavigationArea, key: NavigationalWarningKey) {
      userPreferencesDataStore.updateData {
         val preferenceKey = mil.nga.msi.type.NavigationalWarningKey.newBuilder()
            .setNumber(key.number.toLong())
            .setYear(key.year)
            .build()

         val builder = it.toBuilder()
         builder.putLastReadNavigationWarnings(navigationArea.code, preferenceKey)
         builder.build()
      }
   }

   companion object {
      val BASE_LAYER_KEY = intPreferencesKey("mil.nga.msi.preference.baseLayer")
      val MGRS_KEY = booleanPreferencesKey("mil.nga.msi.preference.mgrs")
   }
}