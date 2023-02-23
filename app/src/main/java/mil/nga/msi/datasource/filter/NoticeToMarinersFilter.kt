package mil.nga.msi.datasource.filter

import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType

class NoticeToMarinersFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(title = "Location", parameter = "location", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Notice Number", parameter = "noticeNumber", type = FilterParameterType.INT),
      )
   }
}