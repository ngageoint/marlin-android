package mil.nga.msi.datasource.bookmark

import androidx.room.ColumnInfo
import androidx.room.Entity
 import mil.nga.msi.datasource.DataSource
import java.util.Date

@Entity(
   tableName = "bookmarks",
   primaryKeys = ["id", "data_source"]
)
data class Bookmark(
   @ColumnInfo(name = "id")
   val id: String,

   @ColumnInfo(name = "data_source")
   val dataSource: DataSource,

   @ColumnInfo(name = "timestamp")
   var date: Date = Date(),

   @ColumnInfo(name = "notes")
   var notes: String? = null
)