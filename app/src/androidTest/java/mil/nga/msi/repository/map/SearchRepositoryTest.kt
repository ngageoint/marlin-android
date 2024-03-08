package mil.nga.msi.repository.map

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.map.GoogleRemoteDataSource
import mil.nga.msi.datasource.map.NominatimRemoteDataSource
import mil.nga.msi.datasource.map.SearchResult
import mil.nga.msi.repository.geocoder.SearchRepository
import mil.nga.msi.ui.map.search.SearchProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchRepositoryTest {

   private lateinit var googleDataSource: GoogleRemoteDataSource
   private lateinit var nominatimDataSource: NominatimRemoteDataSource
   private lateinit var searchProvider: SearchProvider
   @Before
   fun setUp() {
      googleDataSource = mockk<GoogleRemoteDataSource>()
      nominatimDataSource = mockk<NominatimRemoteDataSource>()
   }

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_search_dms() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      val result = searchRepository.search(
         text = "01 01 01N, 001 01 01W",
         searchProvider = SearchProvider.GOOGLE
      ) as? SearchResult.Success

      Assert.assertNotNull(result)
      val place = result?.places?.get(0)
      Assert.assertNotNull(place)
      Assert.assertEquals("1° 01' 01\" N, 1° 01' 01\" W", place?.name)
      Assert.assertEquals(-1.01, place?.location?.longitude ?: Double.MAX_VALUE, 0.01)
      Assert.assertEquals(1.01, place?.location?.latitude ?: Double.MAX_VALUE, 0.01)
   }

   @Test
   fun should_search_mgrs() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      val result = searchRepository.search(
         text = "4QFJ1234567890",
         searchProvider = SearchProvider.GOOGLE
      ) as? SearchResult.Success

      Assert.assertNotNull(result)
      val place = result?.places?.get(0)
      Assert.assertNotNull(place)
      Assert.assertEquals("4QFJ1234567890", place?.name)
      Assert.assertEquals(0.0, place?.location?.longitude ?: Double.MAX_VALUE, 0.01)
      Assert.assertEquals(90.0, place?.location?.latitude ?: Double.MAX_VALUE, 0.01)
   }

   @Test
   fun should_search_gars() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      val result = searchRepository.search(
         text = "151LV38",
         searchProvider = SearchProvider.GOOGLE
      ) as? SearchResult.Success

      Assert.assertNotNull(result)
      val place = result?.places?.get(0)
      Assert.assertNotNull(place)
      Assert.assertEquals("151LV38", place?.name)
      Assert.assertEquals(9.0, place?.location?.longitude ?: Double.MAX_VALUE, 0.01)
      Assert.assertEquals(18.0, place?.location?.latitude ?: Double.MAX_VALUE, 0.01)
   }

   @Test
   fun should_search_latlng() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      val result = searchRepository.search(
         text = "10.0, 20.0",
         searchProvider = SearchProvider.GOOGLE
      ) as? SearchResult.Success

      Assert.assertNotNull(result)
      val place = result?.places?.get(0)
      Assert.assertNotNull(place)
      Assert.assertEquals("10.0, 20.0", place?.name)
      Assert.assertEquals(20.0, place?.location?.longitude ?: Double.MAX_VALUE, 0.01)
      Assert.assertEquals(10.0, place?.location?.latitude ?: Double.MAX_VALUE, 0.01)
   }

   @Test
   fun should_search_google_geocoder() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      coEvery { googleDataSource.search(any()) } returns SearchResult.Success(listOf())

      searchRepository.search(
         text = "Placename",
         searchProvider = SearchProvider.GOOGLE
      ) as? SearchResult.Success

      coVerify(exactly = 1) { googleDataSource.search(any()) }
   }

   @Test
   fun should_search_nominatim_geocoder() = runTest {
      val searchRepository = SearchRepository(
         googleDataSource = googleDataSource,
         nominatimDataSource = nominatimDataSource
      )

      coEvery { nominatimDataSource.search(any()) } returns SearchResult.Success(listOf())

      searchRepository.search(
         text = "Placename",
         searchProvider = SearchProvider.NOMINATIM
      ) as? SearchResult.Success

      coVerify(exactly = 1) { nominatimDataSource.search(any()) }
   }
}