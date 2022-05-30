package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo
import java.util.*

data class AsamListItem(
   @ColumnInfo(name = "reference") val id: String,
   @ColumnInfo(name = "date") val date: Date,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "hostility") val hostility: String?,
   @ColumnInfo(name = "victim") val victim: String?,
   @ColumnInfo(name = "description") val description: String?
)