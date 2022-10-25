package mil.nga.msi.ui.asam.filter

import mil.nga.msi.datasource.filter.ComparatorType

enum class ParameterType(val comparators: List<ComparatorType>) {
   STRING(listOf(ComparatorType.EQUALS, ComparatorType.NOT_EQUALS, ComparatorType.CONTAINS, ComparatorType.NOT_CONTAINS, ComparatorType.STARTS_WITH, ComparatorType.ENDS_WITH)),
   DATE(listOf(ComparatorType.WITHIN, ComparatorType.EQUALS, ComparatorType.NOT_EQUALS, ComparatorType.GREATER_THAN, ComparatorType.GREATER_THAN_OR_EQUAL, ComparatorType.LESS_THAN, ComparatorType.LESS_THAN_OR_EQUAL)),
   INT(listOf()),
   FLOAT(listOf()),
   DOUBLE(listOf(ComparatorType.EQUALS, ComparatorType.NOT_EQUALS, ComparatorType.GREATER_THAN, ComparatorType.GREATER_THAN_OR_EQUAL, ComparatorType.LESS_THAN, ComparatorType.LESS_THAN_OR_EQUAL)),
   BOOLEAN(listOf()),
   ENUMERATION(listOf()),
   LOCATION(listOf(ComparatorType.CLOSE_TO, ComparatorType.NEAR_ME))
}

data class AsamParameter(
   val type: ParameterType,
   val title: String,
   val name: String,
)

class AsamParameters() {
   val parameters: List<AsamParameter> = mutableListOf<AsamParameter>().apply {
      add(AsamParameter(title = "Date", name = "date", type = ParameterType.DATE))
      add(AsamParameter(title = "Location", name = "date", type = ParameterType.LOCATION))
      add(AsamParameter(title = "Reference", name = "reference", type = ParameterType.STRING))
      add(AsamParameter(title = "Latitude", name = "latitude", type = ParameterType.DOUBLE))
      add(AsamParameter(title = "Longitude", name = "longitude", type = ParameterType.DOUBLE))
      add(AsamParameter(title = "Navigation Area", name = "navigation_area", type = ParameterType.STRING))
      add(AsamParameter(title = "Subregion", name = "subregion", type = ParameterType.STRING))
      add(AsamParameter(title = "Description", name = "description", type = ParameterType.STRING))
      add(AsamParameter(title = "Hostility", name = "hostility", type = ParameterType.STRING))
      add(AsamParameter(title = "Victim", name ="victim", type = ParameterType.STRING))
   }
}