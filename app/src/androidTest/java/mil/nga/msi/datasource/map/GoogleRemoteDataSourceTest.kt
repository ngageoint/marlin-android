package mil.nga.msi.datasource.map

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import mil.nga.msi.geocoder.Place
import mil.nga.msi.geocoder.getFromLocationName
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException

class GoogleRemoteDataSourceTest {

   private lateinit var geocoder: Geocoder
   @Before
   fun setUp() {
      mockkStatic("mil.nga.msi.geocoder.GeocoderKt")
      geocoder = mockk<Geocoder>()
   }

   @After
   fun tearDown() {
      unmockkAll()
   }

   @Test
   fun google_search_api_should_succeed() = runTest {
      val googleRemoteDataSource = GoogleRemoteDataSource(geocoder)
      val mockPlaces = listOf(Place(name = "Test", location = LatLng(0.0, 0.0)))
      coEvery { geocoder.getFromLocationName(any()) } returns mockPlaces
      val result = googleRemoteDataSource.search("Test") as? SearchResult.Success
      Assert.assertNotNull(result)
      Assert.assertEquals(mockPlaces[0], result?.places?.get(0))
   }

   @Test
   fun google_search_api_should_fail() = runTest {
      val googleRemoteDataSource = GoogleRemoteDataSource(geocoder)
      coEvery { geocoder.getFromLocationName(any()) } throws  RuntimeException("Test Exception")
      val result = googleRemoteDataSource.search("Test") as? SearchResult.Error
      Assert.assertNotNull(result)
      Assert.assertEquals("Error searching google, please try again later.", result?.message)
   }
}