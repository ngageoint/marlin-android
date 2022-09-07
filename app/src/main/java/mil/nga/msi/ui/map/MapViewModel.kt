package mil.nga.msi.ui.map

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.asam.AsamRepository
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

@HiltViewModel
class MapViewModel @Inject constructor(
   private val application: Application,
   val asamRepository: AsamRepository,
   val asamTileRepository: AsamTileRepository,
   val moduRepository: ModuRepository,
   private val moduTileRepository: ModuTileRepository,
   val lightRepository: LightRepository,
   private val lightTileRepository: LightTileRepository,
   val portRepository: PortRepository,
   private val portTileRepository: PortTileRepository,
   private val beaconRepository: RadioBeaconRepository,
   private val beaconTileRepository: RadioBeaconTileRepository,
   val locationPolicy: LocationPolicy,
   val userPreferencesRepository: UserPreferencesRepository,
   @Named("osmTileProvider") private val osmTileProvider: TileProvider,
   @Named("mgrsTileProvider") private val mgrsTileProvider: TileProvider,
   @Named("garsTileProvider") private val garsTileProvider: TileProvider,
): ViewModel() {

   val baseMap = userPreferencesRepository.baseMapType.asLiveData()
   val mapLocation = userPreferencesRepository.mapLocation.asLiveData()
   private val _zoom = MutableLiveData<Int>()
   private val mapped = userPreferencesRepository.mapped.asLiveData()


   suspend fun setMapLocation(mapLocation: MapLocation, zoom: Int) {
      _zoom.value = zoom
      userPreferencesRepository.setMapLocation(mapLocation)
   }

   private val _tileProviders = MediatorLiveData<Set<TileProvider>>()
   val tileProviders: LiveData<Set<TileProvider>> = _tileProviders
   private var asamTileProvider = AsamTileProvider(application, asamTileRepository)
   private var moduTileProvider = ModuTileProvider(application, moduTileRepository)
   private var portTileProvider = PortTileProvider(application, portTileRepository)
   private var beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
   private var lightTileProvider = LightTileProvider(application, lightTileRepository)
   init {
      _tileProviders.addSource(userPreferencesRepository.mgrs.asLiveData()) { enabled ->
         val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
         if (enabled) providers.add(mgrsTileProvider) else providers.remove(mgrsTileProvider)
         _tileProviders.value = providers
      }

      _tileProviders.addSource(userPreferencesRepository.gars.asLiveData()) { enabled ->
         val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
         if (enabled) providers.add(garsTileProvider) else providers.remove(garsTileProvider)
         _tileProviders.value = providers
      }

      _tileProviders.addSource(baseMap) { baseMap ->
         val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()

         if (baseMap == BaseMapType.OSM) {
            providers.add(osmTileProvider)
         } else {
            providers.remove(osmTileProvider)
         }

         _tileProviders.value = providers
      }

      _tileProviders.addSource(mapped) { mapped ->
         val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()

         if (mapped[DataSource.ASAM] == true) {
            providers.remove(asamTileProvider)
            asamTileProvider = AsamTileProvider(application, asamTileRepository)
            providers.add(asamTileProvider)
         } else {
            providers.remove(asamTileProvider)
         }

         if (mapped[DataSource.MODU] == true) {
            providers.remove(moduTileProvider)
            moduTileProvider = ModuTileProvider(application, moduTileRepository)
            providers.add(moduTileProvider)
         } else {
            providers.remove(moduTileProvider)
         }

         if (mapped[DataSource.LIGHT] == true) {
            providers.remove(lightTileProvider)
            lightTileProvider = LightTileProvider(application, lightTileRepository)
            providers.add(lightTileProvider)
         } else {
            providers.remove(lightTileProvider)
         }

         if (mapped[DataSource.PORT] == true) {
            providers.remove(portTileProvider)
            portTileProvider = PortTileProvider(application, portTileRepository)
            providers.add(portTileProvider)
         } else {
            providers.remove(portTileProvider)
         }

         if (mapped[DataSource.RADIO_BEACON] == true) {
            providers.remove(beaconTileProvider)
            beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
            providers.add(beaconTileProvider)
         } else {
            providers.remove(beaconTileProvider)
         }

         _tileProviders.value = providers
      }

      _tileProviders.addSource(asamRepository.asamMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.ASAM) == true) {
            val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
            providers.remove(asamTileProvider)
            asamTileProvider = AsamTileProvider(application, asamTileRepository)
            providers.add(asamTileProvider)
            _tileProviders.value = providers
         }
      }

      _tileProviders.addSource(moduRepository.moduMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.MODU) == true) {
            if (mapped.value?.get(DataSource.MODU) == true) {
               val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
               providers.remove(moduTileProvider)
               moduTileProvider = ModuTileProvider(application, moduTileRepository)
               providers.add(moduTileProvider)
               _tileProviders.value = providers
            }
         }
      }

      _tileProviders.addSource(lightRepository.lightMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.LIGHT) == true) {
            val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
            providers.remove(lightTileProvider)
            lightTileProvider = LightTileProvider(application, lightTileRepository)
            providers.add(lightTileProvider)
            _tileProviders.value = providers
         }
      }

      _tileProviders.addSource(portRepository.portMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.PORT) == true) {
            val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
            providers.remove(portTileProvider)
            portTileProvider = PortTileProvider(application, portTileRepository)
            providers.add(portTileProvider)
            _tileProviders.value = providers
         }
      }

      _tileProviders.addSource(beaconRepository.radioBeaconMapItems.distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.RADIO_BEACON) == true) {
            val providers = _tileProviders.value?.toMutableSet() ?: mutableSetOf()
            providers.remove(beaconTileProvider)
            beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
            providers.add(beaconTileProvider)
            _tileProviders.value = providers
         }
      }
   }

   private val _mapAnnotations = mutableMapOf<MapAnnotation.Type, List<MapAnnotation>>()
   private val _markers = MediatorLiveData<List<MapAnnotation>>()
   val mapAnnotations: LiveData<List<MapAnnotation>> = _markers
   init {
      _markers.addSource(_zoom) { zoom ->
         val asamSource = asamRepository.asamMapItems.asLiveData()
         if (mapped.value?.get(DataSource.ASAM) == true && zoom >= 13) {
            _markers.addSource(asamSource) { asams: List<AsamMapItem> ->
               _mapAnnotations[MapAnnotation.Type.ASAM] = asams.map { MapAnnotation.fromAsam(it) }
               _markers.value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            _markers.removeSource(asamSource)
            _mapAnnotations[MapAnnotation.Type.ASAM] = emptyList()
            _markers.value = _mapAnnotations.flatMap { entry ->  entry.value }
         }

         val moduSource = moduRepository.moduMapItems.asLiveData()
         if (mapped.value?.get(DataSource.MODU) == true && zoom >= 13) {
            _markers.addSource(moduSource) { modus: List<ModuMapItem> ->
               _mapAnnotations[MapAnnotation.Type.MODU] = modus.map { MapAnnotation.fromModu(it) }
               _markers.value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            _markers.removeSource(moduSource)
            _mapAnnotations[MapAnnotation.Type.MODU] = emptyList()
            _markers.value = _mapAnnotations.flatMap { entry ->  entry.value }
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

      val beacons = if (dataSources[DataSource.PORT] == true) {
         beaconRepository
            .getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { beacon ->
               val key = MapAnnotation.Key(RadioBeaconKey.fromRadioBeacon(beacon).id(), MapAnnotation.Type.RADIO_BEACON)
               MapAnnotation(key, beacon.latitude, beacon.longitude)
            }
      } else emptyList()

      asams + modus + lights + ports + beacons
   }
}