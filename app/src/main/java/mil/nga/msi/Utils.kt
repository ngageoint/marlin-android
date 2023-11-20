package mil.nga.msi

import mil.nga.sf.GeometryEnvelope
import mil.nga.sf.Point
import mil.nga.sf.geojson.Geometry
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Polygon
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.max
import kotlin.math.min

val ISO_OFFSET_DATE_TIME_MOD: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss[.n]X")

fun String.parseAsInstant() = try { Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse(this)) } catch (e: DateTimeParseException) { null }

fun buildEnvelopesSpanning180thMeridian(minX: Double, minY: Double, maxX: Double, maxY: Double,): List<GeometryEnvelope> {
   val farLeft = max(minX, maxX)
   val farRight = min(minX, maxX)

   val leftEnvelope = GeometryEnvelope(farLeft, minY, 180.0, maxY)
   val rightEnvelope = GeometryEnvelope(-180.0, minY, farRight, maxY)

   return listOf(leftEnvelope, rightEnvelope)
}

fun getPointsForGeometry(geometry: Geometry): List<Point> {
   return when (geometry) {
      is mil.nga.sf.geojson.Point -> {
         listOf(geometry.point)
      }
      is LineString ->{
         geometry.lineString.points
      }
      is Polygon -> {
         geometry.polygon.rings[0].points
      }
      else -> {
         listOf(geometry.geometry.centroid)
      }
   }
}

