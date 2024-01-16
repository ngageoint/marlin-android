package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo
import mil.nga.sf.geojson.FeatureCollection
import mil.nga.sf.geojson.FeatureConverter

class RouteMapItem(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "geoJson") val geoJson: String?
) {
    fun featureCollection(): FeatureCollection? = geoJson?.let { FeatureConverter.toFeatureCollection(it) }
}