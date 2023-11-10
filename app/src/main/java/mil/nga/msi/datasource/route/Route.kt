package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.Date

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "createdTime")
    val createdTime: Date,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "updatedTime")
    var updatedTime: Date
) {
    constructor(createdTime: Date, name: String, updatedTime: Date) : this(0, createdTime, name, updatedTime)

    @ColumnInfo(name = "distanceMeters")
    var distanceMeters: Double? = null

    @ColumnInfo(name = "geoJson")
    var geoJson: String? = null

    @ColumnInfo(name = "maxLatitude")
    var maxLatitude: Double? = null

    @ColumnInfo(name = "maxLongitude")
    var maxLongitude: Double? = null

    @ColumnInfo(name = "minLatitude")
    var minLatitude: Double? = null

    @ColumnInfo(name = "minLongitude")
    var minLongitude: Double? = null

    fun bounds(): LatLngBounds? {
        val builder = LatLngBounds.builder()

        val minY = minLatitude
        val minX = minLongitude
        if (minY != null && minX != null) {
            builder.include(LatLng(minY, minX))
        }

        val maxY = maxLatitude
        val maxX = maxLongitude
        if (maxY != null && maxX != null) {
            builder.include(LatLng(maxY, maxX))
        }

        return try { builder.build() } catch(e: Exception) { null }
    }
}
