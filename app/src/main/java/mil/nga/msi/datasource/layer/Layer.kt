package mil.nga.msi.datasource.layer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LayerType {
   XYZ, TMS, WMS, GEOPACKAGE
}

@Entity(tableName = "layers")
data class Layer(
   @PrimaryKey(autoGenerate = true)
   @ColumnInfo(name = "id")
   val id: Long = 0,

   @ColumnInfo(name = "type")
   val type: LayerType,

   @ColumnInfo(name = "url")
   val url: String,

   @ColumnInfo(name = "name")
   val name: String,

   @ColumnInfo(name = "display_name")
   val displayName: String,

   @ColumnInfo(name = "visible")
   var visible: Boolean = true
) {
   @ColumnInfo(name = "group_name")
   var groupName: String? = null

   @ColumnInfo(name = "min_zoom")
   var minZoom: Int? = null

   @ColumnInfo(name = "max_zoom")
   var maxZoom: Int? = null

   @ColumnInfo(name = "file_path")
   var filePath: String? = null

   @ColumnInfo(name = "refresh_rate")
   var refreshRate: Int? = null
}