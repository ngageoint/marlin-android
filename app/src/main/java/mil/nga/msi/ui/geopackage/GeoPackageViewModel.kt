package mil.nga.msi.ui.geopackage

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.extension.related.RelatedTablesExtension
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.geopackage.GeoPackageMediaProperty
import mil.nga.msi.geopackage.getFeature
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class GeoPackageViewModel @Inject constructor(
   private val application: Application,
   private val layerRepository: LayerRepository,
   private val bookmarkRepository: BookmarkRepository,
   private val geoPackageManager: GeoPackageManager
): ViewModel() {
   private val _keyFlow = MutableSharedFlow<GeoPackageFeatureKey>(replay = 1)
   fun setGeoPackageFeatureKey(key: GeoPackageFeatureKey) {
      viewModelScope.launch {
         _keyFlow.emit(key)
      }
   }

   private val _geoPackageFlow = _keyFlow.mapNotNull { key ->
      layerRepository.getLayer(key.layerId)?.let { layer ->
         try {
            val geoPackage = geoPackageManager.openExternal(layer.filePath)
            layer to geoPackage
         } catch (_: Exception) { null }
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   private val _bookmarkFlow = _keyFlow.flatMapLatest { key ->
      bookmarkRepository.observeBookmark(DataSource.GEOPACKAGE, key.id())
   }

   val feature = combine(
      _keyFlow,
      _geoPackageFlow,
      _bookmarkFlow
   ) { key, (layer, geoPackage), bookmark ->
      try {
         geoPackage.getFeature(layer.id, layer.name, key.table, key.featureId)?.copy(bookmark = bookmark)
      } catch (_: Exception) { null }
   }.asLiveData()

   val tileProvider = combine(
      _keyFlow,
      _geoPackageFlow
   ) { key, (_, geoPackage) ->
      val featureDao = geoPackage.getFeatureDao(key.table)
      val featureTiles = mil.nga.geopackage.tiles.features.DefaultFeatureTiles(
         application,
         geoPackage,
         featureDao
      )
      FeatureOverlay(featureTiles)
   }.asLiveData()

   fun setMedia(key: GeoPackageMediaKey) {
      viewModelScope.launch(Dispatchers.IO) {
         layerRepository.getLayer(key.layerId)?.let { layer ->
            val geoPackage = geoPackageManager.openExternal(layer.filePath)
            _media.postValue(getMedia(geoPackage, key.table, key.mediaId))
         }
      }
   }

   private val _media = MutableLiveData<GeoPackageMediaProperty?>()
   val media: LiveData<GeoPackageMediaProperty?> = _media

   private fun getMedia(
      geoPackage: GeoPackage,
      table: String,
      mediaId: Long
   ): GeoPackageMediaProperty? {
      val relatedTablesExtension = RelatedTablesExtension(geoPackage)
      val mediaDao = relatedTablesExtension.getMediaDao(table)
      val mediaRow = mediaDao.getRow(mediaDao.queryForIdRow(mediaId))
      return mediaRow.columns.getColumnIndex("content_type", false)?.let { index ->
         val contentType = mediaRow.getValue(index).toString()
         GeoPackageMediaProperty("Media", mediaRow.data, mediaDao.tableName, mediaRow.id, contentType)
      }
   }

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}