package mil.nga.msi.repository.map

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.msi.buildEnvelopesSpanning180thMeridian
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.getPointsForGeometry
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.repository.route.RouteRepository
import mil.nga.msi.ui.map.AnnotationProvider
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.GeometryEnvelope
import mil.nga.sf.Point
import mil.nga.sf.geojson.Feature
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Singleton
class BottomSheetRepository @Inject constructor(
   val annotationProvider: AnnotationProvider,
   private val application: Application,
   private val filterRepository: FilterRepository,
   private val geoPackageManager: GeoPackageManager,
   private val layerRepository: LayerRepository,
   private val asamRepository: AsamRepository,
   private val moduRepository: ModuRepository,
   private val lightRepository: LightRepository,
   private val portRepository: PortRepository,
   private val beaconRepository: RadioBeaconRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val routeRepository: RouteRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   private val _mapAnnotations = MutableLiveData<List<MapAnnotation>>()
   val mapAnnotations: LiveData<List<MapAnnotation>> = _mapAnnotations

   suspend fun setLocation(point: LatLng, bounds: LatLngBounds): Int {
      val annotations = getMapAnnotations(
         point = point,
         bounds = bounds
      )

      _mapAnnotations.value = annotations
      return annotations.size
   }

   fun clearLocation() {
      _mapAnnotations.value = emptyList()
   }

   private suspend fun getMapAnnotations(
      point: LatLng,
      bounds: LatLngBounds
   ) = withContext(Dispatchers.IO) {
      val dataSources = userPreferencesRepository.mapped.first()

      val boundsFilters = MapBoundsFilter.filtersForBounds(
         minLongitude = bounds.southwest.longitude,
         maxLongitude = bounds.northeast.longitude,
         minLatitude = bounds.southwest.latitude,
         maxLatitude = bounds.northeast.latitude
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
         val inputEnvelopes = if (bounds.southwest.longitude > bounds.northeast.longitude) {
            buildEnvelopesSpanning180thMeridian(
               bounds.southwest.longitude,
               bounds.southwest.latitude,
               bounds.northeast.longitude,
               bounds.northeast.latitude
            )
         } else {
            listOf(
               GeometryEnvelope(
                  bounds.southwest.longitude,
                  bounds.southwest.latitude,
                  bounds.northeast.longitude,
                  bounds.northeast.latitude
               )
            )
         }
         navigationalWarningRepository
            .getNavigationalWarnings(
               minLatitude = bounds.southwest.latitude,
               minLongitude = bounds.southwest.longitude,
               maxLatitude = bounds.northeast.latitude,
               maxLongitude = bounds.northeast.longitude
            )
            .flatMap { warning ->
               warning.getFeatures().filter { feature: Feature ->
                  val points = getPointsForGeometry(feature.geometry.geometry)
                  var featureCrosses180thMeridian = false
                  var leftLong = 180.0
                  var rightLong = -180.0

                  // check if the feature crosses the 180th meridian and track the left/right bounds for that case
                  // this assumes a nav warning won't cross both the prime and 180th meridians
                  for (i in 0..<points.count()) {
                     val currentLong = points[i].x
                     when {
                        currentLong == 0.0 -> break
                        currentLong > 0.0 -> leftLong = min(leftLong, currentLong)
                        currentLong < 0.0 -> rightLong = max(rightLong, currentLong)
                     }
                     if (i > 0 && abs(currentLong - points[i - 1].x) > 180) {
                        featureCrosses180thMeridian = true
                     }
                  }

                  val featureEnvelopes = if (featureCrosses180thMeridian) {
                     buildEnvelopesSpanning180thMeridian(
                        leftLong,
                        feature.geometry.geometry.envelope.minY,
                        rightLong,
                        feature.geometry.geometry.envelope.maxY
                     )
                  } else {
                     listOf(feature.geometry.geometry.envelope)
                  }

                  inputEnvelopes.any { inputEnvelope ->
                     featureEnvelopes.any { featureEnvelope ->
                        inputEnvelope.intersects(featureEnvelope)
                              || inputEnvelope.contains(featureEnvelope)
                     }
                  }
               }.map { feature ->
                  val key = MapAnnotation.Key(
                     NavigationalWarningKey.fromNavigationWarning(warning).id(),
                     MapAnnotation.Type.NAVIGATIONAL_WARNING
                  )
                  val envelope = feature.geometry.geometry.envelope
                  val centroid = envelope.centroid

                  // shift center longitude 180 degrees if the shape crosses the 180th meridian
                  val center = if (abs(envelope.maxX - envelope.minX) > 180) {
                     val antipodalX = if (centroid.x > 0) centroid.x - 180 else centroid.x + 180
                     Point(antipodalX, centroid.y)
                  } else {
                     centroid
                  }

                  MapAnnotation(key, center.y, center.x)
               }
            }
      } else emptyList()

      val routes = if (dataSources[DataSource.ROUTE] == true) {
         val inputEnvelopes = if (bounds.southwest.longitude > bounds.northeast.longitude) {
            buildEnvelopesSpanning180thMeridian(
               bounds.southwest.longitude,
               bounds.southwest.latitude,
               bounds.northeast.longitude,
               bounds.northeast.latitude
            )
         } else {
            listOf(
               GeometryEnvelope(
                  bounds.southwest.longitude,
                  bounds.southwest.latitude,
                  bounds.northeast.longitude,
                  bounds.northeast.latitude
               )
            )
         }
         routeRepository
            .getRoutes(
               minLatitude = bounds.southwest.latitude,
               minLongitude = bounds.southwest.longitude,
               maxLatitude = bounds.northeast.latitude,
               maxLongitude = bounds.northeast.longitude
            )
            .flatMap { route ->
               route.getFeatures().filter { feature: Feature ->
                  val points = getPointsForGeometry(feature.geometry.geometry)
                  var featureCrosses180thMeridian = false
                  var leftLong = 180.0
                  var rightLong = -180.0

                  // check if the feature crosses the 180th meridian and track the left/right bounds for that case
                  // this assumes a route won't cross both the prime and 180th meridians
                  for (i in 0..<points.count()) {
                     val currentLong = points[i].x
                     when {
                        currentLong == 0.0 -> break
                        currentLong > 0.0 -> leftLong = min(leftLong, currentLong)
                        currentLong < 0.0 -> rightLong = max(rightLong, currentLong)
                     }
                     if (i > 0 && abs(currentLong - points[i - 1].x) > 180) {
                        featureCrosses180thMeridian = true
                     }
                  }

                  val featureEnvelopes = if (featureCrosses180thMeridian) {
                     buildEnvelopesSpanning180thMeridian(
                        leftLong,
                        feature.geometry.geometry.envelope.minY,
                        rightLong,
                        feature.geometry.geometry.envelope.maxY
                     )
                  } else {
                     listOf(feature.geometry.geometry.envelope)
                  }

                  inputEnvelopes.any { inputEnvelope ->
                     featureEnvelopes.any { featureEnvelope ->
                        inputEnvelope.intersects(featureEnvelope)
                                || inputEnvelope.contains(featureEnvelope)
                     }
                  }
               }.map { feature ->
                  val key = MapAnnotation.Key(route.id.toString(), MapAnnotation.Type.ROUTE)
                  val envelope = feature.geometry.geometry.envelope
                  val centroid = envelope.centroid

                  // shift center longitude 180 degrees if the shape crosses the 180th meridian
                  val center = if (abs(envelope.maxX - envelope.minX) > 180) {
                     val antipodalX = if (centroid.x > 0) centroid.x - 180 else centroid.x + 180
                     Point(antipodalX, centroid.y)
                  } else {
                     centroid
                  }

                  MapAnnotation(key, center.y, center.x)
               }
            }
      } else emptyList()

      val boundingBox = BoundingBox(
         bounds.southwest.latitude,
         bounds.southwest.longitude,
         bounds.northeast.latitude,
         bounds.northeast.longitude
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

      asams + modus + lights + ports + beacons + dgps + navigationalWarnings + features + routes
   }
}