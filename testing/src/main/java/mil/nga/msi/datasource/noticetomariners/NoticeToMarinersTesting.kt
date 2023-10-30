package mil.nga.msi.datasource.noticetomariners

import org.junit.Assert

/**
 * Assert that the properties of the given [NoticeToMariners] instances are all equal.
 */
fun assertNoticeToMarinersEqual(expected: NoticeToMariners, actual: NoticeToMariners) {
    Assert.assertEquals(expected.odsEntryId, actual.odsEntryId)
    Assert.assertEquals(expected.odsKey, actual.odsKey)
    Assert.assertEquals(expected.noticeNumber, actual.noticeNumber)
    Assert.assertEquals(expected.filename, actual.filename)
    Assert.assertEquals(expected.odsContentId, actual.odsContentId)
    Assert.assertEquals(expected.publicationId, actual.publicationId)
    Assert.assertEquals(expected.title, actual.title)
    Assert.assertEquals(expected.sectionOrder, actual.sectionOrder)
    Assert.assertEquals(expected.limitedDist, actual.limitedDist)
    Assert.assertEquals(expected.internalPath, actual.internalPath)
    Assert.assertEquals(expected.fileSize, actual.fileSize)
    Assert.assertEquals(expected.isFullPublication, actual.isFullPublication)
    Assert.assertEquals(expected.uploadTime, actual.uploadTime)
    Assert.assertEquals(expected.lastModified, actual.lastModified)
}