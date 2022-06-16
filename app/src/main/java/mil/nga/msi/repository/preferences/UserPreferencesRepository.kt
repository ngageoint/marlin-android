package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.BaseMapType
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
   private val preferencesDataStore: DataStore<Preferences>,
   private val mapLocationDataStore: DataStore<MapLocation>
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

   val mapLocation = mapLocationDataStore.data

   suspend fun setMapLocation(mapLocation: MapLocation) {
      mapLocationDataStore.updateData {
         it.toBuilder()
            .setLatitude(mapLocation.latitude)
            .setLongitude(mapLocation.longitude)
            .setZoom(mapLocation.zoom)
            .build()
      }
   }

   companion object {
      val BASE_LAYER_KEY = intPreferencesKey("mil.nga.msi.preference.baseLayer")
      val MGRS_KEY = booleanPreferencesKey("mil.nga.msi.preference.mgrs")
   }
}