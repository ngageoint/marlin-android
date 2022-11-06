package mil.nga.msi.datasource.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class AsamFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(title = "Date", parameter = "date", type = FilterParameterType.DATE),
         FilterParameter(title = "Location", parameter = "date", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Reference", parameter = "reference", type = FilterParameterType.STRING),
         FilterParameter(title = "Latitude", parameter = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", parameter = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Navigation Area", parameter = "navigation_area", type = FilterParameterType.STRING),
         FilterParameter(title = "Subregion", parameter = "subregion", type = FilterParameterType.STRING),
         FilterParameter(title = "Description", parameter = "description", type = FilterParameterType.STRING),
         FilterParameter(title = "Hostility", parameter = "hostility", type = FilterParameterType.STRING),
         FilterParameter(title = "Victim", parameter ="victim", type = FilterParameterType.STRING)
      )
   }
}