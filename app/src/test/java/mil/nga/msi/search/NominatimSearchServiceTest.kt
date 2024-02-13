package mil.nga.msi.search

import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import mil.nga.msi.network.nominatim.NominatimSearchService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NominatimSearchServiceTest {

   private lateinit var server: MockWebServer
   private lateinit var nominatimSearchService: NominatimSearchService

   @Before
   fun setUp() {
      server = MockWebServer()
      nominatimSearchService = Retrofit.Builder()
         .baseUrl(server.url("/"))
         .addConverterFactory(GsonConverterFactory.create())
         .build().create(NominatimSearchService::class.java)
   }

   @After
   fun tearDown() {
      server.shutdown()
      unmockkAll()
   }

   @Test
   fun should_call_the_nominatim_api() = runTest {
      val mockResponse = """
         [
             {
                 "place_id": 4238259,
                 "licence": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
                 "osm_type": "relation",
                 "osm_id": 5396194,
                 "lat": "38.8950368",
                 "lon": "-77.0365427",
                 "class": "boundary",
                 "type": "administrative",
                 "place_rank": 12,
                 "importance": 0.7492888986115819,
                 "addresstype": "city",
                 "name": "Washington",
                 "display_name": "Washington, District of Columbia, United States",
                 "boundingbox": [
                     "38.7916303",
                     "38.9959680",
                     "-77.1197949",
                     "-76.9093660"
                 ]
             },
             {
                 "place_id": 313430381,
                 "licence": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
                 "osm_type": "relation",
                 "osm_id": 165479,
                 "lat": "38.3662806",
                 "lon": "89.4201902",
                 "class": "boundary",
                 "type": "administrative",
                 "place_rank": 8,
                 "importance": 0.7129297923155674,
                 "addresstype": "state",
                 "name": "Washington",
                 "display_name": "Washington, United States",
                 "boundingbox": [
                     "45.5437314",
                     "49.0024392",
                     "-124.8360916",
                     "-116.9159938"
                 ]
             }
         ]
      """.trimIndent()
      val res = MockResponse()
      res.setBody(mockResponse)
      server.enqueue(res)
      val data = nominatimSearchService.search("test")
      val request = server.takeRequest()

      Assert.assertEquals("GET /search?q=test&format=json HTTP/1.1", request.requestLine)
      Assert.assertEquals("marlin-android", request.headers["User-Agent"])
      Assert.assertEquals(2, data.body()?.count())
      Assert.assertEquals("Washington, District of Columbia, United States", data.body()?.get(0)?.displayName)
   }
}