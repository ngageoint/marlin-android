package mil.nga.msi.datasource.filter

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