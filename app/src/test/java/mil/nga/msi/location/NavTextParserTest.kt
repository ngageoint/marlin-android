package mil.nga.msi.location

import mil.nga.msi.coordinate.WGS84
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NavTextParserTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun testMap() {
      val things = listOf(
         listOf("odd" to 1, "even" to 2, "odd" to 3),
         listOf("even" to 4, "odd" to 5, "even" to 6),
         listOf("odd" to 7, "even" to 8, "odd" to 9)
      )

      val all = things.flatten()

      val listOfMaps= things.map { inner ->
         inner.groupBy { it.first }
      }

      val flattened = listOfMaps
         .flatMap { it.asSequence() }
         .associate { it.key to it.value }


      val result = listOfMaps
         .asSequence()
         .flatMap { it.asSequence() }
         .groupBy({ it.key }, { it.value })

      val map = listOfMaps
         .flatMap { it.entries }
         .groupBy { it.key }
         .mapValues { entry -> entry.value.map { it.value } }


   }

   @Test
   fun testParseLocation() {
      val text = "12-15.16N 071-57.30W"
      val latLng = WGS84.from(text)
      Assert.assertNotNull(latLng)
   }

   @Test
   fun testMetersDistance() {
      val distance = LocationWithType(
         distanceFromLocation = "FIVE MILE BERTH"
      ).metersDistance()
      Assert.assertNotNull(distance)
   }

   @Test
   fun testParseDistance() {
      val parser = NavTextParser()

      var distance = parser.parseDistance("IN AREA WITHIN 150 MILES OF 37-35.15N 045-40.37W.")
      Assert.assertEquals("150 MILES", distance)

      distance = parser.parseDistance("WESTERN NORTH ATLANTIC.\nTRINIDAD AND TOBAGO.\n1. UNDERWATER OPERATIONS 12 THRU 25 APR\n   BY M/V BOURBON EVOLUTION 802 IN 10-15.89N 060-33.25W.\n   500 METER BERTH REQUESTED.\n2. CANCEL THIS MSG 260001Z APR 23.\n")
      Assert.assertEquals("500 METER BERTH", distance)

      distance = parser.parseDistance("PERSIAN GULF.\nU.A.E.\nDNC 10.\n1. UNDERWATER OPERATIONS IN PROGRESS UNTIL\n   FURTHER NOTICE IN AREAS BOUND BY:\n   A. 24-40.22N 052-52.61E, 24-39.08N 053-00.18E,\n      24-30.79N 053-03.24E, 24-26.33N 052-56.63E,\n      24-30.70N 052-52.08E.\n   B. 24-24.56N 053-21.72E, 24-24.46N 053-28.22E,\n      24-17.96N 053-28.09E, 24-18.07N 053-21.60E.\n   WIDE BERTH REQUESTED.\n2. CANCEL HYDROPAC 1742/19.\n")
      Assert.assertEquals("WIDE BERTH", distance)

      distance = parser.parseDistance("BARENTS SEA. SVALBARD. DNC 22. SURVEY OPERATIONS IN PROGRESS UNTIL FURTHER NOTICE BY M/V RAMFORD HYPERION TOWING 12 4050 METER LONG CABLES IN AREA BOUND BY 73-44.50N 023-04.50E, 73-41.60N 025-52.40E, 73-13.10N 025-45.00E, 73-15.80N 023-01.40E. FOUR MILE BERTH REQUESTED.")
      Assert.assertEquals("FOUR MILE BERTH", distance)
   }

   @Test
   fun testGetHeadingAndSections() {
      val parser = NavTextParser()

      var headingAndSections = parser.getHeadingAndSections("Somewhere.\nSpecific.\nCHART 10\nSubject has something to say.\n1. number 1 thing\n")
      Assert.assertEquals("Somewhere.\nSpecific.\nCHART 10\nSubject has something to say.", headingAndSections.first)

      headingAndSections = parser.getHeadingAndSections("Somewhere. Specific. CHART 10 Subject has something to say. 1. number 1 thing ")
      Assert.assertEquals("Somewhere. Specific. CHART 10 Subject has something to say.", headingAndSections.first)

      headingAndSections = parser.getHeadingAndSections("Somewhere. Specific. CHART 10 Subject has something to say. A. letter a thing ")
      Assert.assertEquals("Somewhere. Specific. CHART 10 Subject has something to say.", headingAndSections.first)

      headingAndSections = parser.getHeadingAndSections("WESTERN SOUTH ATLANTIC.\nBRAZIL.\nDNC 01.\nDEPTHS LESS THAN CHARTED IN:\nA. 01-47.62S 043-51.68W.\nB. 01-57.76S 044-02.45W.\nC. 01-47.66S 043-52.00W.\n")
      Assert.assertEquals("WESTERN SOUTH ATLANTIC.\nBRAZIL.\nDNC 01.\nDEPTHS LESS THAN CHARTED IN:", headingAndSections.first)
      Assert.assertEquals("A. 01-47.62S 043-51.68W.\nB. 01-57.76S 044-02.45W.\nC. 01-47.66S 043-52.00W.", headingAndSections.second)
   }

   @Test
   fun testSplitNumbers() {
      val parser = NavTextParser()

      val numbers = parser.splitNumbers("1. BROADCAST AND COMMUNICATION SERVICES UNRELIABLE\n   1200Z TO 1400Z DAILY 18 AND 19 APR\n   AT USCG REMOTE COMMUNICATION FACILITIES:\n   A. BOSTON (F) 41-42.8N 070-30.3W.\n   B. CHARLESTON (E) 32-50.7N 079-57.0W.\n   C. CHESAPEAKE (N) 36-43.7N 076-00.6W.\n   D. MIAMI (A) 25-37.4N 080-23.4W.\n   E. NEW ORLEANS (G) 29-53.1N 089-56.7W.\n   F. SAN JUAN (R) 18-27.00N 066-06.00W.\n2. CANCEL THIS MSG 191500Z APR 23.\n")
      Assert.assertEquals(listOf("1. BROADCAST AND COMMUNICATION SERVICES UNRELIABLE\n   1200Z TO 1400Z DAILY 18 AND 19 APR\n   AT USCG REMOTE COMMUNICATION FACILITIES:\n   A. BOSTON (F) 41-42.8N 070-30.3W.\n   B. CHARLESTON (E) 32-50.7N 079-57.0W.\n   C. CHESAPEAKE (N) 36-43.7N 076-00.6W.\n   D. MIAMI (A) 25-37.4N 080-23.4W.\n   E. NEW ORLEANS (G) 29-53.1N 089-56.7W.\n   F. SAN JUAN (R) 18-27.00N 066-06.00W.", "2. CANCEL THIS MSG 191500Z APR 23."), numbers)
   }

   @Test
   fun testSplitLetters() {
      val parser = NavTextParser()

      var numbers = parser.splitLetters("   A. BOSTON (F) 41-42.8N 070-30.3W.\n   B. CHARLESTON (E) 32-50.7N 079-57.0W.\n   C. CHESAPEAKE (N) 36-43.7N 076-00.6W.\n   D. MIAMI (A) 25-37.4N 080-23.4W.\n   E. NEW ORLEANS (G) 29-53.1N 089-56.7W.\n   F. SAN JUAN (R) 18-27.00N 066-06.00W.\n")
      Assert.assertEquals(listOf("A. BOSTON (F) 41-42.8N 070-30.3W.","B. CHARLESTON (E) 32-50.7N 079-57.0W.","C. CHESAPEAKE (N) 36-43.7N 076-00.6W.","D. MIAMI (A) 25-37.4N 080-23.4W.","E. NEW ORLEANS (G) 29-53.1N 089-56.7W.","F. SAN JUAN (R) 18-27.00N 066-06.00W."), numbers)

      numbers = parser.splitLetters("   A. BOSTON (F) 41-42.8N 070-30.3W.    B. CHARLESTON (E) 32-50.7N 079-57.0W.    C. CHESAPEAKE (N) 36-43.7N 076-00.6W.    D. MIAMI (A) 25-37.4N 080-23.4W.    E. NEW ORLEANS (G) 29-53.1N 089-56.7W.    F. SAN JUAN (R) 18-27.00N 066-06.00W. ")
      Assert.assertEquals(listOf("A. BOSTON (F) 41-42.8N 070-30.3W.","B. CHARLESTON (E) 32-50.7N 079-57.0W.","C. CHESAPEAKE (N) 36-43.7N 076-00.6W.","D. MIAMI (A) 25-37.4N 080-23.4W.","E. NEW ORLEANS (G) 29-53.1N 089-56.7W.","F. SAN JUAN (R) 18-27.00N 066-06.00W."), numbers)
   }

   @Test
   fun testSplitSentences() {
      val parser = NavTextParser()

      var sentences = parser.splitSentences("Somewhere.\nSpecific.\nCHART 10\nSubject has something to say.\n")
      Assert.assertEquals(listOf("Somewhere.","Specific.","CHART 10 Subject has something to say."), sentences)

      sentences = parser.splitSentences("Somewhere.\nSpecific.\nCHART 10\nSubject has\nsomething to say.\n")
      Assert.assertEquals(listOf("Somewhere.","Specific.","CHART 10 Subject has something to say."), sentences)

      sentences = parser.splitSentences("   A. BOSTON (F) 41-42.8N 070-30.3W.\n   B. CHARLESTON (E) 32-50.7N 079-57.0W.\n   C. CHESAPEAKE (N) 36-43.7N 076-00.6W.\n   D. MIAMI (A) 25-37.4N 080-23.4W.\n   E. NEW ORLEANS (G) 29-53.1N 089-56.7W.\n   F. SAN JUAN (R) 18-27.00N 066-06.00W.\n")
      Assert.assertEquals(listOf("A.","BOSTON (F) 41-42.8N 070-30.3W.","B.","CHARLESTON (E) 32-50.7N 079-57.0W.","C.","CHESAPEAKE (N) 36-43.7N 076-00.6W.","D.","MIAMI (A) 25-37.4N 080-23.4W.","E.","NEW ORLEANS (G) 29-53.1N 089-56.7W.","F.","SAN JUAN (R) 18-27.00N 066-06.00W."), sentences)

      sentences = parser.splitSentences("1. BROADCAST AND COMMUNICATION SERVICES UNRELIABLE\n   1200Z TO 1400Z DAILY 18 AND 19 APR\n   AT USCG REMOTE COMMUNICATION FACILITIES:")
      Assert.assertEquals(listOf("1.","BROADCAST AND COMMUNICATION SERVICES UNRELIABLE 1200Z TO 1400Z DAILY 18 AND 19 APR AT USCG REMOTE COMMUNICATION FACILITIES:."), sentences)
   }

   @Test
   fun testSplitChart() {
      val parser = NavTextParser()

      var chart = parser.splitChartFromLine(
         line = "Subject has something to say."
      )
      Assert.assertEquals("Subject has something to say.", chart)

      chart = parser.splitChartFromLine(
         chart = "CHART 10",
         line = "CHART 10 Subject has something to say."
      )
      Assert.assertEquals("Subject has something to say.", chart)

      chart = parser.splitChartFromLine(
         dnc = "DNC 10",
         line = "DNC 10. Subject has something to say."
      )
      Assert.assertEquals("Subject has something to say.", chart)

      chart = parser.splitChartFromLine(
         dnc = "DNC 10",
         line = "DNC 10."
      )
      Assert.assertEquals("", chart)
   }

   @Test
   fun testParseHeading() {
      val parser = NavTextParser()

//      parser.parseHeading(listOf("NETHERLANDS ANTILLES.","NAVTEX STATION CURACAO (H) 12-10.31N 068-51.82W OFF AIR."))
//      Assert.assertEquals("NETHERLANDS ANTILLES.", parser.areaName)
//      Assert.assertEquals("NAVTEX STATION CURACAO (H) 12-10.31N 068-51.82W OFF AIR.", parser.specificArea)
//      Assert.assertEquals(1, parser.locations.size)
//      Assert.assertEquals("Point", parser.locations.first().locationType)
//      Assert.assertEquals("12-10.31N 068-51.82W", parser.locations.first().location.first())
   }

//   @Test
//   fun testSplitNumbers() {
//      val parser = spyk<LocationParser>()
//
//      val splitNumbers = parser.javaClass.getDeclaredMethod("splitNumbers", List::class.java)
//      splitNumbers.isAccessible = true
//      val numbers = splitNumbers.invoke(parser, "string parameter") as? List<*>
//      Assert.assertEquals(true, true)
//   }
}