package mil.nga.msi.ui.light

import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark

data class LightState(
   val lightWithBookmark: LightWithBookmark,
   val characteristics: List<Light>
)