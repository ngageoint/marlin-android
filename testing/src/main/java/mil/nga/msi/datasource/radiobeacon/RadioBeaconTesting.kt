import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import org.junit.Assert

/**
 * Assert that the properties of the given [RadioBeacon] instances are all equal.
 */
fun assertRadioBeaconsEqual(expected: RadioBeacon, actual: RadioBeacon) {
    Assert.assertEquals(expected.id, actual.id)
    Assert.assertEquals(expected.volumeNumber, actual.volumeNumber)
    Assert.assertEquals(expected.featureNumber, actual.featureNumber)
    Assert.assertEquals(expected.noticeWeek, actual.noticeWeek)
    Assert.assertEquals(expected.noticeYear, actual.noticeYear)
    Assert.assertEquals(expected.latitude, actual.latitude, 1e-5)
    Assert.assertEquals(expected.longitude, actual.longitude, 1e-5)
    Assert.assertEquals(expected.aidType, actual.aidType)
    Assert.assertEquals(expected.geopoliticalHeading, actual.geopoliticalHeading)
    Assert.assertEquals(expected.regionHeading, actual.regionHeading)
    Assert.assertEquals(expected.precedingNote, actual.precedingNote)
    Assert.assertEquals(expected.name, actual.name)
    Assert.assertEquals(expected.position, actual.position)
    Assert.assertEquals(expected.characteristic, actual.characteristic)
    Assert.assertEquals(expected.range, actual.range)
    Assert.assertEquals(expected.sequenceText, actual.sequenceText)
    Assert.assertEquals(expected.frequency, actual.frequency)
    Assert.assertEquals(expected.stationRemark, actual.stationRemark)
    Assert.assertEquals(expected.postNote, actual.postNote)
    Assert.assertEquals(expected.noticeNumber, actual.noticeNumber)
    Assert.assertEquals(expected.removeFromList, actual.removeFromList)
    Assert.assertEquals(expected.deleteFlag, actual.deleteFlag)
    Assert.assertEquals(expected.sectionHeader, actual.sectionHeader)
}

