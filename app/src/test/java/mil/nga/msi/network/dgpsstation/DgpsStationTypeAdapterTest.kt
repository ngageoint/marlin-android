package mil.nga.msi.network.dgpsstation

import assertDgpsStationsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.network.dgpsstations.DgpsStationsTypeAdapter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class DgpsStationTypeAdapterTest {

    private lateinit var typeAdapter: DgpsStationsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = DgpsStationsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(dgpsStationJson))
        val read = typeAdapter.read(jsonIn).dgpsStations

        assertEquals(1, read.size)
        assertDgpsStationsEqual(dgpsStation, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val dgpsStationJson = """
            {
                "ngalol": [{
                    "volumeNumber":"PUB 112",
                    "featureNumber":6.0,
                    "longitude":103.53333333288998,
                    "noticeWeek":"34",
                    "noticeYear":"2011",
                    "latitude": 1.0,
                    "longitude": 1.0,
                    "noticeNumber":201134,
                    "name": "Chojin Dan Lt",
                    "aidType":"Differential GPS Stations",
                    "position":"38°33'09\"N \n128°23'53.99\"E",
                    "geopoliticalHeading": "KOREA",
                    "regionHeading": "regional heading",
                    "precedingNote": "precedingNote",
                    "stationId":"T670\nR740\nR741\n",
                    "range":100,
                    "frequency":292.0,
                    "transferRate":200,
                    "remarks":"Message types: 3, 5, 7, 9, 16.",
                    "postNote":"post note",
                    "removeFromList":"N",
                    "deleteFlag":"N"
                }]
            }"""

        val dgpsStation = DgpsStation(
            id = "PUB 112--6.0",
            volumeNumber = "PUB 112",
            featureNumber = 6.0f,
            noticeWeek = "34",
            noticeYear = "2011",
            latitude = 38.55249,
            longitude = 128.39833
        ).apply {
            noticeNumber = 201134
            name = "Chojin Dan Lt"
            aidType = "Differential GPS Stations"
            position = "38°33'09\"N \n128°23'53.99\"E"
            geopoliticalHeading = "KOREA"
            regionHeading = "regional heading"
            precedingNote = "precedingNote"
            stationId = "T670\nR740\nR741\n"
            range = 100
            frequency = 292.0f
            transferRate = 200
            remarks = "Message types: 3, 5, 7, 9, 16."
            postNote = "post note"
            removeFromList = "N"
            deleteFlag = "N"
            sectionHeader = "KOREAregional heading"
        }
    }
}

