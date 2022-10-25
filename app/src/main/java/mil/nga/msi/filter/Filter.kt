package mil.nga.msi.filter

import mil.nga.msi.datasource.filter.ComparatorType

data class Filter(
   val parameter: FilterParameter,
   val comparator: ComparatorType,
   val value: Any?
)