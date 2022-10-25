package mil.nga.msi.ui.asam.filter

import mil.nga.msi.datasource.filter.ComparatorType

class Asam

data class AsamFilter(
   val parameter: AsamParameter,
   val comparator: ComparatorType,
   val value: Any?
)