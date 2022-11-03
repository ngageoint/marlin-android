package mil.nga.msi.filter

import mil.nga.msi.datasource.filter.ComparatorType

enum class FilterParameterType(val comparators: List<ComparatorType>) {
   STRING(listOf(
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS,
      ComparatorType.CONTAINS,
      ComparatorType.NOT_CONTAINS,
      ComparatorType.STARTS_WITH,
      ComparatorType.ENDS_WITH)
   ),
   DATE(listOf(
      ComparatorType.WITHIN,
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS,
      ComparatorType.GREATER_THAN,
      ComparatorType.GREATER_THAN_OR_EQUAL,
      ComparatorType.LESS_THAN,
      ComparatorType.LESS_THAN_OR_EQUAL)
   ),
   FLOAT(listOf(
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS,
      ComparatorType.GREATER_THAN,
      ComparatorType.GREATER_THAN_OR_EQUAL,
      ComparatorType.LESS_THAN,
      ComparatorType.LESS_THAN_OR_EQUAL)
   ),
   INT(listOf(
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS,
      ComparatorType.GREATER_THAN,
      ComparatorType.GREATER_THAN_OR_EQUAL,
      ComparatorType.LESS_THAN,
      ComparatorType.LESS_THAN_OR_EQUAL)
   ),
   DOUBLE(listOf(
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS,
      ComparatorType.GREATER_THAN,
      ComparatorType.GREATER_THAN_OR_EQUAL,
      ComparatorType.LESS_THAN,
      ComparatorType.LESS_THAN_OR_EQUAL)
   ),
   BOOLEAN(listOf()),
   LOCATION(listOf(
      ComparatorType.CLOSE_TO,
      ComparatorType.NEAR_ME)
   )
}

data class FilterParameter(
   val type: FilterParameterType,
   val title: String,
   val name: String,
)