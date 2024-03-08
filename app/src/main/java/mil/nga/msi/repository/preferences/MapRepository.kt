package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.coordinate.CoordinateSystem
import mil.nga.msi.type.MapLocation
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.search.SearchProvider
import javax.inject.Inject

class MapRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
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

   val coordinateSystem: Flow<CoordinateSystem> = preferencesDataStore.data.map {
      CoordinateSystem.fromName(it.map.coordinateSystem)
   }.distinctUntilChanged()

   suspend fun setCoordinateSystem(coordinateSystem: CoordinateSystem) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setCoordinateSystem(coordinateSystem.name)
            .build()

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

   val searchProvider: Flow<SearchProvider> = preferencesDataStore.data.map {
      SearchProvider.fromValue(it.map.searchProvider)
   }.distinctUntilChanged()

   suspend fun setSearchProvider(searchProvider: SearchProvider) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setSearchProvider(searchProvider.value)
            .build()

         builder.build()
      }
   }
}