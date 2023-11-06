package mil.nga.msi.network.modu

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.RigStatus
import mil.nga.msi.datasource.modu.assertModusEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader
import java.text.SimpleDateFormat

class ModuTypeAdapterTest {

    private lateinit var typeAdapter: ModusTypeAdapter

    @Before
    fun setup() {
        typeAdapter = ModusTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(moduJson))
        val read = typeAdapter.read(jsonIn).modus

        assertEquals(1, read.size)
        assertModusEqual(modu, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        private const val moduJson = """
            {
                "modu": [{
                    "name":"590021",
                    "date":"2021-04-16",
                    "latitude":1.1999999998445219,
                    "longitude":103.53333333288998,
                    "rigStatus": "Active",
                    "specialStatus": "Specific Berth Requested",
                    "distance": 0.3,
                    "position":"53°14'00\"N \n3°14'30\"E",
                    "navArea":"HYDROLANT",
                    "region": 3,
                    "subregion":37,
                    "hostility": "Test Hostility",
                    "victim":"Test Victim",
                    "description":"Test Description"
                }]
            }"""

        private val modu = Modu(
            name = "590021",
            date = dateFormat.parse("2021-04-16")!!,
            latitude = 1.1999999998445219,
            longitude = 103.53333333288998,
        ).apply {
            rigStatus = RigStatus.ACTIVE
            specialStatus = "Specific Berth Requested"
            distance = 0.3
            position = "53°14'00\"N \n3°14'30\"E"
            navigationArea = "HYDROLANT"
            region = "3"
            subregion = "37"
        }
    }
}



