package mil.nga.msi.network.asam

import assertAsamsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.asam.Asam
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader
import java.text.SimpleDateFormat

class AsamTypeAdapterTest {

    private lateinit var typeAdapter: AsamsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = AsamsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(asamJson))
        val read = typeAdapter.read(jsonIn).asams

        assertEquals(1, read.size)
        assertAsamsEqual(asam, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        const val asamJson = """
            {
                "asam": [{
                    "reference":"2022-200",
                    "date":"2022-09-07",
                    "latitude":1.1999999998445219,
                    "longitude":103.53333333288998,
                    "position":"1°12'00\"N \n103°32'00\"E",
                    "navArea":"XI",
                    "subreg":"71",
                    "hostility": "Test Hostility",
                    "victim":"Test Victim",
                    "description":"Test Description"
                }]
            }"""

        val asam = Asam(
            reference = "2022-200",
            date = dateFormat.parse("2022-09-07")!!,
            latitude = 1.1999999998445219,
            longitude = 103.53333333288998,
        ).apply {
            position = "1°12'00\"N \n103°32'00\"E"
            navigationArea = "XI"
            subregion = "71"
            hostility = "Test Hostility"
            victim = "Test Victim"
            description = "Test Description"
        }
    }
}


