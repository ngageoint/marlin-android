package mil.nga.msi.network.electronicpublication

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.network.nextBooleanOrNull
import mil.nga.msi.network.nextIntOrNull
import mil.nga.msi.network.nextLongOrNull
import mil.nga.msi.network.nextStringOrNull
import mil.nga.msi.parseAsInstant
import java.lang.UnsupportedOperationException
import java.time.Instant

class ElectronicPublicationTypeAdapter: TypeAdapter<ElectronicPublication>() {

    override fun write(jsonOut: JsonWriter?, value: ElectronicPublication?) {
        throw UnsupportedOperationException()
    }

    override fun read(jsonIn: JsonReader): ElectronicPublication? {
        jsonIn.beginObject()
        val ePub = readEPubKeys(jsonIn)
        jsonIn.endObject()
        return ePub
    }
}

private fun readEPubKeys(jsonIn: JsonReader): ElectronicPublication? {
    var s3Key: String? = null
    var contentId: Int? = null
    var fileExtension: String? = null
    var filenameBase: String? = null
    var fileSize: Long? = null
    var fullFilename: String? = null
    var fullPubFlag: Boolean? = null
    var internalPath: String? = null
    var odsEntryId: Int? = null
    var pubDownloadDisplayName: String? = null
    var pubDownloadId: Int? = null
    var pubDownloadOrder: Int? = null
    var pubsecId: Int? = null
    var pubsecLastModified: Instant? = null
    var pubTypeId: Int = ElectronicPublicationType.Unknown.typeId
    var sectionDisplayName: String? = null
    var sectionLastModified: Instant? = null
    var sectionName: String? = null
    var sectionOrder: Int? = null
    var uploadTime: Instant? = null
    val downloadedBytes = 0L
    val isDownloaded = false
    val isDownloading = false

    while (jsonIn.hasNext() && jsonIn.peek() !== JsonToken.END_OBJECT) {
        val keyName = jsonIn.nextName()
        when (keyName) {
            "s3Key" -> s3Key = jsonIn.nextStringOrNull()
            "contentId" -> contentId = jsonIn.nextIntOrNull()
            "fileExtension" -> fileExtension = jsonIn.nextStringOrNull()
            "filenameBase" -> filenameBase = jsonIn.nextStringOrNull()
            "fileSize" -> fileSize = jsonIn.nextLongOrNull()
            "fullFilename" -> fullFilename = jsonIn.nextStringOrNull()
            "fullPubFlag" -> fullPubFlag = jsonIn.nextBooleanOrNull()
            "internalPath" -> internalPath = jsonIn.nextStringOrNull()
            "odsEntryId" -> odsEntryId = jsonIn.nextIntOrNull()
            "pubDownloadDisplayName" -> pubDownloadDisplayName = jsonIn.nextStringOrNull()
            "pubDownloadId" -> pubDownloadId = jsonIn.nextIntOrNull()
            "pubDownloadOrder" -> pubDownloadOrder = jsonIn.nextIntOrNull()
            "pubsecId" -> pubsecId = jsonIn.nextIntOrNull()
            "pubsecLastModified" -> pubsecLastModified = jsonIn.nextStringOrNull()?.parseAsInstant()
            "pubTypeId" -> pubTypeId = jsonIn.nextIntOrNull() ?: ElectronicPublicationType.Unknown.typeId
            "sectionDisplayName" -> sectionDisplayName = jsonIn.nextStringOrNull()
            "sectionLastModified" -> sectionLastModified = jsonIn.nextStringOrNull()?.parseAsInstant()
            "sectionName" -> sectionName = jsonIn.nextStringOrNull()
            "sectionOrder" -> sectionOrder = jsonIn.nextIntOrNull()
            "uploadTime" -> uploadTime = jsonIn.nextStringOrNull()?.parseAsInstant()
        }
    }

    return if (s3Key == null) null else ElectronicPublication(
        s3Key = s3Key,
        contentId = contentId,
        downloadedBytes = downloadedBytes,
        fileExtension = fileExtension,
        filenameBase = filenameBase,
        fileSize = fileSize,
        fullFilename = fullFilename,
        fullPubFlag = fullPubFlag,
        internalPath = internalPath,
        isDownloaded = isDownloaded,
        isDownloading = isDownloading,
        odsEntryId = odsEntryId,
        pubDownloadDisplayName = pubDownloadDisplayName,
        pubDownloadId = pubDownloadId,
        pubDownloadOrder = pubDownloadOrder,
        pubsecId = pubsecId,
        pubsecLastModified = pubsecLastModified,
        pubTypeId = pubTypeId,
        sectionDisplayName = sectionDisplayName,
        sectionLastModified = sectionLastModified,
        sectionName = sectionName,
        sectionOrder = sectionOrder,
        uploadTime = uploadTime,
    )
} 