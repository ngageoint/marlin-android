package mil.nga.msi.datasource.map

import android.location.Geocoder
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import mil.nga.msi.network.nominatim.NominatimSearchService
import mil.nga.msi.ui.map.search.NominatimResultItem
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.lang.RuntimeException

class NominatimRemoteDataSourceTest {

   private lateinit var geocoder: Geocoder
   private lateinit var nominatimService: NominatimSearchService
   @Before
   fun setUp() {
      mockkStatic("mil.nga.msi.geocoder.GeocoderKt")
      geocoder = mockk<Geocoder>()
      nominatimService = mockk<NominatimSearchService>()
   }

   @After
   fun tearDown() {
      unmockkAll()
   }

   @Test
   fun nominatim_search_api_should_succeed() = runTest {
      val mockPlaces = listOf(
         NominatimResultItem(
            displayName = "Test",
            lat = "10.0",
            lon = "20.0"
         )
      )

      coEvery { nominatimService.search(any()) } returns Response.success(mockPlaces)
      val nominatimRemoteDataSource = NominatimRemoteDataSource(nominatimService)
      val result = nominatimRemoteDataSource.search("Test") as? SearchResult.Success
      Assert.assertNotNull(result)
      val place = result?.places?.get(0)
      Assert.assertNotNull(place)
      Assert.assertEquals("Test", place?.name)
      Assert.assertEquals(20.0, place?.location?.longitude ?: Double.MAX_VALUE, 0.00001)
      Assert.assertEquals(10.0, place?.location?.latitude ?: Double.MAX_VALUE, 0.00001)
   }

   @Test
   fun nominatim_search_api_should_fail() = runTest {
      val nominatimRemoteDataSource = NominatimRemoteDataSource(nominatimService)
      coEvery { nominatimService.search(any()) } throws  RuntimeException("Test Exception")
      val result = nominatimRemoteDataSource.search("Test") as? SearchResult.Error
      Assert.assertNotNull(result)
      Assert.assertEquals("Error searching nominatim, please try again later.", result?.message)
   }
}