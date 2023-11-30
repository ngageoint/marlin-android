package mil.nga.msi.network.noticetomariners

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.assertNoticeToMarinersEqual
import mil.nga.msi.parseAsInstant
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class NoticeToMarinersTypeAdapterTest {

    private lateinit var typeAdapter: NoticeToMarinersTypeAdapter

    @Before
    fun setup() {
        typeAdapter = NoticeToMarinersTypeAdapter()
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(noticeJson))
        val read = typeAdapter.read(jsonIn).noticeToMariners

        assertEquals(1, read.size)
        assertNoticeToMarinersEqual(notice, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val noticeJson = """
            {
                "pubs": [
                    {
                        "publicationIdentifier": 42311,
                        "noticeNumber": 202318,
                        "title": "Front Cover",
                        "odsKey": "16694429/SFH00000/UNTM/202318/Front_Cover.pdf",
                        "sectionOrder": 20,
                        "limitedDist": false,
                        "odsEntryId": 30133,
                        "odsContentId": 16694429,
                        "internalPath": "UNTM/202318",
                        "filenameBase": "Front_Cover",
                        "fileExtension": "pdf",
                        "fileSize": 112373,
                        "isFullPublication": false,
                        "uploadTime": "2023-04-26T12:39:39.263+0000",
                        "lastModified": "2023-04-26T12:39:39.263Z"
                    }
                ]
            }"""

        val notice = NoticeToMariners(
            odsEntryId = 30133,
            odsKey = "16694429/SFH00000/UNTM/202318/Front_Cover.pdf",
            noticeNumber = 202318,
            filename = "Front_Cover.pdf"
        ).apply {
            title = "Front Cover"
            publicationId = 42311
            sectionOrder = 20
            limitedDist = false
            odsContentId = "16694429"
            internalPath = "UNTM/202318"
            fileSize = 112373
            isFullPublication = false
            uploadTime = "2023-04-26T12:39:39.263+0000".parseAsInstant()
            lastModified = "2023-04-26T12:39:39.263Z".parseAsInstant()
        }
    }
}

