package mil.nga.msi.ui.modu.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class ModuFilterParameters() {
   val parameters: List<FilterParameter> = mutableListOf<FilterParameter>().apply {
      add(FilterParameter(title = "Date", name ="date", type = FilterParameterType.DATE))
      add(FilterParameter(title = "Name", name ="name", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Location", name = "location", type = FilterParameterType.LOCATION))
      add(FilterParameter(title = "Latitude", name = "latitude", type = FilterParameterType.DOUBLE))
      add(FilterParameter(title = "Longitude", name = "longitude", type = FilterParameterType.DOUBLE))
      add(FilterParameter(title = "Region", name = "region", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Subregion", name = "subregion", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Distance", name = "distance", type = FilterParameterType.DOUBLE))
      add(FilterParameter(title = "Special Status", name = "special_status", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Rig Status", name = "rig_status", type = FilterParameterType.STRING))
      add(FilterParameter(title = "Navigation Area", name = "navigation_area", type = FilterParameterType.STRING))
   }
}