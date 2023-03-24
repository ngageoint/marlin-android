package mil.nga.msi.datasource.layer

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class LayerType {
   XYZ, TMS, WMS, GEOPACKAGE
}

@Entity(tableName = "layers")
@Parcelize
@kotlinx.serialization.Serializable
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

   @ColumnInfo(name = "visible")
   var visible: Boolean = true,

   @ColumnInfo(name = "min_zoom")
   var minZoom: Int? = null,

   @ColumnInfo(name = "max_zoom")
   var maxZoom: Int? = null,

   @ColumnInfo(name = "file_path")
   var filePath: String? = null,

   @ColumnInfo(name = "tables")
   var tables: List<String> = mutableListOf(),

   @ColumnInfo(name = "refresh_rate")
   var refreshRate: Int? = null
): Parcelable