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
fun assertEPubsEqual(expected: ElectronicPublication, actual: ElectronicPublication) {
    Assert.assertEquals(expected, actual)
    Assert.assertEquals(expected.contentId, actual.contentId)
    Assert.assertEquals(expected.downloadedBytes, actual.downloadedBytes)
    Assert.assertEquals(expected.fileExtension, actual.fileExtension)
    Assert.assertEquals(expected.fileSize, actual.fileSize)
    Assert.assertEquals(expected.filenameBase, actual.filenameBase)
    Assert.assertEquals(expected.fullFilename, actual.fullFilename)
    Assert.assertEquals(expected.fullPubFlag, actual.fullPubFlag)
    Assert.assertEquals(expected.internalPath, actual.internalPath)
    Assert.assertEquals(expected.isDownloaded, actual.isDownloaded)
    Assert.assertEquals(expected.isDownloading, actual.isDownloading)
    Assert.assertEquals(expected.odsEntryId, actual.odsEntryId)
    Assert.assertEquals(expected.pubsecId, actual.pubsecId)
    Assert.assertEquals(expected.pubsecLastModified, actual.pubsecLastModified)
    Assert.assertEquals(expected.pubDownloadDisplayName, actual.pubDownloadDisplayName)
    Assert.assertEquals(expected.pubDownloadId, actual.pubDownloadId)
    Assert.assertEquals(expected.pubDownloadOrder, actual.pubDownloadOrder)
    Assert.assertEquals(expected.pubTypeId, actual.pubTypeId)
    Assert.assertEquals(expected.s3Key, actual.s3Key)
    Assert.assertEquals(expected.sectionDisplayName, actual.sectionDisplayName)
    Assert.assertEquals(expected.sectionLastModified, actual.sectionLastModified)
    Assert.assertEquals(expected.sectionName, actual.sectionName)
    Assert.assertEquals(expected.sectionOrder, actual.sectionOrder)
    Assert.assertEquals(expected.uploadTime, actual.uploadTime)
}