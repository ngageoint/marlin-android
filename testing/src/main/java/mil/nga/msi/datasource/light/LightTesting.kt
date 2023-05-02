import mil.nga.msi.datasource.light.Light
import org.junit.Assert

/**
 * Assert that the properties of the given [Light] instances are all equal.
 */
fun assertLightsEqual(expected: Light, actual: Light) {
    Assert.assertEquals(expected.id, actual.id)
    Assert.assertEquals(expected.latitude, actual.latitude, 1e-5)
    Assert.assertEquals(expected.longitude, actual.longitude, 1e-5)
    Assert.assertEquals(expected.volumeNumber, actual.volumeNumber)
    Assert.assertEquals(expected.featureNumber, actual.featureNumber)
    Assert.assertEquals(expected.characteristicNumber, actual.characteristicNumber)
    Assert.assertEquals(expected.noticeWeek, actual.noticeWeek)
    Assert.assertEquals(expected.noticeYear, actual.noticeYear)
    Assert.assertEquals(expected.internationalFeature, actual.internationalFeature)
    Assert.assertEquals(expected.aidType, actual.aidType)
    Assert.assertEquals(expected.geopoliticalHeading, actual.geopoliticalHeading)
    Assert.assertEquals(expected.regionHeading, actual.regionHeading)
    Assert.assertEquals(expected.subregionHeading, actual.subregionHeading)
    Assert.assertEquals(expected.localHeading, actual.localHeading)
    Assert.assertEquals(expected.precedingNote, actual.precedingNote)
    Assert.assertEquals(expected.name, actual.name)
    Assert.assertEquals(expected.position, actual.position)
    Assert.assertEquals(expected.characteristic, actual.characteristic)
    Assert.assertEquals(expected.heightFeet, actual.heightFeet)
    Assert.assertEquals(expected.heightMeters, actual.heightMeters)
    Assert.assertEquals(expected.range, actual.range)
    Assert.assertEquals(expected.structure, actual.structure)
    Assert.assertEquals(expected.remarks, actual.remarks)
    Assert.assertEquals(expected.postNote, actual.postNote)
    Assert.assertEquals(expected.noticeNumber, actual.noticeNumber)
    Assert.assertEquals(expected.removeFromList, actual.removeFromList)
    Assert.assertEquals(expected.deleteFlag, actual.deleteFlag)
}