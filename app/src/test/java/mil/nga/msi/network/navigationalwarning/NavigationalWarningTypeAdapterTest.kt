package mil.nga.msi.network.navigationalwarning

import assertNavigationWarningsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale

class NavigationalWarningTypeAdapterTest {

    private lateinit var typeAdapter: NavigationalWarningsTypeAdapter

    @Before
    fun setup() {
        typeAdapter = NavigationalWarningsTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(warningJson))
        val read = typeAdapter.read(jsonIn).warnings

        assertEquals(1, read.size)
        assertNavigationWarningsEqual(warning, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yyyy", Locale.US)

        const val warningJson = """
            {
                "broadcast-warn": [{
                  "msgYear": 2023,
                  "msgNumber": 464,
                  "navArea": "4",
                  "subregion": "24",
                  "text": "WESTERN NORTH ATLANTIC.\nSURINAME.\n1. OPERATIONS IN PROGRESS UNTIL 17 MAY APR\n   BY M/V ATLANTIC SPIRIT IN 07-21.61N 055-41.02W.\n   FIVE MILE BERTH REQUESTED.\n2. CANCEL THIS MSG 180001Z MAY 23.\n",
                  "status": "A",
                  "issueDate": "261846Z APR 2023",
                  "authority": "SURINAME 7/23 261838Z APR 23.",
                  "cancelDate": "261846Z APR 2023",
                  "cancelNavArea": "cancel navigation area",
                  "cancelMsgYear": 2023,
                  "cancelMsgNumber": 1,
                  "year": 2023,
                  "area": "4",
                  "number": 464
                }]
            }"""

        val warning = NavigationalWarning(
            id = "464--2023--NAVAREA_IV",
            number = 464,
            year = 2023,
            navigationArea = NavigationArea.NAVAREA_IV,
            issueDate = dateFormat.parse("261846Z APR 2023")!!
        ).apply {
            subregions = mutableListOf("24")
            text = "WESTERN NORTH ATLANTIC.\nSURINAME.\n1. OPERATIONS IN PROGRESS UNTIL 17 MAY APR\n   BY M/V ATLANTIC SPIRIT IN 07-21.61N 055-41.02W.\n   FIVE MILE BERTH REQUESTED.\n2. CANCEL THIS MSG 180001Z MAY 23.\n"
            status = "A"
            authority = "SURINAME 7/23 261838Z APR 23."
            cancelNumber = 1
            cancelDate = dateFormat.parse("261846Z APR 2023")!!
            cancelNavigationArea = "cancel navigation area"
            cancelYear = 2023
        }
    }
}


