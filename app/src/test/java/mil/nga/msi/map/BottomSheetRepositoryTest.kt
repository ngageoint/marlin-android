package mil.nga.msi.map

import android.os.Looper
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.map.BottomSheetRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class BottomSheetRepositoryTest {

   private lateinit var filterRepository: FilterRepository

   private lateinit var layerRepository: LayerRepository

   private lateinit var navigationalWarningRepository: NavigationalWarningRepository

   private lateinit var userPreferencesRepository: UserPreferencesRepository

   private lateinit var bottomSheetRepository: BottomSheetRepository

   @Before
   fun setUp() {
      mockkStatic(Looper::class)
      val looper = mockk<Looper> {
         every { thread } returns Thread.currentThread()
      }
      every { Looper.getMainLooper() } returns looper

      filterRepository = mockk()
      val filterMap = mockk<Map<DataSource, List<Filter>>>()
      every { filterMap[any()] } returns null
      every { filterRepository.filters } returns flowOf(filterMap)

      layerRepository = mockk()
      every { layerRepository.observeVisibleLayers() } returns flowOf(emptyList())

      navigationalWarningRepository = mockk()
      every {
         navigationalWarningRepository.getNavigationalWarnings(
            any(),
            any(),
            any(),
            any()
         )
      } returns emptyList()

      userPreferencesRepository = mockk()
      val dataSources = mockk<Map<DataSource, Boolean>>()
      every { dataSources[any()] } returns false
      every { dataSources[DataSource.NAVIGATION_WARNING] } returns true
      every { userPreferencesRepository.mapped } returns flowOf(dataSources)

      bottomSheetRepository = BottomSheetRepository(
         annotationProvider = mockk(),
         application = mockk(),
         filterRepository = filterRepository,
         geoPackageManager = mockk(),
         layerRepository = layerRepository,
         asamRepository = mockk(),
         moduRepository = mockk(),
         lightRepository = mockk(),
         portRepository = mockk(),
         beaconRepository = mockk(),
         dgpsStationRepository = mockk(),
         navigationalWarningRepository = navigationalWarningRepository,
         userPreferencesRepository = userPreferencesRepository,
      )
   }

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_not_return_navWarnings_that_havent_been_tapped() = runTest {
      setMockNavWarning(polygonNavWarningOn180thMeridian())
      val latLng = LatLng(50.0, 170.0)
      val bounds = buildInputBounds(latLng.latitude, latLng.longitude, 2.5)

      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(0, tappedAnnotationsCount)
   }

   @Test
   fun should_return_tapped_polygons_that_dont_cross_180th_meridian() = runTest {
      setMockNavWarning(polygonNavWarning())
      val latLng = LatLng(27.0, 145.9)
      val bounds = buildInputBounds(latLng.latitude, latLng.longitude, 2.5)

      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(1, tappedAnnotationsCount)
      Assert.assertEquals(
         "3403--2023--HYDROPAC",
         bottomSheetRepository.mapAnnotations.value?.get(0)?.key?.id
      )
   }

   @Test
   fun should_return_tapped_polygons_that_cross_180th_meridian() = runTest {
      setMockNavWarning(polygonNavWarningOn180thMeridian())
      val latLng = LatLng(17.0, -179.9)
      val bounds = buildInputBounds(latLng.latitude, latLng.longitude, 2.5)

      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(1, tappedAnnotationsCount)
      Assert.assertEquals(
         "801--2023--NAVAREA_XII",
         bottomSheetRepository.mapAnnotations.value?.get(0)?.key?.id
      )
   }

   @Test
   fun should_return_tapped_lines_that_cross_180th_meridian() = runTest {
      setMockNavWarning(lineNavWarningOn180thMeridian())
      val latLng = LatLng(20.0, 179.0)
      val bounds = buildInputBounds(latLng.latitude, latLng.longitude, 2.5)

      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(1, tappedAnnotationsCount)
      Assert.assertEquals(
         "762--2023--NAVAREA_XII",
         bottomSheetRepository.mapAnnotations.value?.get(0)?.key?.id
      )
   }

   private fun setMockNavWarning(navWarning: NavigationalWarning) {
      every {
         navigationalWarningRepository.getNavigationalWarnings(
            any(),
            any(),
            any(),
            any()
         )
      } returns listOf(navWarning)
   }

   private fun buildInputBounds(lat: Double, long: Double, tolerance: Double): LatLngBounds {
      return LatLngBounds(
         LatLng(lat - tolerance, long - tolerance),
         LatLng(lat + tolerance, long + tolerance)
      )
   }

   private fun polygonNavWarningOn180thMeridian(): NavigationalWarning {
      val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)
      return NavigationalWarning(
         id = "801--2023--NAVAREA_XII",
         number = 801,
         year = 2023,
         navigationArea = NavigationArea.NAVAREA_XII,
         issueDate = dateFormat.parse("101910Z NOV 2023")!!
      ).apply {
         subregions = mutableListOf("19", "81","83")
         text =
            "NORTH PACIFIC.\nHAWAII TO MARSHALL ISLANDS.\n1. HAZARDOUS OPERATIONS 1410Z TO 1720Z DAILY\n   15 THRU 18 NOV:\n   A. ROCKET LAUNCHING IN AREA BOUND BY\n      14-26.00N 172-39.00E, 12-55.00N 169-45.00E,\n      11-44.00N 167-39.00E, 11-27.00N 167-49.00E,\n      11-52.00N 168-58.00E, 12-48.00N 171-10.00E,\n      14-29.00N 175-20.00E, 15-24.00N 177-41.00E,\n      16-20.00N 179-40.00W, 17-42.00N 175-13.00W,\n      19-35.00N 168-59.00W, 21-01.00N 164-29.00W,\n      22-32.00N 160-00.00W, 23-35.00N 156-29.00W,\n      24-00.00N 155-28.00W, 24-31.00N 155-39.00W,\n      24-14.00N 157-43.00W, 23-26.00N 161-55.00W,\n      22-44.00N 165-15.00W, 21-54.00N 167-59.00W,\n      20-57.00N 170-54.00W, 19-55.00N 173-55.00W,\n      19-03.00N 176-19.00W, 17-39.00N 179-59.00E,\n      16-13.00N 176-31.00E, 15-36.00N 175-03.00E.\n   B. SPACE DEBRIS IN AREA BOUND BY\n      24-02.64N 157-33.72W, 24-08.82N 157-02.82W,\n      23-32.16N 156-53.28W, 23-25.80N 157-25.56W.\n2. CANCEL NAVAREA XII 794/23.\n3. CANCEL THIS MSG 181820Z NOV 23.\n"
         status = "A"
         authority = "SPACEX 0/23 101821Z NOV 23."
         cancelNumber = null
         cancelDate = null
         cancelNavigationArea = null
         cancelYear = null
         geoJson = """
            {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[172.65,14.433333333333334],[169.75,12.916666666666666],[167.65,11.733333333333333],[167.81666666666666,11.45],[168.96666666666667,11.866666666666667],[171.16666666666666,12.8],[175.33333333333334,14.483333333333333],[177.68333333333334,15.4],[-179.66666666666666,16.333333333333332],[-175.21666666666667,17.7],[-168.98333333333332,19.583333333333332],[-164.48333333333332,21.016666666666666],[-160.0,22.533333333333335],[-156.48333333333332,23.583333333333332],[-155.46666666666667,24.0],[-155.65,24.516666666666666],[-157.71666666666667,24.233333333333334],[-161.91666666666666,23.433333333333334],[-165.25,22.733333333333334],[-167.98333333333332,21.9],[-170.9,20.95],[-173.91666666666666,19.916666666666668],[-176.31666666666666,19.05],[179.98333333333332,17.65],[176.51666666666668,16.216666666666665],[175.05,15.6]]]},"properties":{}},{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[-157.562,24.044],[-157.047,24.147],[-156.888,23.536],[-157.426,23.43]]]},"properties":{}}]}
         """.trimIndent()
      }
   }

   private fun polygonNavWarning(): NavigationalWarning {
      val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)
      return NavigationalWarning(
         id = "3403--2023--HYDROPAC",
         number = 3403,
         year = 2023,
         navigationArea = NavigationArea.HYDROPAC,
         issueDate = dateFormat.parse("271213Z OCT 2023")!!
      ).apply {
         subregions = mutableListOf("97")
         text =
            "WESTERN NORTH PACIFIC.\nDNC 12.\n1. GUNNERY EXERCISES 2300Z TO 0900Z DAILY\n   31 OCT THRU 29 NOV IN AREA BOUND BY\n   28-15.25N 146-29.78E, 25-25.27N 147-37.78E,\n   25-00.26N 145-35.80E, 27-55.25N 144-57.80E.\n2. CANCEL THIS MSG 301000Z NOV 23.\n"
         status = "A"
         authority = "NAVAREA XI 367/23 271202Z OCT 23."
         cancelNumber = null
         cancelDate = null
         cancelNavigationArea = null
         cancelYear = null
         geoJson = """
            {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[146.49633333333333,28.254166666666666],[147.62966666666668,25.421166666666668],[145.59666666666666,25.004333333333335],[144.96333333333334,27.920833333333334]]]},"properties":{}}]}
         """.trimIndent()
      }
   }

   private fun lineNavWarningOn180thMeridian(): NavigationalWarning {
      val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)
      return NavigationalWarning(
         id = "762--2023--NAVAREA_XII",
         number = 762,
         year = 2023,
         navigationArea = NavigationArea.NAVAREA_XII,
         issueDate = dateFormat.parse("301324Z OCT 2023")!!
      ).apply {
         subregions = mutableListOf("19")
         text =
            "NORTH PACIFIC.\n1. CABLE OPERATIONS 01 THRU 23 NOV\n   BY M/V ANIEK IN VICINITY OF TRACKLINE JOINING\n   20-24.00N 175-07.00E, 20-25.00N 176-22.00E,\n   20-13.00N 177-02.00E, 20-15.00N 178-25.00E,\n   20-35.00N 178-47.00E, 20-56.00N 179-46.00E,\n   20-58.00N 179-09.00W.\n   WIDE BERTH REQUESTED.\n2. CANCEL THIS MSG 240001Z NOV 23.\n"
         status = "A"
         authority = "NAVAREA XI 374/23 301232Z OCT 23.."
         cancelNumber = null
         cancelDate = null
         cancelNavigationArea = null
         cancelYear = null
         geoJson = """
            {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"LineString","coordinates":[[175.11666666666667,20.4],[176.36666666666667,20.416666666666668],[177.03333333333333,20.216666666666665],[178.41666666666666,20.25],[178.78333333333333,20.583333333333332],[179.76666666666668,20.933333333333334],[-179.15,20.966666666666665]]},"properties":{}}]}
         """.trimIndent()
      }
   }
}