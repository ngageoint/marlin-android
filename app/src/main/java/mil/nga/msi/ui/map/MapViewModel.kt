package mil.nga.msi.ui.map

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.geopackage.map.tiles.overlay.XYZGeoPackageOverlay
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.repository.DataSourceRepository
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.geocoder.GeocoderRemoteDataSource
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.map.*
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.repository.preferences.SharedPreferencesRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.overlay.*
import mil.nga.sf.GeometryEnvelope
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
   NAVIGATIONAL_WARNING
}

@HiltViewModel
class MapViewModel @Inject constructor(
   private val application: Application,
   private val layerService: LayerService,
   private val filterRepository: FilterRepository,
   private val layerRepository: LayerRepository,
   private val geoPackageManager: GeoPackageManager,
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
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val navigationalWarningTileRepository: NavigationalWarningTileRepository,
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

   private val _zoom = MutableLiveData<Int>()

   suspend fun setMapLocation(mapLocation: MapLocation, zoom: Int) {
      _zoom.value = zoom
      mapRepository.setMapLocation(mapLocation)
   }

   private var asamTileProvider = AsamTileProvider(application, asamTileRepository)
   private var moduTileProvider = ModuTileProvider(application, moduTileRepository)
   private var portTileProvider = PortTileProvider(application, portTileRepository)
   private var beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
   private var lightTileProvider = LightTileProvider(application, lightTileRepository)
   private var dgpsTileProvider = DgpsStationTileProvider(application, dgpsStationTileRepository)
   private var navigationWarningTileProvider = NavigationalWarningTileProvider(application, navigationalWarningTileRepository)

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

         if (mapped[DataSource.NAVIGATION_WARNING] == true) {
            navigationWarningTileProvider = NavigationalWarningTileProvider(application, navigationalWarningTileRepository)
            providers[TileProviderType.NAVIGATIONAL_WARNING] = navigationWarningTileProvider
         } else {
            providers.remove(TileProviderType.NAVIGATIONAL_WARNING)
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

      addSource(moduRepository.observeModuMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.MODU) == true) {
            if (mapped.value?.get(DataSource.MODU) == true) {
               val providers = value?.toMutableMap() ?: mutableMapOf()
               moduTileProvider = ModuTileProvider(application, moduTileRepository)
               providers[TileProviderType.MODU] = moduTileProvider
               value = providers
            }
         }
      }

      addSource(lightRepository.observeLightMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.LIGHT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            lightTileProvider = LightTileProvider(application, lightTileRepository)
            providers[TileProviderType.LIGHT] = lightTileProvider
            value = providers
         }
      }

      addSource(portRepository.observePortMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.PORT) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            portTileProvider = PortTileProvider(application, portTileRepository)
            providers[TileProviderType.PORT] = portTileProvider
            value = providers
         }
      }

      addSource(beaconRepository.observeRadioBeaconMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.RADIO_BEACON) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            beaconTileProvider = RadioBeaconTileProvider(application, beaconTileRepository)
            providers[TileProviderType.RADIO_BEACON] = beaconTileProvider
            value = providers
         }
      }

      addSource(dgpsStationRepository.observeDgpsStationMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.DGPS_STATION) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            dgpsTileProvider = DgpsStationTileProvider(application, dgpsStationTileRepository)
            providers[TileProviderType.DGPS_STATION] = dgpsTileProvider
            value = providers
         }
      }

      addSource(navigationalWarningRepository.observeNavigationalWarningMapItems().distinctUntilChanged().asLiveData()) {
         if (mapped.value?.get(DataSource.NAVIGATION_WARNING) == true) {
            val providers = value?.toMutableMap() ?: mutableMapOf()
            navigationWarningTileProvider = NavigationalWarningTileProvider(application, navigationalWarningTileRepository)
            providers[TileProviderType.NAVIGATIONAL_WARNING] = navigationWarningTileProvider
            value = providers
         }
      }
   }

   suspend fun getMapAnnotations(
      minLongitude: Double,
      maxLongitude: Double,
      minLatitude: Double,
      maxLatitude: Double,
      point: LatLng
   ) = withContext(Dispatchers.IO) {
      val dataSources = mapped.value ?: emptyMap()
      val boundsFilters = MapBoundsFilter.filtersForBounds(
         minLongitude = minLongitude,
         maxLongitude = maxLongitude,
         minLatitude = minLatitude,
         maxLatitude = maxLatitude
      )

      val asams = if (dataSources[DataSource.ASAM] == true) {
         val entry = filterRepository.filters.first()
         val asamFilters = entry[DataSource.ASAM] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply { addAll(asamFilters) }
         asamRepository
            .getAsams(filters)
            .map { asam ->
               val key = MapAnnotation.Key(asam.reference, MapAnnotation.Type.ASAM)
               MapAnnotation(key, asam.latitude, asam.longitude)
            }
      } else emptyList()

      val modus = if (dataSources[DataSource.MODU] == true) {
         val entry = filterRepository.filters.first()
         val moduFilters = entry[DataSource.MODU] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply { addAll(moduFilters) }
         moduRepository
            .getModus(filters)
            .map { modu ->
               val key = MapAnnotation.Key(modu.name, MapAnnotation.Type.MODU)
               MapAnnotation(key, modu.latitude, modu.longitude)
            }
      } else emptyList()

      val lights = if (dataSources[DataSource.LIGHT] == true) {
         val entry = filterRepository.filters.first()
         val lightFilters = entry[DataSource.LIGHT] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply {
            addAll(lightFilters)
            add(
              Filter(
                 parameter = FilterParameter(
                    type = FilterParameterType.INT,
                    title = "Characteristic Number",
                    parameter = "characteristic_number",
                 ),
                 comparator = ComparatorType.EQUALS,
                 value = 1
              )
            )
         }

         lightRepository
            .getLights(filters)
            .map { light ->
               val key = MapAnnotation.Key(LightKey.fromLight(light).id(), MapAnnotation.Type.LIGHT)
               MapAnnotation(key, light.latitude, light.longitude)
            }
      } else emptyList()

      val ports = if (dataSources[DataSource.PORT] == true) {
         val entry = filterRepository.filters.first()
         val portFilters = entry[DataSource.PORT] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply { addAll(portFilters) }
         portRepository
            .getPorts(filters)
            .map { port ->
               val key = MapAnnotation.Key(port.portNumber.toString(), MapAnnotation.Type.PORT)
               MapAnnotation(key, port.latitude, port.longitude)
            }
      } else emptyList()

      val beacons = if (dataSources[DataSource.RADIO_BEACON] == true) {
         val entry = filterRepository.filters.first()
         val beaconsFilters = entry[DataSource.RADIO_BEACON] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply { addAll(beaconsFilters) }
         beaconRepository
            .getRadioBeacons(filters)
            .map { beacon ->
               val key = MapAnnotation.Key(RadioBeaconKey.fromRadioBeacon(beacon).id(), MapAnnotation.Type.RADIO_BEACON)
               MapAnnotation(key, beacon.latitude, beacon.longitude)
            }
      } else emptyList()

      val dgps = if (dataSources[DataSource.DGPS_STATION] == true) {
         val entry = filterRepository.filters.first()
         val dgpsFilters = entry[DataSource.DGPS_STATION] ?: emptyList()
         val filters = boundsFilters.toMutableList().apply { addAll(dgpsFilters) }
         dgpsStationRepository
            .getDgpsStations(filters)
            .map { dgps ->
               val key = MapAnnotation.Key(DgpsStationKey.fromDgpsStation(dgps).id(), MapAnnotation.Type.DGPS_STATION)
               MapAnnotation(key, dgps.latitude, dgps.longitude)
            }
      } else emptyList()

      val navigationalWarnings = if (dataSources[DataSource.NAVIGATION_WARNING] == true) {
         val geometryEnvelope = GeometryEnvelope(minLongitude, minLatitude, maxLongitude, maxLatitude)
         navigationalWarningRepository
            .getNavigationalWarnings(
               minLatitude = minLatitude,
               minLongitude = minLongitude,
               maxLatitude = maxLatitude,
               maxLongitude = maxLongitude
            )
            .flatMap { warning ->
               warning.getFeatures().filter { feature ->
                  geometryEnvelope.contains(feature.geometry.geometry.envelope) ||
                  geometryEnvelope.intersects(feature.geometry.geometry.envelope)
               }.map { feature ->
                  val key = MapAnnotation.Key(NavigationalWarningKey.fromNavigationWarning(warning).id(), MapAnnotation.Type.NAVIGATIONAL_WARNING)
                  val center = feature.geometry.geometry.centroid
                  MapAnnotation(key, center.y, center.x)
               }
            }
      } else emptyList()

      val boundingBox = BoundingBox(
         minLongitude,
         minLatitude,
         maxLongitude,
         maxLatitude
      )
      val features = layerRepository.observeVisibleLayers()
         .first()
         .filter { it.type == LayerType.GEOPACKAGE }
         .flatMap { layer ->
         try {
            val geoPackage = geoPackageManager.openExternal(layer.filePath)
            val annotations = mutableListOf<MapAnnotation>()

            layer.url.split(",").filter { it.isNotEmpty() }.forEach { table ->
               if (geoPackage.featureTables.contains(table)) {
                  val featureDao = geoPackage.getFeatureDao(table)
                  val indexer = FeatureIndexManager(application, geoPackage, featureDao)
                  indexer.query(boundingBox).forEach { result ->
                     val key = MapAnnotation.Key(GeoPackageFeatureKey(layer.id, table, result.id).id(), MapAnnotation.Type.GEOPACKAGE)
                     val annotation = MapAnnotation(key, point.latitude, point.longitude)
                     annotations.add(annotation)
                  }
               }
            }

            annotations
         } catch (_: Exception) { emptyList() }
      }

     asams + modus + lights + ports + beacons + dgps + navigationalWarnings + features
   }
}