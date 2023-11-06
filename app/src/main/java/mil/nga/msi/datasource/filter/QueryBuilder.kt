package mil.nga.msi.datasource.filter

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.grid.features.Bounds
import mil.nga.msi.datasource.port.types.EnumerationType
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.sort.SortParameter
import mil.nga.sf.util.GeometryUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val METERS_IN_NAUTICAL_MILE = 1852

class QueryBuilder(
   val table: String,
   val filters: List<Filter> = emptyList(),
   val sort: List<SortParameter> = emptyList()
) {
   fun buildQuery(
      count: Boolean = false
   ): SimpleSQLiteQuery {
      val filterStrings = mutableListOf<String>()
      filters.forEach { filter ->
         val filterString: String? = when (filter.parameter.type) {
            FilterParameterType.DATE -> {
               dateQuery(filter)
            }
            FilterParameterType.DOUBLE -> {
               doubleQuery(filter)
            }
            FilterParameterType.ENUMERATION -> {
               enumerationQuery(filter)
            }
            FilterParameterType.FLOAT -> {
               floatQuery(filter)
            }
            FilterParameterType.INT -> {
               intQuery(filter)
            }
            FilterParameterType.LOCATION -> {
               locationQuery(filter)
            }
            FilterParameterType.STRING -> {
               stringQuery(filter)
            }
         }
         filterString?.let { filterStrings.add(it) }
      }

      val sortStrings = mutableListOf<String>()
      sort.forEach { sort ->
         sortStrings.add("${sort.parameter.parameter}  ${sort.direction.name}")
      }

      val condition = if (filterStrings.isNotEmpty()) {" WHERE ${filterStrings.joinToString(" AND ")}"} else ""
      val sort = if (sortStrings.isNotEmpty()) {" ORDER BY  ${sortStrings.joinToString(",")}"} else ""
      val parameter = if (count) "COUNT(*)" else "*"
      return SimpleSQLiteQuery("SELECT $parameter FROM $table $condition $sort")
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
            }?.let { "${filter.parameter.parameter} > $it" }
         }
         ComparatorType.EQUALS -> {
            epoch?.let { "${filter.parameter.parameter} = $epoch" }
         }
         ComparatorType.NOT_EQUALS -> {
            epoch?.let { "${filter.parameter.parameter} != $epoch" }
         }
         ComparatorType.GREATER_THAN -> {
            epoch?.let { "${filter.parameter.parameter} > $epoch" }
         }
         ComparatorType.GREATER_THAN_OR_EQUAL -> {
            epoch?.let { "${filter.parameter.parameter} >= $epoch" }
         }
         ComparatorType.LESS_THAN -> {
            epoch?.let { "${filter.parameter.parameter} < $epoch" }
         }
         ComparatorType.LESS_THAN_OR_EQUAL -> {
            epoch?.let { "${filter.parameter.parameter} <= $epoch" }
         }
         else -> null
      }
   }

   private fun doubleQuery(filter: Filter): String? {
      return filter.value?.toString()?.toDoubleOrNull()?.let{ value ->
         when(filter.comparator) {
            ComparatorType.EQUALS -> {
               "${filter.parameter.parameter} = $value"
            }
            ComparatorType.NOT_EQUALS -> {
               "${filter.parameter.parameter} != $value"
            }
            ComparatorType.GREATER_THAN -> {
               "${filter.parameter.parameter} > $value"
            }
            ComparatorType.GREATER_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} >= $value"
            }
            ComparatorType.LESS_THAN -> {
               "${filter.parameter.parameter} < $value"
            }
            ComparatorType.LESS_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} <= $value"
            }
            else -> null
         }
      }
   }

   private fun enumerationQuery(filter: Filter): String? {
      val enumeration = filter.value as? EnumerationType
      return enumeration?.name?.let { name ->
         when(filter.comparator) {
            ComparatorType.EQUALS -> {
               "${filter.parameter.parameter} = '${name}'"
            }
            ComparatorType.NOT_EQUALS -> {
               "${filter.parameter.parameter} != '${name}'"
            }
            else -> null
         }
      }
   }

   private fun floatQuery(filter: Filter): String? {
      return filter.value?.toString()?.toFloatOrNull()?.let{ value ->
         when(filter.comparator) {
            ComparatorType.EQUALS -> {
               "${filter.parameter.parameter} = $value"
            }
            ComparatorType.NOT_EQUALS -> {
               "${filter.parameter.parameter} != $value"
            }
            ComparatorType.GREATER_THAN -> {
               "${filter.parameter.parameter} > $value"
            }
            ComparatorType.GREATER_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} >= $value"
            }
            ComparatorType.LESS_THAN -> {
               "${filter.parameter.parameter} < $value"
            }
            ComparatorType.LESS_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} <= $value"
            }
            else -> null
         }
      }
   }

   private fun intQuery(filter: Filter): String? {
      return filter.value?.toString()?.toIntOrNull()?.let{ value ->
         when(filter.comparator) {
            ComparatorType.EQUALS -> {
               "${filter.parameter.parameter} = $value"
            }
            ComparatorType.NOT_EQUALS -> {
               "${filter.parameter.parameter} != $value"
            }
            ComparatorType.GREATER_THAN -> {
               "${filter.parameter.parameter} > $value"
            }
            ComparatorType.GREATER_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} >= $value"
            }
            ComparatorType.LESS_THAN -> {
               "${filter.parameter.parameter} < $value"
            }
            ComparatorType.LESS_THAN_OR_EQUAL -> {
               "${filter.parameter.parameter} <= $value"
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
            "${filter.parameter.parameter} LIKE '${filter.value}'"
         }
         ComparatorType.NOT_EQUALS -> {
            "${filter.parameter.parameter} NOT LIKE '${filter.value}'"
         }
         ComparatorType.CONTAINS -> {
            "${filter.parameter.parameter} LIKE '%${filter.value}%'"
         }
         ComparatorType.NOT_CONTAINS -> {
            "${filter.parameter.parameter} NOT LIKE '%${filter.value}%'"
         }
         ComparatorType.STARTS_WITH -> {
            "${filter.parameter.parameter} LIKE '${filter.value}%'"
         }
         ComparatorType.ENDS_WITH -> {
            "${filter.parameter.parameter} LIKE '%${filter.value}'"
         }
         else -> null
      }
   }
}