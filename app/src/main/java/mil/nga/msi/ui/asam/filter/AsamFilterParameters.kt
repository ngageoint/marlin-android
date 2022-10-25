package mil.nga.msi.ui.asam.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class AsamFilterParameters() {
   val parameters: List<FilterParameter> = mutableListOf<FilterParameter>().apply {
      add(FilterParameter(title = "Date", name = "date", type = FilterParameterType.DATE))
      add(FilterParameter(title = "Location", name = "date", type = FilterParameterType.LOCATION))
      add(FilterParameter(title = "Reference", name = "reference", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Latitude", name = "latitude", type = FilterParameterType.DOUBLE))
      add(FilterParameter(title = "Longitude", name = "longitude", type = FilterParameterType.DOUBLE))
      add(FilterParameter(title = "Navigation Area", name = "navigation_area", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Subregion", name = "subregion", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Description", name = "description", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Hostility", name = "hostility", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Victim", name ="victim", type = FilterParameterType.STRING))
   }
}