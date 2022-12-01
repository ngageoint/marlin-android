package mil.nga.msi.network.electronicpublication

import com.google.gson.stream.JsonReader
import mil.nga.msi.ISO_OFFSET_DATE_TIME_MOD
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.StringReader
import java.time.Instant


class ElectronicPublicationTypeAdapterTest {

    lateinit var subject: ElectronicPublicationTypeAdapter

    @Before
    fun setup() {
        subject = ElectronicPublicationTypeAdapter()
    }

    @Test
    fun deserializes_from_a_json_object() {

        val jsonIn = JsonReader(StringReader(ePubJson))
        val read = subject.read(jsonIn)

        assertEPubsEqual(read!!, ePub)
        assertFalse(jsonIn.hasNext())
    }

    @Test
    fun deserializes_minimal_json_object() {

        val jsonIn = JsonReader(StringReader("""{ "s3Key": "abc123" } """))
        val read = subject.read(jsonIn)

        assertEPubsEqual(read!!, ElectronicPublication(s3Key = "abc123"))
    }

    @Test
    fun deserializes_null_without_s3_key() {

        val jsonIn = JsonReader(StringReader("""{ "sectionName": "abc123" } """))
        val read = subject.read(jsonIn)

        assertNull(read)
    }
}

const val ePubJson = """
{
    "pubTypeId": 30,
    "pubDownloadId": 14,
    "fullPubFlag": false,
    "pubDownloadOrder": 4,
    "pubDownloadDisplayName": "Pub. 108 - Atlas of Pilot Charts North Pacific Ocean, 3rd Ed. 1994",
    "pubsecId": 68,
    "odsEntryId": 22205,
    "sectionOrder": 14,
    "sectionName": "108dec",
    "sectionDisplayName": "Pub. 108: December",
    "sectionLastModified": "2019-09-20T14:02:18.929+0000",
    "contentId": 16693989,
    "internalPath": "",
    "filenameBase": "108dec",
    "fileExtension": "pdf",
    "s3Key": "16693989/SFH00000/108dec.pdf",
    "fileSize": 8573556,
    "uploadTime": "2019-09-20T14:02:18.929+0000",
    "fullFilename": "108dec.pdf",
    "pubsecLastModified": "2019-09-20T14:02:18.929685Z"
}
"""

val ePub: ElectronicPublication = ElectronicPublication(
    s3Key = "16693989/SFH00000/108dec.pdf",
    contentId = 16693989,
    downloadedBytes = 0,
    filenameBase = "108dec",
    fileExtension = "pdf",
    fileSize = 8573556,
    fullFilename = "108dec.pdf",
    fullPubFlag = false,
    internalPath = "",
    isDownloaded = false,
    isDownloading = false,
    odsEntryId = 22205,
    pubDownloadOrder = 4,
    pubDownloadDisplayName = "Pub. 108 - Atlas of Pilot Charts North Pacific Ocean, 3rd Ed. 1994",
    pubDownloadId = 14,
    pubsecId = 68,
    pubsecLastModified = Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse("2019-09-20T14:02:18.929685Z")),
    pubTypeId = 30,
    sectionOrder = 14,
    sectionName = "108dec",
    sectionDisplayName = "Pub. 108: December",
    sectionLastModified = Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse("2019-09-20T14:02:18.929+0000")),
    uploadTime = Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse("2019-09-20T14:02:18.929+0000"))
)

fun assertEPubsEqual(a: ElectronicPublication, b: ElectronicPublication) {
    assertEquals(a, b)
    assertEquals(a.contentId, b.contentId)
    assertEquals(a.downloadedBytes, b.downloadedBytes)
    assertEquals(a.fileExtension, b.fileExtension)
    assertEquals(a.fileSize, b.fileSize)
    assertEquals(a.filenameBase, b.filenameBase)
    assertEquals(a.fullFilename, b.fullFilename)
    assertEquals(a.fullPubFlag, b.fullPubFlag)
    assertEquals(a.internalPath, b.internalPath)
    assertEquals(a.isDownloaded, b.isDownloaded)
    assertEquals(a.isDownloading, b.isDownloading)
    assertEquals(a.odsEntryId, b.odsEntryId)
    assertEquals(a.pubsecId, b.pubsecId)
    assertEquals(a.pubsecLastModified, b.pubsecLastModified)
    assertEquals(a.pubDownloadDisplayName, b.pubDownloadDisplayName)
    assertEquals(a.pubDownloadId, b.pubDownloadId)
    assertEquals(a.pubDownloadOrder, b.pubDownloadOrder)
    assertEquals(a.pubTypeId, b.pubTypeId)
    assertEquals(a.s3Key, b.s3Key)
    assertEquals(a.sectionDisplayName, b.sectionDisplayName)
    assertEquals(a.sectionLastModified, b.sectionLastModified)
    assertEquals(a.sectionName, b.sectionName)
    assertEquals(a.sectionOrder, b.sectionOrder)
    assertEquals(a.uploadTime, b.uploadTime)
}
