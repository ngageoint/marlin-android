import mil.nga.msi.datasource.bookmark.Bookmark
import org.junit.Assert

/**
 * Assert that the properties of the given [Bookmark] instances are all equal.
 */
fun assertBookmarksEqual(expected: Bookmark, actual: Bookmark) {
    Assert.assertEquals(expected.id, actual.id)
    Assert.assertEquals(expected.dataSource, actual.dataSource)
    Assert.assertEquals(expected.date, actual.date)
    Assert.assertEquals(expected.notes, actual.notes)
}