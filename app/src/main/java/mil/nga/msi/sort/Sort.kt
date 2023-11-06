package mil.nga.msi.sort

import mil.nga.msi.filter.FilterParameter

enum class SortDirection(val title: String) {
   ASC(title = "Ascending"),
   DESC(title = "Descending")
}

data class SortParameter(
   val parameter: FilterParameter,
   val direction: SortDirection
)

data class Sort(
   val section: Boolean,
   val parameters: List<SortParameter>
)