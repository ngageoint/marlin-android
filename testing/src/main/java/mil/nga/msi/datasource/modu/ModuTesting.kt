import mil.nga.msi.datasource.modu.Modu
import org.junit.Assert

/**
 * Assert that the properties of the given [Modu] instances are all equal.
 */
fun assertModusEqual(expected: Modu, actual: Modu) {
    Assert.assertEquals(expected.name, actual.name)
    Assert.assertEquals(expected.date, actual.date)
    Assert.assertEquals(expected.latitude, actual.latitude, 1e-5)
    Assert.assertEquals(expected.longitude, actual.longitude, 1e-5)
    Assert.assertEquals(expected.position, actual.position)
    Assert.assertEquals(expected.navigationArea, actual.navigationArea)
    Assert.assertEquals(expected.rigStatus, actual.rigStatus)
    Assert.assertEquals(expected.specialStatus, actual.specialStatus)
    Assert.assertEquals(expected.distance, actual.distance)
    Assert.assertEquals(expected.navigationArea, actual.navigationArea)
    Assert.assertEquals(expected.region, actual.region)
    Assert.assertEquals(expected.subregion, actual.subregion)
}