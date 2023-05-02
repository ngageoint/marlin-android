package mil.nga.msi.network.radiobeacon

import assertRadioBeaconsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class RadioBeaconTypeAdapterTest {

    private lateinit var typeAdapter: RadioBeaconsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = RadioBeaconsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(beaconJson))
        val read = typeAdapter.read(jsonIn).radioBeacons

        assertEquals(1, read.size)
        assertRadioBeaconsEqual(beacon, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val beaconJson = """
            {
                "ngalol": [
                    {
                        "volumeNumber": "PUB 114",
                        "aidType": "Radiobeacons",
                        "geopoliticalHeading": "UNITED KINGDOM",
                        "regionHeading": "region heading",
                        "precedingNote": "preceding note",
                        "featureNumber": 10.0,
                        "name": "Brighton Marina",
                        "position": "50°48'42.01\"N \n0°05'57\"W",
                        "characteristic": "BM\n(- • • •  - - ).\n",
                        "range": "10",
                        "sequenceText": "sequence text",
                        "frequency": "294.5\nA1A.",
                        "stationRemark": "station remark",
                        "postNote": "post note",
                        "noticeNumber": 198001,
                        "removeFromList": "N",
                        "deleteFlag": "N",
                        "noticeWeek": "01",
                        "noticeYear": "1980"
                    }
                ]
            }"""

        val beacon = RadioBeacon(
            id = "PUB 114--10.0",
            volumeNumber = "PUB 114",
            featureNumber = "10.0",
            noticeWeek = "01",
            noticeYear = "1980",
            latitude = 50.811666,
            longitude = -0.0991666
        ).apply {
            aidType = "Radiobeacons"
            geopoliticalHeading = "UNITED KINGDOM"
            regionHeading = "region heading"
            precedingNote = "preceding note"
            name = "Brighton Marina"
            position = "50°48'42.01\"N \n0°05'57\"W"
            characteristic = "BM\n(- • • •  - - ).\n"
            range = "10"
            sequenceText = "sequence text"
            frequency = "294.5\nA1A."
            stationRemark = "station remark"
            postNote = "post note"
            noticeNumber = "198001"
            removeFromList = "N"
            deleteFlag = "N"
            sectionHeader = "UNITED KINGDOMregion heading"
        }
    }
}

