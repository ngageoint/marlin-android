package mil.nga.msi.repository.preferences

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.*
import mil.nga.msi.R
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
import mil.nga.msi.ui.port.PortRoute
import javax.inject.Inject

enum class DataSource(val route: Route, val mappable: Boolean, val color:  Color, val icon: Int) {
   ASAM(AsamRoute.Main, true, Color(0xFF000000), R.drawable.ic_asam_24dp),
   MODU(ModuRoute.Main,true, Color(0xFF0042A4), R.drawable.ic_modu_24dp),
   NAVIGATION_WARNING(NavigationWarningRoute.Main, false, Color(0xFFD32F2F), R.drawable.ic_round_warning_24),
   LIGHT(LightRoute.Main, true, Color(0xFFFFC500), R.drawable.ic_baseline_lightbulb_24),
   PORT(PortRoute.Main, true, Color(0xFF5856d6), R.drawable.ic_baseline_anchor_24)
}

class UserPreferencesRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>,
) {
   val baseMapType: Flow<BaseMapType> = preferencesDataStore.data.map {
      BaseMapType.fromValue(it.mapLayer)
   }.distinctUntilChanged()

   suspend fun setBaseMapType(baseMapType: BaseMapType) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setMapLayer(baseMapType.value)
            .build()
      }
   }

   val gars: Flow<Boolean> = preferencesDataStore.data.map {
      it.gars
   }.distinctUntilChanged()

   suspend fun setGARS(enabled: Boolean) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setGars(enabled)
            .build()
      }
   }

   val mgrs: Flow<Boolean> = preferencesDataStore.data.map {
      it.mgrs
   }.distinctUntilChanged()

   suspend fun setMGRS(enabled: Boolean) {
      preferencesDataStore.updateData {
         it.toBuilder()
            .setMgrs(enabled)
            .build()
      }
   }

   val mapLocation = preferencesDataStore.data.map { it.mapLocation }.distinctUntilChanged()

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

   val lastReadNavigationalWarnings: Flow<MutableMap<String,  mil.nga.msi.type.NavigationalWarningKey>> = preferencesDataStore.data.map { it.lastReadNavigationWarningsMap }

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

   val mapped: Flow<Map<DataSource, Boolean>> = preferencesDataStore.data.map { preferences ->
      preferences.mappedMap.mapKeys { DataSource.valueOf(it.key) }
   }.distinctUntilChanged()

   suspend fun setMapped(type: DataSource) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         val enabled = builder.mappedMap[type.name] ?: false
         builder.putMapped(type.name, !enabled)
         builder.build()
      }
   }

   val tabs: Flow<List<DataSource>> = preferencesDataStore.data.map { preferences ->
      preferences.tabsList.map { DataSource.valueOf(it) }
   }

   suspend fun setTabs(tabs: List<DataSource>) {
      preferencesDataStore.updateData { preferences ->
         preferences.toBuilder()
            .clearTabs()
            .addAllTabs(tabs.map { it.name }.toSet())
            .build()
      }
   }

   val nonTabs: Flow<List<DataSource>> = preferencesDataStore.data.map { preferences ->
      preferences.nonTabsList.map { DataSource.valueOf(it) }
   }

   suspend fun setNonTabs(tabs: List<DataSource>) {
      preferencesDataStore.updateData { preferences ->
         preferences.toBuilder()
            .clearNonTabs()
            .addAllNonTabs(tabs.map { it.name }.toSet())
            .build()
      }
   }
}