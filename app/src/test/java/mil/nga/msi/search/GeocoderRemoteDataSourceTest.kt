package mil.nga.msi.search

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import mil.nga.msi.geocoder.getFromLocationName
import mil.nga.msi.repository.geocoder.GeocoderRemoteDataSource
import mil.nga.msi.repository.geocoder.GeocoderState
import mil.nga.msi.ui.map.search.NominatimSearchProvider
import mil.nga.msi.ui.map.search.SearchType
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GeocoderRemoteDataSourceTest {

   private lateinit var mockGeocoder: Geocoder
   private lateinit var geocoderRemoteDataSource: GeocoderRemoteDataSource
   private lateinit var mockNominatimSearchProvider: NominatimSearchProvider

   @Before
   fun setUp() {
      mockkStatic("mil.nga.msi.geocoder.GeocoderKt")
      mockNominatimSearchProvider = mockk<NominatimSearchProvider>()
      mockGeocoder = mockk<Geocoder>()
      geocoderRemoteDataSource = GeocoderRemoteDataSource(mockGeocoder, mockNominatimSearchProvider)
   }

   @After
   fun tearDown() {
      unmockkAll()
   }

   @Test
   fun should_search_google_maps_api() = runTest {
      geocoderRemoteDataSource.geocode("Test", SearchType.NATIVE)
      verify { mockGeocoder.getFromLocationName("Test", any<(List<GeocoderState>) -> Unit>()) }
   }

   @Test
   fun should_search_nominatim_api() = runTest {
      // mock response from search provider
      coEvery { mockNominatimSearchProvider.search("Test") } answers {
         listOf(
            GeocoderState(
               name = "Test Location",
               location = LatLng(5.0, 5.0),
               address = "123 Test Street"
            )
         )
      }

      val result = geocoderRemoteDataSource.geocode("Test", SearchType.NOMINATIM)

      // assert google maps API wasn't called
      verify(exactly = 0) {
         mockGeocoder.getFromLocationName(
            any(),
            any<(List<GeocoderState>) -> Unit>()
         )
      }

      Assert.assertEquals(1, result.count())
      Assert.assertEquals("Test Location", result[0].name)
      Assert.assertEquals("123 Test Street", result[0].address)
      Assert.assertEquals(LatLng(5.0, 5.0), result[0].location)
   }
}