package mil.nga.msi.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.DataSourceRepository
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.map.*
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.overlay.*
import javax.inject.Inject
import javax.inject.Named

enum class TileProviderType {
   MGRS,
   GARS,
   OSM,
   ASAM,
   MODU,
   LIGHT,
   PORT,
   RADIO_BEACON,
   DGPS_STATION
}

@HiltViewModel
class MapViewModel @Inject constructor(
   private val application: Application,
   private val asamRepository: AsamRepository,
   private val asamTileRepository: AsamTileRepository,
   private val moduRepository: ModuRepository,
   private val moduTileRepository: ModuTileRepository,
   private val lightRepository: LightRepository,
   private val lightTileRepository: LightTileRepository,
   private val portRepository: PortRepository,
   private val portTileRepository: PortTileRepository,
   private val beaconRepository: RadioBeaconRepository,
   private val beaconTileRepository: RadioBeaconTileRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val dgpsStationTileRepository: DgpsStationTileRepository,
   dataSourceRepository: DataSourceRepository,
   val locationPolicy: LocationPolicy,
   val userPreferencesRepository: UserPreferencesRepository,
   @Named("osmTileProvider") private val osmTileProvider: TileProvider,
   @Named("mgrsTileProvider") private val mgrsTileProvider: TileProvider,
   @Named("garsTileProvider") private val garsTileProvider: TileProvider,
): ViewModel() {

   val baseMap = userPreferencesRepository.baseMapType.asLiveData()
   val mapLocation = userPreferencesRepository.mapLocation.asLiveData()
   val fetching = dataSourceRepository.fetching

   private val _zoom = MutableLiveData<Int>()
   private val mapped = userPreferencesRepository.mapped.asLiveData()

   suspend fun setMapLocation(mapLocation: MapLocation, zoom: Int) {
      _zoom.value = zoom
      userPreferencesRepository.setMapLocation(mapLocation)
   }

   private var asamTileProvider = AsamTileProvider(application, asamTileRepository)
   private var moduTileProvider = ModuTileProvider(application, moduTileRepository)
   private var portTileProvider = PortTileProvider(application, portTileRepository)
   private var beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
   private var lightTileProvider = LightTileProvider(application, lightTileRepository)
   private var dgpsTileProvider = DgpsStationTileProvider(application, dgpsStationTileRepository)

   val tileProviders: LiveData<Map<TileProviderType, TileProvider>> = MediatorLiveData<Map<TileProviderType, TileProvider>>().apply {
      addSource(userPreferencesRepository.mgrs.asLiveData()) { enabled ->
         val providers = value?.toMutableMap() ?: mutableMapOf()
         if (enabled) providers[TileProviderType.MGRS] = mgrsTileProvider else providers.remove(TileProviderType.MGRS)
         value = providers
      }

      addSource(userPreferencesRepository.gars.asLiveData()) { enabled ->
         val providers = value?.toMutableMap() ?: mutableMapOf()
         if (enabled) providers[TileProviderType.GARS] = garsTileProvider else providers.remove(TileProviderType.GARS)
         value = providers
      }

      addSource(baseMap) { baseMap ->
         val providers = value?.toMutableMap() ?: mutableMapOf()

         if (baseMap == BaseMapType.OSM) {
            providers[TileProviderType.OSM] = osmTileProvider
         } else {
            providers.remove(TileProviderType.OSM)
         }

         value = providers
      }

      addSource(mapped) { mapped ->
         val providers = value?.toMutableMap() ?: mutableMapOf()

         if (mapped[DataSource.ASAM] == true) {
            asamTileProvider = AsamTileProvider(application, asamTileRepository)
            providers[TileProviderType.ASAM] = asamTileProvider
         } else {
            providers.remove(TileProviderType.ASAM)
         }

         if (mapped[DataSource.MODU] == true) {
            moduTileProvider = ModuTileProvider(application, moduTileRepository)
            providers[TileProviderType.MODU] = moduTileProvider
         } else {
            providers.remove(TileProviderType.MODU)
         }

         if (mapped[DataSource.LIGHT] == true) {
            lightTileProvider = LightTileProvider(application, lightTileRepository)
            providers[TileProviderType.LIGHT] = lightTileProvider
         } else {
            providers.remove(TileProviderType.LIGHT)
         }

         if (mapped[DataSource.PORT] == true) {
            portTileProvider = PortTileProvider(application, portTileRepository)
            providers[TileProviderType.PORT] = portTileProvider
         } else {
            providers.remove(TileProviderType.PORT)
         }

         if (mapped[DataSource.RADIO_BEACON] == true) {
            beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
            providers[TileProviderType.RADIO_BEACON] = beaconTileProvider
         } else {
            providers.remove(TileProviderType.RADIO_BEACON)
         }

         if (mapped[DataSource.DGPS_STATION] == true) {
            dgpsTileProvider = DgpsStationTileProvider(application, dgpsStationTileRepository)
            providers[TileProviderType.DGPS_STATION] = dgpsTileProvider
         } else {
            providers.remove(TileProviderType.DGPS_STATION)
         }

         value = providers
      }

      addSource(asamRepository.observeAsamMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.ASAM) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            asamTileProvider = AsamTileProvider(application, asamTileRepository)
            providers[TileProviderType.ASAM] = asamTileProvider
            value = providers
         }
      }

      addSource(moduRepository.moduMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.MODU) == true) {
            if (mapped.value?.get(DataSource.MODU) == true) {
               val providers = value?.toMutableMap() ?: mutableMapOf()
               moduTileProvider = ModuTileProvider(application, moduTileRepository)
               providers[TileProviderType.MODU] = moduTileProvider
               value = providers
            }
         }
      }

      addSource(lightRepository.lightMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.LIGHT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            lightTileProvider = LightTileProvider(application, lightTileRepository)
            providers[TileProviderType.LIGHT] = lightTileProvider
            value = providers
         }
      }

      addSource(portRepository.portMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.PORT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            portTileProvider = PortTileProvider(application, portTileRepository)
            providers[TileProviderType.PORT] = portTileProvider
            value = providers
         }
      }

      addSource(beaconRepository.radioBeaconMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.RADIO_BEACON) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
            providers[TileProviderType.RADIO_BEACON] = beaconTileProvider
            value = providers
         }
      }

      addSource(dgpsStationRepository.dgpsStationMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.DGPS_STATION) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            dgpsTileProvider = DgpsStationTileProvider(application, dgpsStationTileRepository)
            providers[TileProviderType.DGPS_STATION] = dgpsTileProvider
            value = providers
         }
      }
   }

   suspend fun getMapAnnotations(
      minLongitude: Double,
      maxLongitude: Double,
      minLatitude: Double,
      maxLatitude: Double
   ) = withContext(Dispatchers.IO) {
      val dataSources = mapped.value ?: emptyMap()

      val asams = if (dataSources[DataSource.ASAM] == true) {
         asamRepository
            .getAsams(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { asam ->
               val key = MapAnnotation.Key(asam.reference, MapAnnotation.Type.ASAM)
               MapAnnotation(key, asam.latitude, asam.longitude)
            }
      } else emptyList()

      val modus = if (dataSources[DataSource.MODU] == true) {
         moduRepository
            .getModus(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { modu ->
               val key = MapAnnotation.Key(modu.name, MapAnnotation.Type.MODU)
               MapAnnotation(key, modu.latitude, modu.longitude)
            }
      } else emptyList()

      val lights = if (dataSources[DataSource.LIGHT] == true) {
         lightRepository
            .getLights(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { light ->
               val key = MapAnnotation.Key(LightKey.fromLight(light).id(), MapAnnotation.Type.LIGHT)
               MapAnnotation(key, light.latitude, light.longitude)
            }
      } else emptyList()

      val ports = if (dataSources[DataSource.PORT] == true) {
         portRepository
            .getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { port ->
               val key = MapAnnotation.Key(port.portNumber.toString(), MapAnnotation.Type.PORT)
               MapAnnotation(key, port.latitude, port.longitude)
            }
      } else emptyList()

      val beacons = if (dataSources[DataSource.RADIO_BEACON] == true) {
         beaconRepository
            .getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { beacon ->
               val key = MapAnnotation.Key(RadioBeaconKey.fromRadioBeacon(beacon).id(), MapAnnotation.Type.RADIO_BEACON)
               MapAnnotation(key, beacon.latitude, beacon.longitude)
            }
      } else emptyList()

      val dgps = if (dataSources[DataSource.DGPS_STATION] == true) {
         dgpsStationRepository
            .getDgpsStations(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { dgps ->
               val key = MapAnnotation.Key(DgpsStationKey.fromDgpsStation(dgps).id(), MapAnnotation.Type.DGPS_STATION)
               MapAnnotation(key, dgps.latitude, dgps.longitude)
            }
      } else emptyList()

     asams + modus + lights + ports + beacons + dgps
   }
}