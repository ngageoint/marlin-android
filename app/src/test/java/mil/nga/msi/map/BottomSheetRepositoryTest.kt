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
      every { dataSources[any()] } returns true
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
   fun should_return_tapped_navwarnings_that_cross_180th_meridian() = runTest {
      every {
         navigationalWarningRepository.getNavigationalWarnings(
            any(),
            any(),
            any(),
            any()
         )
      } returns listOf(polygonNavWarningOn180thMeridian())

      val dataSources = mockk<Map<DataSource, Boolean>>()
      every { dataSources[any()] } returns false
      every { dataSources[DataSource.NAVIGATION_WARNING] } returns true
      every { userPreferencesRepository.mapped } returns flowOf(dataSources)

      val latLng = LatLng(40.0, 170.0)
      val bounds = LatLngBounds(
         LatLng(latLng.latitude - 1, latLng.longitude - 1),
         LatLng(latLng.latitude + 1, latLng.longitude + 1)
      )

      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(1, tappedAnnotationsCount)
      Assert.assertEquals(
         "372--2023--NAVAREA_XII",
         bottomSheetRepository.mapAnnotations.value?.get(0)?.key?.id
      )
   }

   @Test
   fun should_not_return_navWarnings_that_havent_been_tapped() = runTest {
      every {
         navigationalWarningRepository.getNavigationalWarnings(
            any(),
            any(),
            any(),
            any()
         )
      } returns listOf(polygonNavWarningOn180thMeridian())

      val dataSources = mockk<Map<DataSource, Boolean>>()
      every { dataSources[any()] } returns false
      every { dataSources[DataSource.NAVIGATION_WARNING] } returns true
      every { userPreferencesRepository.mapped } returns flowOf(dataSources)

      val latLng = LatLng(50.0, 170.0)
      val bounds = LatLngBounds(
         LatLng(latLng.latitude - 1, latLng.longitude - 1),
         LatLng(latLng.latitude + 1, latLng.longitude + 1)
      )
      val tappedAnnotationsCount = bottomSheetRepository.setLocation(latLng, bounds)
      Assert.assertEquals(0, tappedAnnotationsCount)
   }

   private fun polygonNavWarningOn180thMeridian(): NavigationalWarning {
      val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)
      return NavigationalWarning(
         id = "372--2023--NAVAREA_XII",
         number = 372,
         year = 2023,
         navigationArea = NavigationArea.NAVAREA_XII,
         issueDate = dateFormat.parse("210124Z JUN 2023")!!
      ).apply {
         subregions = mutableListOf("19", "97")
         text =
            "NORTH PACIFIC.\n1. HAZARDOUS OPERATIONS, SPACE DEBRIS\n 0554Z TO 0912Z DAILY 22 THRU 28 JUN\n IN AREA BOUND BY\n 34-54.00N 152-00.00W, 36-53.00N 151-00.00W,\n 44-04.00N 165-00.00E, 42-05.00N 165-00.00E.\n2. CANCEL THIS MSG 281012Z JUN 23.\n"
         status = "A"
         authority = "SPACEX 0/23 210000Z JUN 23."
         cancelNumber = null
         cancelDate = null
         cancelNavigationArea = null
         cancelYear = null
         geoJson = """
            {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[-152.0,34.9],[-151.0,36.88333333333333],[165.0,44.06666666666667],[165.0,42.083333333333336]]]},"properties":{}}]}
         """.trimIndent()
      }
   }
}