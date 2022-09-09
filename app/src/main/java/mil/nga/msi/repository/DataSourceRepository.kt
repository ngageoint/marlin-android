package mil.nga.msi.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject

class DataSourceRepository @Inject constructor(
   val application: Application,
   asamRepository: AsamRepository,
   moduRepository: ModuRepository,
   navigationalWarningRepository: NavigationalWarningRepository,
   lightRepository: LightRepository,
   portRepository: PortRepository,
   radioBeaconRepository: RadioBeaconRepository,
   dgpsStationRepository: DgpsStationRepository,
) {
   val fetching: LiveData<Map<DataSource, Boolean>> = MediatorLiveData<Map<DataSource, Boolean>>().apply {
      value = emptyMap()

      addSource(asamRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.ASAM, it) }
      }

      addSource(moduRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.MODU, it) }
      }

      addSource(navigationalWarningRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.NAVIGATION_WARNING, it) }
      }

      addSource(lightRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.LIGHT, it) }
      }

      addSource(portRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.PORT, it) }
      }

      addSource(radioBeaconRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.RADIO_BEACON, it) }
      }

      addSource(dgpsStationRepository.fetching) {
         value = value?.toMutableMap()?.apply { put(DataSource.RADIO_BEACON, it) }
      }
   }
}