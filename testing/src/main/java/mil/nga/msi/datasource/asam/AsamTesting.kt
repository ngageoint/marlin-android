package mil.nga.msi.datasource.asam

import org.junit.Assert

/**
 * Assert that the properties of the given [Asam] instances are all equal.
 */
fun assertAsamsEqual(expected: Asam, actual: Asam) {
    Assert.assertEquals(expected.reference, actual.reference)
    Assert.assertEquals(expected.date, actual.date)
    Assert.assertEquals(expected.latitude, actual.latitude, 1e-5)
    Assert.assertEquals(expected.longitude, actual.longitude, 1e-5)
    Assert.assertEquals(expected.position, actual.position)
    Assert.assertEquals(expected.navigationArea, actual.navigationArea)
    Assert.assertEquals(expected.subregion, actual.subregion)
    Assert.assertEquals(expected.description, actual.description)
    Assert.assertEquals(expected.hostility, actual.hostility)
    Assert.assertEquals(expected.victim, actual.victim)
}