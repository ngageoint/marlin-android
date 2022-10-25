package mil.nga.msi.datasource.filter

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.grid.features.Bounds
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.sf.util.GeometryUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val METERS_IN_NAUTICAL_MILE = 1852

class QueryBuilder(
   val table: String,
   val filters: List<Filter>
) {
   fun buildQuery(): SimpleSQLiteQuery {
      val filterStrings = mutableListOf<String>()
      filters.forEach { filter ->
         val filterString: String? = when (filter.parameter.type) {
            FilterParameterType.DATE -> {
               dateQuery(filter)
            }
            FilterParameterType.DOUBLE -> {
               doubleQuery(filter)
            }
            FilterParameterType.LOCATION -> {
               locationQuery(filter)
            }
            FilterParameterType.STRING -> {
               stringQuery(filter)
            }
            else -> null
         }
         filterString?.let { filterStrings.add(it) }
      }

      val condition = if (filterStrings.isNotEmpty()) {" WHERE ${filterStrings.joinToString(" AND ")}"} else ""
      Log.i("Billy", "table $table condition $condition")
      return SimpleSQLiteQuery("SELECT * FROM $table $condition")
   }

   private fun dateQuery(filter: Filter): String? {
      val epoch = try {
         val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
         val date: LocalDateTime = LocalDate.parse(filter.value.toString(), dateFormat).atTime(0, 0)
         date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
      } catch (e: Exception) { null }

      return when(filter.comparator) {
         ComparatorType.WITHIN -> {
            when(filter.value) { // TODO this should be a constant/enumeration
               "last 7 days" -> {
                  ZonedDateTime.now(ZoneOffset.UTC).minusDays(7).toInstant().toEpochMilli()
               }
               "last 30 days" -> {
                  ZonedDateTime.now(ZoneOffset.UTC).minusDays(30).toInstant().toEpochMilli()
               }
               "last 365 days" -> {
                  ZonedDateTime.now(ZoneOffset.UTC).minusDays(365).toInstant().toEpochMilli()
               }
               else -> null
            }?.let { "${filter.parameter.name} > $it" }
         }
         ComparatorType.EQUALS -> {
            epoch?.let { "${filter.parameter.name} = $epoch" }
         }
         ComparatorType.NOT_EQUALS -> {
            epoch?.let { "${filter.parameter.name} != $epoch" }
         }
         ComparatorType.GREATER_THAN -> {
            epoch?.let { "${filter.parameter.name} > $epoch" }
         }
         ComparatorType.GREATER_THAN_OR_EQUAL -> {
            epoch?.let { "${filter.parameter.name} >= $epoch" }
         }
         ComparatorType.LESS_THAN -> {
            epoch?.let { "${filter.parameter.name} < $epoch" }
         }
         ComparatorType.LESS_THAN_OR_EQUAL -> {
            epoch?.let { "${filter.parameter.name} <= $epoch" }
         }
         else -> null
      }
   }

   private fun doubleQuery(filter: Filter): String? {
      return filter.value?.toString()?.toDoubleOrNull()?.let{ value ->
         when(filter.comparator) {
            ComparatorType.EQUALS -> {
               "${filter.parameter.name} = $value"
            }
            ComparatorType.NOT_EQUALS -> {
               "${filter.parameter.name} != $value"
            }
            ComparatorType.GREATER_THAN -> {
               "${filter.parameter.name} > $value"
            }
            ComparatorType.GREATER_THAN_OR_EQUAL -> {
               "${filter.parameter.name} >= $value"
            }
            ComparatorType.LESS_THAN -> {
               "${filter.parameter.name} < $value"
            }
            ComparatorType.LESS_THAN_OR_EQUAL -> {
               "${filter.parameter.name} <= $value"
            }
            else -> null
         }
      }
   }

   private fun locationQuery(filter: Filter): String? {
      val bounds: Bounds? = when (filter.comparator) {
         ComparatorType.CLOSE_TO,
         ComparatorType.NEAR_ME -> {
            val value = filter.value.toString()
            val values = if (value.isNotEmpty()) value.split(",") else emptyList()
            val latitude = values.getOrNull(0)?.toDoubleOrNull()
            val longitude = values.getOrNull(1)?.toDoubleOrNull()
            val distance = values.getOrNull(2)?.toDoubleOrNull()

            if (latitude != null && longitude != null && distance != null) {
               val metersDistance = distance * METERS_IN_NAUTICAL_MILE
               val point = GeometryUtils.degreesToMeters(longitude, latitude)
               val southWest = GeometryUtils.metersToDegrees(point.x - metersDistance, point.y - metersDistance)
               val northEast = GeometryUtils.metersToDegrees(point.x + metersDistance, point.y + metersDistance)
               Bounds(southWest.x, southWest.y, northEast.x, northEast.y)
            } else null
         }
         else -> null
      }

      return bounds?.let {
         "latitude >= ${it.minY} AND longitude >= ${it.minX} AND latitude <= ${it.maxY} AND longitude <= ${it.maxX}"
      }
   }

   private fun stringQuery(filter: Filter): String? {
      return when(filter.comparator) {
         ComparatorType.EQUALS -> {
            "${filter.parameter.name} = '${filter.value}'"
         }
         ComparatorType.NOT_EQUALS -> {
            "${filter.parameter.name} != '${filter.value}'"
         }
         ComparatorType.CONTAINS -> {
            "${filter.parameter.name} LIKE '%${filter.value}%'"
         }
         ComparatorType.NOT_CONTAINS -> {
            "${filter.parameter.name} NOT LIKE '%${filter.value}%'"
         }
         ComparatorType.STARTS_WITH -> {
            "${filter.parameter.name} LIKE '${filter.value}%'"
         }
         ComparatorType.ENDS_WITH -> {
            "${filter.parameter.name} LIKE '%${filter.value}'"
         }
         else -> null
      }
   }
}