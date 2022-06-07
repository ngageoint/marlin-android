package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.ui.map.BaseMapType
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
   private val dataStore: DataStore<Preferences>
) {
   val baseMapType: Flow<BaseMapType> = dataStore.data.map { preferences ->
      val value = preferences[BASE_LAYER_KEY]
      BaseMapType.fromValue(value)
   }

   suspend fun setBaseMapType(baseMapType: BaseMapType) {
      dataStore.edit { preferences ->
         preferences[BASE_LAYER_KEY] = baseMapType.value
      }
   }

   companion object {
      val BASE_LAYER_KEY = intPreferencesKey("mil.nga.msi.preference.baseLayer")
   }
}