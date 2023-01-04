package mil.nga.msi.filter

import mil.nga.msi.datasource.port.types.EnumerationType

enum class ComparatorType(val title: String) {
   EQUALS(title = "="),
   NOT_EQUALS(title = "!="),
   GREATER_THAN(title = ">"),
   GREATER_THAN_OR_EQUAL(title = ">="),
   LESS_THAN(title = "<"),
   LESS_THAN_OR_EQUAL(title = "<="),
   CONTAINS(title = "contains"),
   NOT_CONTAINS(title = "not contains"),
   STARTS_WITH(title = "starts with"),
   ENDS_WITH(title = "ends with"),
   NEAR_ME(title = "near me"),
   CLOSE_TO(title = "close to"),
   WITHIN(title = "within")
}

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
   LOCATION(listOf(
      ComparatorType.CLOSE_TO,
      ComparatorType.NEAR_ME)
   ),
   ENUMERATION(listOf(
      ComparatorType.EQUALS,
      ComparatorType.NOT_EQUALS
   ))
}

data class FilterParameter(
   val title: String,
   val parameter: String,
   val type: FilterParameterType,
   val enumerationValues: List<EnumerationType> = emptyList()
)

data class Filter(
   val parameter: FilterParameter,
   val comparator: ComparatorType,
   val value: Any?
)