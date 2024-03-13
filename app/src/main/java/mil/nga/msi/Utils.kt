package mil.nga.msi

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import mil.nga.sf.Geometry
import mil.nga.sf.GeometryEnvelope
import mil.nga.sf.LineString
import mil.nga.sf.Point
import mil.nga.sf.Polygon
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.max
import kotlin.math.min

val ISO_OFFSET_DATE_TIME_MOD: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss[.n]X")

fun String.parseAsInstant() = try { Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse(this)) } catch (e: DateTimeParseException) { null }

fun buildEnvelopesSpanning180thMeridian(minX: Double, minY: Double, maxX: Double, maxY: Double): List<GeometryEnvelope> {
   val farLeft = max(minX, maxX)
   val farRight = min(minX, maxX)

   val leftEnvelope = GeometryEnvelope(farLeft, minY, 180.0, maxY)
   val rightEnvelope = GeometryEnvelope(-180.0, minY, farRight, maxY)

   return listOf(leftEnvelope, rightEnvelope)
}

fun getPointsForGeometry(geometry: Geometry): List<Point> {
   return when (geometry) {
      is Point -> {
         listOf(geometry)
      }
      is LineString ->{
         geometry.points
      }
      is Polygon -> {
         geometry.rings[0].points
      }
      else -> {
         listOf(geometry.centroid)
      }
   }
}

fun buildZoomNavOptions(navController: NavController): NavOptions {
   return navOptions {
      popUpTo(navController.graph.findStartDestination().id) {
         saveState = true
         inclusive = true
      }
      launchSingleTop = true
   }
}
