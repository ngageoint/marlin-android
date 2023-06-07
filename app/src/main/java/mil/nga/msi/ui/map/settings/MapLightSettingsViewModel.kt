package mil.nga.msi.ui.map.settings

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.DataSourceTileProvider
import mil.nga.msi.ui.map.overlay.LightImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class LightTileRepository(
   private val mapRepository: MapRepository
): TileRepository {
   val lights = mutableListOf<Light>().apply {
      add(
         Light(
            id = "PUB 110--14840--1",
            volumeNumber = "PUB 110",
            featureNumber = "14840",
            characteristicNumber = 1,
            noticeWeek = "06",
            noticeYear = "2015",
            latitude = 16.473,
            longitude = -61.507,
         ).apply {
            remarks = "R. 120°-163°, W.-170°, G.-200°."
            range = "W. 12 /n R. 9 /n G. 9"
            characteristic = "Fl.(2)W.R.G. period 6s fl. 1.0s, ec. 1.0s fl. 1.0s, ec. 3.0s"
         }
      )

      add(
         Light(
            id = "PUB 110--14836--1",
            volumeNumber = "PUB 110",
            featureNumber = "14836",
            characteristicNumber = 1,
            noticeWeek = "24",
            noticeYear = "2019",
            latitude = 16.41861,
            longitude = -61.5338,
         ).apply {
            range = "10"
            characteristic = "Fl.(3)W. period 12s"
         }
      )
   }

   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return lights.filter {
         it.latitude in minLatitude..maxLatitude &&
         it.longitude in minLongitude..maxLongitude
      }.map {
         LightImage(it, mapRepository)
      }
   }
}

class LightTileProvider(
   application: Application,
   repository: TileRepository
): DataSourceTileProvider(application, repository)

@HiltViewModel
class MapLightSettingsViewModel @Inject constructor(
   val application: Application,
   val mapRepository: MapRepository
): ViewModel() {

   private val lightTileRepository = LightTileRepository(mapRepository)

   val baseMap = mapRepository.baseMapType.asLiveData()
   val center = lightTileRepository.lights.first().run { LatLng(latitude, longitude)  }
   val showLightRanges = mapRepository.showLightRanges.asLiveData()
   val showSectorLightRanges = mapRepository.showSectorLightRanges.asLiveData()

   val lightTileProvider = MediatorLiveData<LightTileProvider>().apply {
      value = LightTileProvider(application, lightTileRepository)

      addSource(mapRepository.showLightRanges.asLiveData()) {
         value = LightTileProvider(application, lightTileRepository)
      }

      addSource(mapRepository.showSectorLightRanges.asLiveData()) {
         value = LightTileProvider(application, lightTileRepository)
      }
   }

   fun setShowLightRanges(enabled: Boolean) {
      viewModelScope.launch {
         mapRepository.setShowLightRanges(enabled)
      }
   }

   fun setShowSectorLightRanges(enabled: Boolean) {
      viewModelScope.launch {
         mapRepository.setShowSectorLightRanges(enabled)
      }
   }
}