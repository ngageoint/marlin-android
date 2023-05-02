package mil.nga.msi.network.light

import assertLightsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.light.Light
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class LightTypeAdapterTest {

    private lateinit var typeAdapter: LightsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = LightsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(lightJson))
        val read = typeAdapter.read(jsonIn).lights

        assertEquals(1, read.size)
        assertLightsEqual(light, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val lightJson = """
            {
                "ngalol": [
                    {
                        "volumeNumber": "PUB 110",
                        "aidType": "Lighted Aids",
                        "geopoliticalHeading": "GREENLAND",
                        "regionHeading": "region heading",
                        "subregionHeading": "subregion heading",
                        "localHeading": "local heading",
                        "precedingNote": "preceding note",
                        "featureNumber": "4\nL5000",
                        "name": "-Outer.",
                        "position": "65°35'32.1\"N \n37°34'08.9\"W",
                        "charNo": 1,
                        "characteristic": "Fl.W.\nperiod 5s \nfl. 1.0s, ec. 4.0s \n",
                        "heightFeetMeters": "36\n11",
                        "range": "7",
                        "structure": "Yellow pedestal, red band; 7.\n",
                        "remarks": "remarks",
                        "postNote": "post note",
                        "noticeNumber": 201507,
                        "removeFromList": "N",
                        "deleteFlag": "Y",
                        "noticeWeek": "07",
                        "noticeYear": "2015"
                    }
                ]
            }"""

        val light = Light(
            id = "PUB 110--4--1",
            volumeNumber = "PUB 110",
            featureNumber = "4",
            characteristicNumber = 1,
            noticeWeek = "07",
            noticeYear = "2015",
            latitude = 65.592222,
            longitude = -37.56916
        ).apply {
            internationalFeature = "L5000"
            aidType = "Lighted Aids"
            geopoliticalHeading = "GREENLAND"
            regionHeading = "region heading"
            subregionHeading = "subregion heading"
            localHeading = "local heading"
            precedingNote = "preceding note"
            name = "-Outer."
            position = "65°35'32.1\"N \n37°34'08.9\"W"
            characteristic = "Fl.W.\nperiod 5s \nfl. 1.0s, ec. 4.0s \n"
            heightFeet = 36f
            heightMeters = 11f
            range = "7"
            structure = "Yellow pedestal, red band; 7.\n"
            remarks = "remarks"
            postNote = "post note"
            noticeNumber = 201507
            removeFromList = "N"
            deleteFlag = "Y"
            sectionHeader = "GREENLANDregion heading"
        }
    }
}

