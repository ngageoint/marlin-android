package mil.nga.msi.datasource.light

import androidx.compose.ui.graphics.Color

data class LightSector(
   val startDegrees: Double,
   val endDegrees: Double,
   val color: Color,
   val text: String
)