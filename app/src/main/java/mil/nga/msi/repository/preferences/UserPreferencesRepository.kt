package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.map.BaseMapType
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>,
) {
   val baseMapType: Flow<BaseMapType> = preferencesDataStore.data.map {
      BaseMapType.fromValue(it.mapLayer)
   }

   suspend fun setBaseMapType(baseMapType: BaseMapType) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setMapLayer(baseMapType.value)
            .build()
      }
   }

   val mgrs: Flow<Boolean> = preferencesDataStore.data.map {
      it.mgrs
   }

   suspend fun setMGRS(enabled: Boolean) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setMgrs(enabled)
            .build()
      }
   }

   val mapLocation = preferencesDataStore.data.map { it.mapLocation }

   suspend fun setMapLocation(mapLocation: MapLocation) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.mapLocation.toBuilder()
            .setLatitude(mapLocation.latitude)
            .setLongitude(mapLocation.longitude)
            .setZoom(mapLocation.zoom)
            .build()

         builder.build()
      }
   }

   val lastReadNavigationalWarnings = preferencesDataStore.data.map { it.lastReadNavigationWarningsMap }

   suspend fun setLastReadNavigationalWarning(navigationArea: NavigationArea, key: NavigationalWarningKey) {
      preferencesDataStore.updateData {
         val preferenceKey = mil.nga.msi.type.NavigationalWarningKey.newBuilder()
            .setNumber(key.number.toLong())
            .setYear(key.year)
            .build()

         val builder = it.toBuilder()
         builder.putLastReadNavigationWarnings(navigationArea.code, preferenceKey)
         builder.build()
      }
   }
}