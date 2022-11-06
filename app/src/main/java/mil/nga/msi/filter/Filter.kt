package mil.nga.msi.filter

data class Filter(
   val parameter: FilterParameter,
   val comparator: ComparatorType,
   val value: Any?
)