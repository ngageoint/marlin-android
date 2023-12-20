package mil.nga.msi.map

import android.content.Context
import android.util.DisplayMetrics
import com.google.maps.android.geometry.Bounds
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import mil.nga.msi.ui.map.overlay.NavigationalWarningImage
import mil.nga.sf.geojson.FeatureConverter
import org.junit.After
import org.junit.Assert
import org.junit.Test

//@RunWith(AndroidJUnit4::class)
class DataSourceTileProviderTest {
   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_scale_circular_nav_warnings() {
      val mockContext = mockk<Context>()
      every { mockContext.resources.displayMetrics } returns DisplayMetrics().apply {
         this.density = 2.75F
      }
      val geoJson = """
         {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[134.76833333333335,29.472833333333334]},"properties":{"radius":129640.0}}]}
      """.trimIndent()
      val feature = FeatureConverter.toFeatureCollection(geoJson).features.first()
      val navImage = NavigationalWarningImage(feature)
      val largerCircle = navImage.image(mockContext, 3, Bounds(0.0, 0.0, 0.0, 0.0), 0.0)

      val geoJson2 = """
         {"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[134.76833333333335,29.472833333333334]},"properties":{"radius":100000.0}}]}
      """.trimIndent()
      val feature2 = FeatureConverter.toFeatureCollection(geoJson2).features.first()
      val navImage2 = NavigationalWarningImage(feature2)
      val smallerCircle = navImage2.image(mockContext, 3, Bounds(0.0, 0.0, 0.0, 0.0), 0.0)

      Assert.assertEquals(15, largerCircle.first().height)
      Assert.assertEquals(12, smallerCircle.first().height)
   }
}