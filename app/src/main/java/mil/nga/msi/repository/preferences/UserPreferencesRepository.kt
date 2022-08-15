package mil.nga.msi.repository.preferences

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import javax.inject.Inject

enum class DataSource(val route: Route, val color:  Color) {
   ASAM(AsamRoute.Main, Color(0xFF000000)),
   MODU(ModuRoute.Main, Color(0xFF0042A4)),
   NAVIGATION_WARNING(NavigationWarningRoute.Main, Color(0xFFD32F2F)),
   LIGHT(LightRoute.Main, Color(0xFFFFC500))
}

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

   val gars: Flow<Boolean> = preferencesDataStore.data.map {
      it.gars
   }

   suspend fun setGARS(enabled: Boolean) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setGars(enabled)
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
         builder.mapLocation = builder.mapLocation.toBuilder()
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

   val mapped: Flow<Map<DataSource, Boolean>> = preferencesDataStore.data.map {
      it.mappedMap.mapKeys { entry ->
         DataSource.valueOf(entry.key)
      }
   }

   suspend fun setMapped(type: DataSource) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         val enabled = builder.mappedMap[type.name] ?: false
         builder.putMapped(type.name, !enabled)
         builder.build()
      }
   }

   val tabs: Flow<List<DataSource>> = preferencesDataStore.data.map {
      it.tabsList.map { name ->
         DataSource.valueOf(name)
      }
   }

   suspend fun setTabs(tabs: List<DataSource>) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .clearTabs()
            .addAllTabs(tabs.map { it.name })
            .build()
      }
   }
}