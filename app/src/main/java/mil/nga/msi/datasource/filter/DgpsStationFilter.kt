package mil.nga.msi.datasource.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class DgpsStationFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(title = "Name", parameter = "name", type = FilterParameterType.STRING),
         FilterParameter(title = "Location", parameter = "date", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Latitude", parameter = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", parameter = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Number", parameter = "feature_number", type = FilterParameterType.FLOAT),
         FilterParameter(title = "Geopolitical Heading", parameter = "geopolitical_heading", type = FilterParameterType.STRING),
         FilterParameter(title = "Station ID", parameter = "station_id", type = FilterParameterType.STRING),
         FilterParameter(title = "Range (nm)", parameter = "range", type = FilterParameterType.INT),
         FilterParameter(title = "Frequency (kHz)", parameter ="frequency", type = FilterParameterType.FLOAT),
         FilterParameter(title = "Transfer Rate", parameter ="transfer_rate", type = FilterParameterType.INT),
         FilterParameter(title = "Remarks", parameter ="remarks", type = FilterParameterType.STRING),
         FilterParameter(title = "Notice Number", parameter ="notice_number", type = FilterParameterType.INT),
         FilterParameter(title = "Notice Week", parameter ="notice_week", type = FilterParameterType.STRING),
         FilterParameter(title = "Notice Year", parameter ="notice_year", type = FilterParameterType.STRING),
         FilterParameter(title = "Volume Number", parameter ="volume_number", type = FilterParameterType.STRING),
         FilterParameter(title = "Preceding Note", parameter ="preceding_note", type = FilterParameterType.STRING),
         FilterParameter(title = "Post Note", parameter ="post_note", type = FilterParameterType.STRING),
         FilterParameter(title = "Aid Type", parameter ="aid_type", type = FilterParameterType.STRING),
         FilterParameter(title = "Region Heading", parameter ="region_heading", type = FilterParameterType.STRING),
         FilterParameter(title = "Remove From List", parameter ="remove_from_list", type = FilterParameterType.STRING),
         FilterParameter(title = "Delete Flag", parameter ="delete_flag", type = FilterParameterType.STRING)
      )
   }
}