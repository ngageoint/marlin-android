package mil.nga.msi

import mil.nga.sf.GeometryEnvelope
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.max
import kotlin.math.min

val ISO_OFFSET_DATE_TIME_MOD: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss[.n]X")

fun String.parseAsInstant() = try { Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse(this)) } catch (e: DateTimeParseException) { null }

data class SplitEnvelope (
   val leftEnvelope: GeometryEnvelope,
   val rightEnvelope: GeometryEnvelope
)
fun createSplitEnvelopeOn180thMeridian(minX: Double,maxX: Double,minY: Double,maxY: Double,): SplitEnvelope {
   val farLeft = max(minX, maxX)
   val farRight = min(minX, maxX)

   val leftEnvelope = GeometryEnvelope(farLeft, minY, 180.0, maxY)
   val rightEnvelope = GeometryEnvelope(-180.0, minY, farRight, maxY)

   return SplitEnvelope(leftEnvelope, rightEnvelope)
}