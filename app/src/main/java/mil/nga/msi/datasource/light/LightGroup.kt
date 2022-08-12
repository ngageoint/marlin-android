package mil.nga.msi.datasource.light

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class LightGroup(
   @ColumnInfo(name = "section") val section: String,
   @Embedded val light: Light
)