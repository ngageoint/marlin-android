import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import org.junit.Assert

/**
 * TODO: this is duplicated from the local tests because there's no easy way to share code
 * between the local and instrumented tests.  we would have to create a separate module of shared
 * code that would depend on the app module, which might be impossible because it's not a library
 * and does not actually expose any of its elements.
 *
 * see https://issuetracker.google.com/issues/232420188
 */

/**
 * Assert that the properties of the given [ElectronicPublication] instances are all equal.
 */
fun assertEPubsEqual(a: ElectronicPublication, b: ElectronicPublication) {
    Assert.assertEquals(a, b)
    Assert.assertEquals(a.contentId, b.contentId)
    Assert.assertEquals(a.downloadedBytes, b.downloadedBytes)
    Assert.assertEquals(a.fileExtension, b.fileExtension)
    Assert.assertEquals(a.fileSize, b.fileSize)
    Assert.assertEquals(a.filenameBase, b.filenameBase)
    Assert.assertEquals(a.fullFilename, b.fullFilename)
    Assert.assertEquals(a.fullPubFlag, b.fullPubFlag)
    Assert.assertEquals(a.internalPath, b.internalPath)
    Assert.assertEquals(a.isDownloaded, b.isDownloaded)
    Assert.assertEquals(a.isDownloading, b.isDownloading)
    Assert.assertEquals(a.odsEntryId, b.odsEntryId)
    Assert.assertEquals(a.pubsecId, b.pubsecId)
    Assert.assertEquals(a.pubsecLastModified, b.pubsecLastModified)
    Assert.assertEquals(a.pubDownloadDisplayName, b.pubDownloadDisplayName)
    Assert.assertEquals(a.pubDownloadId, b.pubDownloadId)
    Assert.assertEquals(a.pubDownloadOrder, b.pubDownloadOrder)
    Assert.assertEquals(a.pubTypeId, b.pubTypeId)
    Assert.assertEquals(a.s3Key, b.s3Key)
    Assert.assertEquals(a.sectionDisplayName, b.sectionDisplayName)
    Assert.assertEquals(a.sectionLastModified, b.sectionLastModified)
    Assert.assertEquals(a.sectionName, b.sectionName)
    Assert.assertEquals(a.sectionOrder, b.sectionOrder)
    Assert.assertEquals(a.uploadTime, b.uploadTime)
}