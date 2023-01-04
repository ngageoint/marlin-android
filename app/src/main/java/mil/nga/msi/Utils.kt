package mil.nga.msi

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val ISO_OFFSET_DATE_TIME_MOD = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss[.n]X")

fun String.parseAsInstant() = try { Instant.from(ISO_OFFSET_DATE_TIME_MOD.parse(this)) } catch (e: DateTimeParseException) { null }