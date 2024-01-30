package mil.nga.msi.ui.map

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.geopackage.map.tiles.overlay.XYZGeoPackageOverlay
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.repository.DataSourceRepository
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.geocoder.GeocoderRemoteDataSource
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.map.*
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.repository.preferences.SharedPreferencesRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.repository.route.RouteRepository
import mil.nga.msi.type.MapLocation
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
   DGPS_STATION,
   NAVIGATIONAL_WARNING,
   ROUTE
}

@HiltViewModel
class MapViewModel @Inject constructor(
   private val application: Application,
   private val layerService: LayerService,
   filterRepository: FilterRepository,
   layerRepository: LayerRepository,
   private val bottomSheetRepository: BottomSheetRepository,
   private val geoPackageManager: GeoPackageManager,
   private val asamRepository: AsamRepository,
   private val asamsTileRepository: AsamsTileRepository,
   private val moduRepository: ModuRepository,
   private val modusTileRepository: ModusTileRepository,
   private val lightRepository: LightRepository,
   private val lightsTileRepository: LightsTileRepository,
   private val portRepository: PortRepository,
   private val portsTileRepository: PortsTileRepository,
   private val beaconRepository: RadioBeaconRepository,
   private val beaconTileRepository: RadioBeaconsTileRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val dgpsStationsTileRepository: DgpsStationsTileRepository,
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val navigationalWarningsTileRepository: NavigationalWarningsTileRepository,
   private val routeRepository: RouteRepository,
   private val routesTileRepository: RoutesTileRepository,
   dataSourceRepository: DataSourceRepository,
   val locationPolicy: LocationPolicy,
   private val preferencesRepository: SharedPreferencesRepository,
   val mapRepository: MapRepository,
   val userPreferencesRepository: UserPreferencesRepository,
   val annotationProvider: AnnotationProvider,
   private val geocoderRemoteDataSource: GeocoderRemoteDataSource,
   @Named("osmTileProvider") private val osmTileProvider: TileProvider,
   @Named("mgrsTileProvider") private val mgrsTileProvider: TileProvider,
   @Named("garsTileProvider") private val garsTileProvider: TileProvider
): ViewModel() {

   val baseMap = mapRepository.baseMapType.asLiveData()
   val mapLocation = mapRepository.mapLocation.asLiveData()
   val showLocation = mapRepository.showLocation.asLiveData()
   val showScale = mapRepository.showScale.asLiveData()
   val fetching = dataSourceRepository.fetching
   val mapped = userPreferencesRepository.mapped.asLiveData()
   val coordinateSystem = mapRepository.coordinateSystem.asLiveData()

   val orderedDataSources = combine(
      userPreferencesRepository.tabs,
      userPreferencesRepository.nonTabs
   ) { tabs, nonTabs ->
      tabs + nonTabs
   }.asLiveData()

   private val _zoom = MutableLiveData<Int>()

   suspend fun setMapLocation(mapLocation: MapLocation, zoom: Int) {
      _zoom.value = zoom
      mapRepository.setMapLocation(mapLocation)
   }

   suspend fun setTapLocation(point: LatLng, bounds: LatLngBounds): Int {
      return bottomSheetRepository.setLocation(point, bounds)
   }

   private var asamTileProvider = DataSourceTileProvider(application, asamsTileRepository)
   private var moduTileProvider = DataSourceTileProvider(application, modusTileRepository)
   private var portTileProvider = DataSourceTileProvider(application, portsTileRepository)
   private var beaconTileProvider = DataSourceTileProvider(application, beaconTileRepository)
   private var lightTileProvider = DataSourceTileProvider(application, lightsTileRepository)
   private var dgpsTileProvider = DataSourceTileProvider(application, dgpsStationsTileRepository)
   private var navigationWarningTileProvider = DataSourceTileProvider(application, navigationalWarningsTileRepository)
   private var routeTileProvider = DataSourceTileProvider(application, routesTileRepository)

   private val searchText = MutableStateFlow("")
   fun search(text: String) {
      searchText.value = text
   }

   val searchResults = searchText
      .map {
         if (it.isNotEmpty()) {
            geocoderRemoteDataSource.geocode(it)
         } else emptyList()
      }.flowOn(Dispatchers.IO)
      .asLiveData()

   fun toggleOnMap(dataSource: DataSource) {
      viewModelScope.launch {
         userPreferencesRepository.setMapped(dataSource)
      }
   }

   val filterCount = filterRepository.filters.map { entry ->
      entry.values.fold(0) { count, filters -> count + filters.size }
   }.asLiveData()

   val layers = combine(mapRepository.layers, layerRepository.observeVisibleLayers()) { order, layers ->
      val orderById = order.withIndex().associate { (index, it) -> it to index }
      layers.sortedBy { orderById[it.id.toInt()] }
   }.transform { layers ->
      val tileProviders = layers.flatMap {  layer ->
         val credentials = preferencesRepository.getLayerCredentials(layer.id)

         when (layer.type) {
            LayerType.WMS -> {
               listOf(WMSTileProvider(url = layer.url, service = layerService, credentials = credentials))
            }
            LayerType.XYZ, LayerType.TMS -> {
               listOf(GridTileProvider(layer = layer, service = layerService, credentials = credentials))
            }
            LayerType.GEOPACKAGE -> {
               val geoPackage = geoPackageManager.openExternal(layer.filePath)
               layer.url.split(",").filter { it.isNotEmpty() }.map { table ->
                  if (geoPackage.tileTables.contains(table)) {
                     val tileDao = geoPackage.getTileDao(table)
                     XYZGeoPackageOverlay(tileDao)
                  } else {
                     val featureDao = geoPackage.getFeatureDao(table)
                     val featureTiles = mil.nga.geopackage.tiles.features.DefaultFeatureTiles(
                        application,
                        geoPackage,
                        featureDao
                     )
                     FeatureOverlay(featureTiles)
                  }
               }
            }
         }
      }

      emit(tileProviders)
   }.asLiveData()

   val tileProviders: LiveData<Map<TileProviderType, TileProvider>> = MediatorLiveData<Map<TileProviderType, TileProvider>>().apply {
      addSource(mapRepository.mgrs.asLiveData()) { enabled ->
         val providers = value?.toMutableMap() ?: mutableMapOf()
         if (enabled) providers[TileProviderType.MGRS] = mgrsTileProvider else providers.remove(TileProviderType.MGRS)
         value = providers
      }

      addSource(mapRepository.gars.asLiveData()) { enabled ->
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
            asamTileProvider = DataSourceTileProvider(application, asamsTileRepository)
            providers[TileProviderType.ASAM] = asamTileProvider
         } else {
            providers.remove(TileProviderType.ASAM)
         }

         if (mapped[DataSource.MODU] == true) {
            moduTileProvider = DataSourceTileProvider(application, modusTileRepository)
            providers[TileProviderType.MODU] = moduTileProvider
         } else {
            providers.remove(TileProviderType.MODU)
         }

         if (mapped[DataSource.LIGHT] == true) {
            lightTileProvider = DataSourceTileProvider(application, lightsTileRepository)
            providers[TileProviderType.LIGHT] = lightTileProvider
         } else {
            providers.remove(TileProviderType.LIGHT)
         }

         if (mapped[DataSource.PORT] == true) {
            portTileProvider = DataSourceTileProvider(application, portsTileRepository)
            providers[TileProviderType.PORT] = portTileProvider
         } else {
            providers.remove(TileProviderType.PORT)
         }

         if (mapped[DataSource.RADIO_BEACON] == true) {
            beaconTileProvider = DataSourceTileProvider(application, beaconTileRepository)
            providers[TileProviderType.RADIO_BEACON] = beaconTileProvider
         } else {
            providers.remove(TileProviderType.RADIO_BEACON)
         }

         if (mapped[DataSource.DGPS_STATION] == true) {
            dgpsTileProvider = DataSourceTileProvider(application, dgpsStationsTileRepository)
            providers[TileProviderType.DGPS_STATION] = dgpsTileProvider
         } else {
            providers.remove(TileProviderType.DGPS_STATION)
         }

         if (mapped[DataSource.NAVIGATION_WARNING] == true) {
            navigationWarningTileProvider = DataSourceTileProvider(application, navigationalWarningsTileRepository)
            providers[TileProviderType.NAVIGATIONAL_WARNING] = navigationWarningTileProvider
         } else {
            providers.remove(TileProviderType.NAVIGATIONAL_WARNING)
         }

         if (mapped[DataSource.ROUTE] == true) {
            routeTileProvider = DataSourceTileProvider(application, routesTileRepository)
            providers[TileProviderType.ROUTE] = routeTileProvider
         } else {
            providers.remove(TileProviderType.ROUTE)
         }

         value = providers
      }

      addSource(asamRepository.observeAsamMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.ASAM) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            asamTileProvider = DataSourceTileProvider(application, asamsTileRepository)
            providers[TileProviderType.ASAM] = asamTileProvider
            value = providers
         }
      }

      addSource(moduRepository.observeModuMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.MODU) == true) {
            if (mapped.value?.get(DataSource.MODU) == true) {
               val providers = value?.toMutableMap() ?: mutableMapOf()
               moduTileProvider = DataSourceTileProvider(application, modusTileRepository)
               providers[TileProviderType.MODU] = moduTileProvider
               value = providers
            }
         }
      }

      addSource(lightRepository.observeLightMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.LIGHT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            lightTileProvider = DataSourceTileProvider(application, lightsTileRepository)
            providers[TileProviderType.LIGHT] = lightTileProvider
            value = providers
         }
      }

      addSource(portRepository.observePortMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.PORT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            portTileProvider = DataSourceTileProvider(application, portsTileRepository)
            providers[TileProviderType.PORT] = portTileProvider
            value = providers
         }
      }

      addSource(beaconRepository.observeRadioBeaconMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.RADIO_BEACON) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            beaconTileProvider = DataSourceTileProvider(application, beaconTileRepository)
            providers[TileProviderType.RADIO_BEACON] = beaconTileProvider
            value = providers
         }
      }

      addSource(dgpsStationRepository.observeDgpsStationMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.DGPS_STATION) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            dgpsTileProvider = DataSourceTileProvider(application, dgpsStationsTileRepository)
            providers[TileProviderType.DGPS_STATION] = dgpsTileProvider
            value = providers
         }
      }

      addSource(navigationalWarningRepository.observeNavigationalWarningMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.NAVIGATION_WARNING) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            navigationWarningTileProvider = DataSourceTileProvider(application, navigationalWarningsTileRepository)
            providers[TileProviderType.NAVIGATIONAL_WARNING] = navigationWarningTileProvider
            value = providers
         }
      }

      addSource(routeRepository.observeRouteMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.ROUTE) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            routeTileProvider = DataSourceTileProvider(application, routesTileRepository)
            providers[TileProviderType.ROUTE] = routeTileProvider
            value = providers
         }
      }
   }
}