import mil.nga.msi.datasource.layer.Layer
import org.junit.Assert

/**
 * Assert that the properties of the given [Layer] instances are all equal.
 */
fun assertLayersEqual(expected: Layer, actual: Layer) {
    Assert.assertEquals(expected.type, actual.type)
    Assert.assertEquals(expected.url, actual.url)
    Assert.assertEquals(expected.name, actual.name)
    Assert.assertEquals(expected.minZoom, actual.minZoom)
    Assert.assertEquals(expected.maxZoom, actual.maxZoom)
    Assert.assertEquals(expected.visible, actual.visible)
    Assert.assertEquals(expected.filePath, actual.filePath)
    Assert.assertEquals(expected.refreshRate, actual.refreshRate)
}