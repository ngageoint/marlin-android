package mil.nga.msi.repository.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.type.Developer
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.map.BaseMapType
import java.time.Instant
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>,
) {
   val baseMapType: Flow<BaseMapType> = preferencesDataStore.data.map {
      BaseMapType.fromValue(it.map.mapLayer)
   }.distinctUntilChanged()

   suspend fun setBaseMapType(baseMapType: BaseMapType) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setMapLayer(baseMapType.value)
            .build()

         builder.build()
      }
   }

   val gars: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.gars
   }.distinctUntilChanged()

   suspend fun setGARS(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setGars(enabled)
            .build()

         builder.build()
      }
   }

   val mgrs: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.mgrs
   }.distinctUntilChanged()

   suspend fun setMGRS(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setMgrs(enabled)
            .build()

         builder.build()
      }
   }

   val showLightRanges: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.showLightRanges
   }.distinctUntilChanged()

   suspend fun setShowLightRanges(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setShowLightRanges(enabled)
            .build()

         builder.build()
      }
   }

   val showSectorLightRanges: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.showLightSectorRanges
   }.distinctUntilChanged()

   suspend fun setShowSectorLightRanges(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setShowLightSectorRanges(enabled)
            .build()

         builder.build()
      }
   }

   val showLocation: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.showLocation
   }.distinctUntilChanged()

   suspend fun setShowLocation(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setShowLocation(enabled)
            .build()

         builder.build()
      }
   }

   val showScale: Flow<Boolean> = preferencesDataStore.data.map {
      it.map.showScale
   }.distinctUntilChanged()

   suspend fun setShowScale(enabled: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setShowScale(enabled)
            .build()

         builder.build()
      }
   }

   val mapLocation = preferencesDataStore.data.map { it.map.mapLocation }.distinctUntilChanged()
   suspend fun setMapLocation(mapLocation: MapLocation) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         val mapBuilder = builder.map.toBuilder()
         builder.map = mapBuilder.setMapLocation(mapBuilder.mapLocation.toBuilder()
            .setLatitude(mapLocation.latitude)
            .setLongitude(mapLocation.longitude)
            .setZoom(mapLocation.zoom)
         ).build()

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

   suspend fun fetched(dataSource: DataSource): Instant? {
      return preferencesDataStore.data.first().dataSourceSyncDateMap[dataSource.name]?.let {
         Instant.ofEpochSecond(it)
      }
   }

   suspend fun setFetched(dataSource: DataSource, instant: Instant) {
      preferencesDataStore.updateData { preferences ->
         val builder = preferences.toBuilder()
         builder.putDataSourceSyncDate(dataSource.name, instant.epochSecond)
         builder.build()
      }
   }

   val layers: Flow<List<Int>> = preferencesDataStore.data.map {
      it.layersList
   }.distinctUntilChanged()

   suspend fun setLayers(layers: List<Int>) {
      preferencesDataStore.updateData { preferences ->
         preferences.toBuilder()
            .clearLayers()
            .addAllLayers(layers)
            .build()
      }
   }

   fun developer(): Flow<Developer> {
      return preferencesDataStore.data.map {
         it.developer
      }.distinctUntilChanged()
   }

   suspend fun setDeveloperMode() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.developer = builder.developer.toBuilder()
            .setShowDeveloperMode(true)
            .build()

         builder.build()
      }
   }

   suspend fun setShowNoLocationNavigationWarnings(show: Boolean) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.developer = builder.developer.toBuilder()
            .setShowNonParsedNavigationWarnings(show)
            .build()

         builder.build()
      }
   }
}