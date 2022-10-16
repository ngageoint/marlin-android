package mil.nga.msi.datasource.light

import androidx.compose.ui.graphics.Color

data class LightSector(
   val startDegrees: Double,
   val endDegrees: Double,
   val range: Double? = null,
   val color: Color,
   val text: String? = null,
   var obscured: Boolean = false,
   val characteristicNumber: Int? = null
)