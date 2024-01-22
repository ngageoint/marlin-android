package mil.nga.msi.datasource.route

import org.junit.Assert

/**
 * Assert that the properties of the given [Route] instances are all equal.
 */
fun assertRoutesEqual(expectedId: Long, expected: Route, actual: Route) {
    Assert.assertEquals(expectedId, actual.id)
    Assert.assertEquals(expected.name, actual.name)
    Assert.assertEquals(expected.updatedTime, actual.updatedTime)
    Assert.assertEquals(expected.createdTime, actual.createdTime)
    // TODO: the rest of the properties
}