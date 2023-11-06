package mil.nga.msi.datasource.navigationwarning

import org.junit.Assert

/**
 * Assert that the properties of the given [NavigationalWarning] instances are all equal.
 */
fun assertNavigationWarningsEqual(expected: NavigationalWarning, actual: NavigationalWarning) {
    Assert.assertEquals(expected.id, actual.id)
    Assert.assertEquals(expected.number, actual.number)
    Assert.assertEquals(expected.year, actual.year)
    Assert.assertEquals(expected.navigationArea, actual.navigationArea)
    Assert.assertEquals(expected.issueDate, actual.issueDate)
    Assert.assertEquals(expected.subregions, actual.subregions)
    Assert.assertEquals(expected.text, actual.text)
    Assert.assertEquals(expected.status, actual.status)
    Assert.assertEquals(expected.authority, actual.authority)
    Assert.assertEquals(expected.cancelNumber, actual.cancelNumber)
    Assert.assertEquals(expected.cancelDate, actual.cancelDate)
    Assert.assertEquals(expected.cancelNavigationArea, actual.cancelNavigationArea)
    Assert.assertEquals(expected.cancelYear, actual.cancelYear)
}

/**
 * Assert that the properties of the given [NavigationalWarning] instances are all equal to given [NavigationalWarningListItem].
 */
fun assertNavigationWarningsEqual(expected: NavigationalWarning, actual: NavigationalWarningListItem) {
    Assert.assertEquals(expected.number, actual.number)
    Assert.assertEquals(expected.year, actual.year)
    Assert.assertEquals(expected.navigationArea, actual.navigationArea)
    Assert.assertEquals(expected.issueDate, actual.issueDate)
    Assert.assertEquals(expected.subregions, actual.subregions)
    Assert.assertEquals(expected.text, actual.text)
}