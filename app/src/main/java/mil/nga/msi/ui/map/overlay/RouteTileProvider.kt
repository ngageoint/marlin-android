package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.GeometryType
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Polygon
import javax.inject.Inject

class RouteTileProvider @Inject constructor(
    val application: Application,
    val repository: TileRepository
) : DataSourceTileProvider(application, repository)

class RouteImage(
    override val feature: Feature
): DataSourceImage {
    override val dataSource = DataSource.ROUTE

    override fun image(
        context: Context,
        zoom: Int,
        tileBounds: Bounds,
        tileSize: Double
    ): List<Bitmap> {
        return when (feature.geometry.geometryType) {
            GeometryType.POINT -> {
                val radius = feature.properties["radius"].toString().toDoubleOrNull()
                if (radius == null) {
                    listOf(pointImage(context, zoom))
                } else {
                    listOf(circleImage(context, zoom, radius, feature.simpleGeometry.centroid))
                }
            }
            GeometryType.LINESTRING -> {
                val lineString = feature.geometry as LineString
                listOf(lineImage(context, lineString, zoom, tileBounds, tileSize))
            }
            GeometryType.POLYGON -> {
                val polygon = feature.geometry as Polygon
                listOf(polygonImage(context, polygon, zoom, tileBounds, tileSize))
            }
            else -> emptyList()
        }
    }
}