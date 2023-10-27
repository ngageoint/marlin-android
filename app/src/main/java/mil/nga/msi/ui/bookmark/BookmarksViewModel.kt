package mil.nga.msi.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.geopackage.getFeature
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationRepository
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject

data class ItemWithBookmark(
   val item: Any,
   val dataSource: DataSource,
   val bookmark: Bookmark
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
   private val repository: BookmarkRepository,
   private val asamRepository: AsamRepository,
   private val electronicPublicationRepository: ElectronicPublicationRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val layerRepository: LayerRepository,
   private val lightRepository: LightRepository,
   private val moduRepository: ModuRepository,
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val portRepository: PortRepository,
   private val radioBeaconRepository: RadioBeaconRepository,
   private val geoPackageManager: GeoPackageManager
): ViewModel() {

   val bookmarks = repository.observeBookmarks().map { bookmarks ->
      bookmarks.mapNotNull { bookmark ->
         when(bookmark.dataSource) {
            DataSource.ASAM -> {
               asamRepository.getAsam(bookmark.id)?.let { asam ->
                  ItemWithBookmark(asam, DataSource.ASAM, bookmark)
               }
            }
            DataSource.DGPS_STATION -> {
               val key = DgpsStationKey.fromId(bookmark.id)
               dgpsStationRepository.getDgpsStation(key.volumeNumber, key.featureNumber)?.let { dgpsStation ->
                  ItemWithBookmark(dgpsStation, DataSource.DGPS_STATION, bookmark)
               }
            }
            DataSource.GEOPACKAGE -> {
               val key = GeoPackageFeatureKey.fromId(bookmark.id)
               layerRepository.getLayer(key.layerId)?.let { layer ->
                  val geoPackage = geoPackageManager.openExternal(layer.filePath)
                  geoPackage.getFeature(layer.id, layer.name, key.table, key.featureId)?.let { feature ->
                     ItemWithBookmark(key to feature, DataSource.GEOPACKAGE, bookmark)
                  }
               }
            }
            DataSource.ELECTRONIC_PUBLICATION -> {
               electronicPublicationRepository.getElectronicPublication(s3Key = bookmark.id)?.let { publication ->
                  ItemWithBookmark(publication, DataSource.ELECTRONIC_PUBLICATION, bookmark)
               }
            }
            DataSource.LIGHT -> {
               val key = LightKey.fromId(bookmark.id)
               lightRepository.getLight(key.volumeNumber, key.featureNumber, key.characteristicNumber)?.let { light ->
                  ItemWithBookmark(light, DataSource.LIGHT, bookmark)
               }
            }
            DataSource.MODU -> {
               moduRepository.getModu(bookmark.id)?.let { modu ->
                  ItemWithBookmark(modu, DataSource.MODU, bookmark)
               }
            }

            DataSource.NAVIGATION_WARNING -> {
               val key = NavigationalWarningKey.fromId(bookmark.id)
               navigationalWarningRepository.getNavigationalWarning(key)?.let { warning ->
                  ItemWithBookmark(warning, DataSource.NAVIGATION_WARNING, bookmark)
               }
            }
            DataSource.NOTICE_TO_MARINERS -> {
               val noticeNumber = bookmark.id.toInt()
               ItemWithBookmark(noticeNumber, DataSource.NOTICE_TO_MARINERS, bookmark)
            }
            DataSource.PORT -> {
               bookmark.id.toIntOrNull()?.let { portNumber ->
                  portRepository.getPort(portNumber)?.let { port ->
                     ItemWithBookmark(port, DataSource.PORT, bookmark)
                  }
               }
            }
            DataSource.RADIO_BEACON -> {
               val key = RadioBeaconKey.fromId(bookmark.id)
               radioBeaconRepository.getRadioBeacon(key)?.let { beacon ->
                  ItemWithBookmark(beacon, DataSource.RADIO_BEACON, bookmark)
               }
            }
            else -> null
         }
      }
   }.asLiveData()

   fun deleteBookmark(key: BookmarkKey) {
      viewModelScope.launch {
         val bookmark = Bookmark(key.id, key.dataSource)
         repository.delete(bookmark)
      }
   }
}