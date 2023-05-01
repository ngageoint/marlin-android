import mil.nga.msi.datasource.dgpsstation.DgpsStation
import org.junit.Assert

/**
 * Assert that the properties of the given [DgpsStation] instances are all equal.
 */
fun assertDgpsStationsEqual(expected: DgpsStation, actual: DgpsStation) {
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
    Assert.assertEquals(expected.stationId, actual.stationId)
    Assert.assertEquals(expected.range, actual.range)
    Assert.assertEquals(expected.frequency, actual.frequency)
    Assert.assertEquals(expected.transferRate, actual.transferRate)
    Assert.assertEquals(expected.remarks, actual.remarks)
    Assert.assertEquals(expected.postNote, actual.postNote)
    Assert.assertEquals(expected.noticeNumber, actual.noticeNumber)
    Assert.assertEquals(expected.removeFromList, actual.removeFromList)
    Assert.assertEquals(expected.deleteFlag, actual.deleteFlag)
}
