package mil.nga.msi.datasource.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class LightFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(title = "Name", parameter ="name", type = FilterParameterType.STRING),
         FilterParameter(title = "Location", parameter = "location", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Latitude", parameter = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", parameter = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Feature Number", parameter = "feature_number", type = FilterParameterType.STRING),
         FilterParameter(title = "Volume Number", parameter = "volume_number", type = FilterParameterType.STRING),
         FilterParameter(title = "International Feature Number", parameter = "international_feature", type = FilterParameterType.STRING),
         FilterParameter(title = "Structure", parameter = "structure", type = FilterParameterType.STRING),
         FilterParameter(title = "Focal Plane Elevation (ft)", parameter = "height_feet", type = FilterParameterType.STRING),
         FilterParameter(title = "Focal Plane Elevation (ft)", parameter = "height_meters", type = FilterParameterType.STRING),
         FilterParameter(title = "Range (nm)", parameter = "range", type = FilterParameterType.STRING),
         FilterParameter(title = "Remarks", parameter = "remarks", type = FilterParameterType.STRING),
         FilterParameter(title = "Characteristic", parameter = "characteristic", type = FilterParameterType.STRING),
         FilterParameter(title = "Notice Number", parameter = "notice_number", type = FilterParameterType.INT),
         FilterParameter(title = "Notice Week", parameter = "notice_week", type = FilterParameterType.STRING),
         FilterParameter(title = "Notice Year", parameter = "notice_year", type = FilterParameterType.STRING),
         FilterParameter(title = "Preceding Note", parameter = "preceding_note", type = FilterParameterType.STRING),
         FilterParameter(title = "Post Note", parameter = "post_note", type = FilterParameterType.STRING)
      )
   }
}