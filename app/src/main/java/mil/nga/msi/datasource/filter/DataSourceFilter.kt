package mil.nga.msi.datasource.filter

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

enum class DataSourcePropertyType {
   STRING,
   DATE,
   INT,
   FLOAT,
   DOUBLE,
   BOOLEAN,
   ENUMERATION,
   LOCATION
}

data class DataSourceProperty(
   val id: String,
   val name: String,
   val key: String,
   val type: DataSourcePropertyType,
   val enumerations: List<List<String>>
)

interface DataSourceFilter {
   val properties: List<DataSourceProperty>
   val isMappable: Boolean
   val dataSourceName: String
   val fullDataSourceName: String
   val key: String
   val color: Color
   val imageName: String
   val coordinate: LatLng
}