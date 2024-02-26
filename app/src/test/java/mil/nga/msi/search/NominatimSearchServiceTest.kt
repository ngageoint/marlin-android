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
                 "place_id": 294586699,
                 "licence": "Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright",
                 "osm_type": "relation",
                 "osm_id": 5396194,
                 "boundingbox": [
                     "38.7916303",
                     "38.995968",
                     "-77.1197949",
                     "-76.909366"
                 ],
                 "lat": "38.8950368",
                 "lon": "-77.0365427",
                 "display_name": "Washington, District of Columbia, United States",
                 "class": "boundary",
                 "type": "administrative",
                 "importance": 0.8492888986115819,
                 "icon": "/poi_boundary_administrative.p.20.png"
             },
             {
                 "place_id": 293979722,
                 "licence": "Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright",
                 "osm_type": "relation",
                 "osm_id": 165479,
                 "boundingbox": [
                     "45.5437226",
                     "49.0024392",
                     "-124.8360916",
                     "-116.9159938"
                 ],
                 "lat": "invalid lat test",
                 "lon": "invalid lon test",
                 "display_name": "Washington, United States",
                 "class": "boundary",
                 "type": "administrative",
                 "importance": 0.8129297923155674,
                 "icon": "/poi_boundary_administrative.p.20.png"
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