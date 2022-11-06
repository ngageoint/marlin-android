package mil.nga.msi.datasource.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class ModuFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(title = "Date", parameter ="date", type = FilterParameterType.DATE),
         FilterParameter(title = "Name", parameter ="name", type = FilterParameterType.STRING),
         FilterParameter(title = "Location", parameter = "location", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Latitude", parameter = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", parameter = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Region", parameter = "region", type = FilterParameterType.STRING),
         FilterParameter(title = "Subregion", parameter = "subregion", type = FilterParameterType.STRING),
         FilterParameter(title = "Distance", parameter = "distance", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Special Status", parameter = "special_status", type = FilterParameterType.STRING),
         FilterParameter(title = "Rig Status", parameter = "rig_status", type = FilterParameterType.STRING),
         FilterParameter(title = "Navigation Area", parameter = "navigation_area", type = FilterParameterType.STRING)
      )
   }
}