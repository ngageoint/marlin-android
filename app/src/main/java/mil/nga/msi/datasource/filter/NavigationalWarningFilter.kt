package mil.nga.msi.datasource.filter

import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.ui.export.ExportDataSource

class NavigationalWarningFilter {
   companion object {
      val parameters = listOf(
         FilterParameter(
            title = "Navigation Area",
            parameter = "navigation_area",
            type = FilterParameterType.ENUMERATION,
            enumerationValues = NavigationArea.values().toList()
         )
      )
   }
}