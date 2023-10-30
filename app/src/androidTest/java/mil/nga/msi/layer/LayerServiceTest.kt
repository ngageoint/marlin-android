package mil.nga.msi.layer

//import io.mockk.MockK
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import mil.nga.msi.network.layer.LayerService
//import mil.nga.msi.network.layer.wms.WMSCapabilities
//import mil.nga.msi.repository.layer.LayerRemoteDataSource
//import mil.nga.msi.repository.preferences.Credentials
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.ResponseBody.Companion.toResponseBody
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito
//import org.mockito.MockitoAnnotations
//import org.mockito.kotlin.doReturn
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import retrofit2.Response
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class LayerServiceTest {
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//    }
//
//    @Test
//    fun testGetTile() = runTest {
//        val mockResponse = "".toResponseBody(
//            contentType = "image/png".toMediaTypeOrNull()
//        )
//
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getTile(
//                    url = "https://test.com/tiles/1/1/1.png"
//                )
//            } doReturn Response.success(mockResponse)
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        val tile = repository.getTile(url = "https://test.com/tiles")
//        assertEquals(true, tile)
//
//        verify(mockService).getTile(
//            url = "https://test.com/tiles/1/1/1.png",
//            credentials = null
//        )
//    }
//
//    @Test
//    fun testGetTileWithCredentials() = runTest {
//        val credentials = Credentials(username = "test", password = "test")
//        val basicCredentials = okhttp3.Credentials.basic(
//            credentials.username,
//            credentials.password
//        )
//
//        val mockResponse = "".toResponseBody(
//            contentType = "image/png".toMediaTypeOrNull()
//        )
//
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getTile(
//                    url = "https://test.com/tiles/1/1/1.png",
//                    credentials = basicCredentials
//                )
//            } doReturn Response.success(mockResponse)
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        val tile = repository.getTile(
//            url = "https://test.com/tiles",
//            credentials = credentials
//        )
//        assertEquals(true, tile)
//
//        verify(mockService).getTile(
//            url = "https://test.com/tiles/1/1/1.png",
//            credentials = basicCredentials
//        )
//    }
//    @Test
//    fun testGetTileWithParameters() = runTest {
//        val mockResponse = "".toResponseBody(
//            contentType = "image/png".toMediaTypeOrNull()
//        )
//
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getTile(
//                    url = "https://test.com/tiles/1/1/1.png?one=one&two=two"
//                )
//            } doReturn Response.success(mockResponse)
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        val tile = repository.getTile(url = "https://test.com/tiles?one=one&two=two")
//        assertEquals(true, tile)
//
//        verify(mockService).getTile(
//            url = "https://test.com/tiles/1/1/1.png?one=one&two=two",
//            credentials = null
//        )
//    }
//
//    @Test
//    fun testGetWMSCapabilities() = runTest {
//        val wmsCapabilities = WMSCapabilities()
//
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getWMSCapabilities(
//                    url = "https://test.com/wms"
//                )
//            } doReturn Response.success(wmsCapabilities)
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        repository.getWMSCapabilities(url = "https://test.com/wms")
//
//        verify(mockService).getWMSCapabilities(
//            url = "https://test.com/wms?service=WMS&version=1.3.0&request=GetCapabilities",
//            credentials = null
//        )
//    }
//
//    @Test
//    fun testGetWMSCapabilitiesWithCredentials() = runTest {
//            val credentials = Credentials(username = "test", password = "test")
//            val basicCredentials = okhttp3.Credentials.basic(
//                credentials.username,
//                credentials.password
//            )
//
//            val mockService = mock<LayerService> {
//                onBlocking {
//                    getWMSCapabilities(
//                        url = "https://test.com/wms",
//                        credentials = basicCredentials
//                    )
//                } doReturn Response.success(WMSCapabilities())
//            }
//
//            val repository = LayerRemoteDataSource(mockService)
//            repository.getWMSCapabilities(url = "https://test.com/wms", credentials = credentials)
//
//            verify(mockService).getWMSCapabilities(
//                url = "https://test.com/wms?service=WMS&version=1.3.0&request=GetCapabilities",
//                credentials = basicCredentials
//            )
//    }
//
//    @Test
//    fun testGetWMSCapabilitiesWithParameters() = runTest {
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getWMSCapabilities(url = "https://test.com/wms")
//            } doReturn Response.success(WMSCapabilities())
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        repository.getWMSCapabilities(url = "https://test.com/wms?one=one&two=two")
//
//        verify(mockService).getWMSCapabilities(
//            url = "https://test.com/wms?service=WMS&version=1.3.0&request=GetCapabilities&one=one&two=two",
//            credentials = null
//        )
//    }
//
//    @Test
//    fun testGetWMSCapabilitiesWithInvalidUrl() = runTest {
//        val mockService = mock<LayerService> {
//            onBlocking {
//                getWMSCapabilities(url = "https:")
//            } doReturn Response.success(WMSCapabilities())
//        }
//
//        val repository = LayerRemoteDataSource(mockService)
//        val wmsCapabilities = repository.getWMSCapabilities(url = "https:")
//        assertEquals(null, wmsCapabilities)
//    }
//}
//
//
