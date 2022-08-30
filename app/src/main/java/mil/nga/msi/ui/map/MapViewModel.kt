package mil.nga.msi.ui.map

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.cluster.MapAnnotation
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MapViewModel @Inject constructor(
   asamRepository: AsamRepository,
   moduRepository: ModuRepository,
   val lightRepository: LightRepository,
   val portRepository: PortRepository,
   val beaconRepository: RadioBeaconRepository,
   val locationPolicy: LocationPolicy,
   val userPreferencesRepository: UserPreferencesRepository,
   @Named("lightTileProvider") private val lightTileProvider: TileProvider,
   @Named("portTileProvider") private val portTileProvider: TileProvider,
   @Named("radioBeaconTileProvider") private val radioBeaconTileProvider: TileProvider
): ViewModel() {

   val baseMap = userPreferencesRepository.baseMapType.asLiveData()
   val mapLocation = userPreferencesRepository.mapLocation.asLiveData()
   val gars = userPreferencesRepository.gars.asLiveData()
   val mgrs = userPreferencesRepository.mgrs.asLiveData()

   private val mapped = userPreferencesRepository.mapped.asLiveData()

   suspend fun setMapLocation(mapLocation: MapLocation) = userPreferencesRepository.setMapLocation(mapLocation)

   val tileProviders: LiveData<MutableList<TileProvider>> = Transformations.switchMap(mapped) { mapped ->
      MediatorLiveData<MutableList<TileProvider>>().apply {
         value = mutableListOf()

         if (mapped[DataSource.LIGHT] == true) {
            value?.add(lightTileProvider)
         } else {
            value?.remove(lightTileProvider)
         }

         if (mapped[DataSource.PORT] == true) {
            value?.add(portTileProvider)
         } else {
            value?.remove(portTileProvider)
         }

         if (mapped[DataSource.RADIO_BEACON] == true) {
            value?.add(radioBeaconTileProvider)
         } else {
            value?.remove(radioBeaconTileProvider)
         }
      }
   }

   private val _mapAnnotations = mutableMapOf<MapAnnotation.Type, List<MapAnnotation>>()
   val mapAnnotations = Transformations.switchMap(mapped) { mapped ->
      MediatorLiveData<List<MapAnnotation>>().apply {
         value = emptyList()

         val asamSource = asamRepository.asamMapItems.asLiveData()
         if (mapped[DataSource.ASAM] == true) {
            addSource(asamSource) { asams: List<AsamMapItem> ->
               _mapAnnotations[MapAnnotation.Type.ASAM] = asams.map { MapAnnotation.fromAsam(it) }
               value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            removeSource(asamSource)
            _mapAnnotations[MapAnnotation.Type.ASAM] = emptyList()
            value = _mapAnnotations.flatMap { entry ->  entry.value }
         }

         val moduSource = moduRepository.moduMapItems.asLiveData()
         if (mapped[DataSource.MODU] == true) {
            addSource(moduSource) { modus: List<ModuMapItem> ->
               _mapAnnotations[MapAnnotation.Type.MODU] = modus.map { MapAnnotation.fromModu(it) }
               value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            removeSource(moduSource)
            _mapAnnotations[MapAnnotation.Type.MODU] = emptyList()
            value = _mapAnnotations.flatMap { entry ->  entry.value }
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
      val lights = if (dataSources.containsKey(DataSource.LIGHT)) {
         lightRepository
            .getLights(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { light ->
               val key = MapAnnotation.Key(LightKey.fromLight(light).id(), MapAnnotation.Type.LIGHT)
               MapAnnotation(key, light.latitude, light.longitude)
            }
      } else emptyList()

      val ports = if (dataSources.containsKey(DataSource.PORT)) {
         portRepository
            .getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { port ->
               val key = MapAnnotation.Key(port.portNumber.toString(), MapAnnotation.Type.PORT)
               MapAnnotation(key, port.latitude, port.longitude)
            }
      } else emptyList()

      val beacons = if (dataSources.containsKey(DataSource.PORT)) {
         beaconRepository
            .getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude)
            .map { beacon ->
               val key = MapAnnotation.Key(RadioBeaconKey.fromRadioBeacon(beacon).id(), MapAnnotation.Type.RADIO_BEACON)
               MapAnnotation(key, beacon.latitude, beacon.longitude)
            }
      } else emptyList()

      lights + ports + beacons
   }
}