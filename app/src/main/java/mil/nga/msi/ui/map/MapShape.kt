package mil.nga.msi.ui.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Polygon

sealed class MapShape(
   var tag: String? = null
) {
   fun center(): LatLng {
      return when (this) {
         is PointShape -> latLng
         is CircleShape -> center
         is PolylineShape, is PolygonShape -> {
            val points = if (this is PolylineShape) points else if (this is PolygonShape) points else emptyList()
            val builder = LatLngBounds.Builder()
            points.forEach { builder.include(it) }
            val bounds = builder.build()
            return bounds.center
         }
      }
   }

   companion object {
      fun fromGeometry(feature: Feature, tag: String? = null): MapShape? {
         return when (val geometry = feature.geometry) {
            is Point -> {
               val radius = feature.properties["radius"]?.toString()?.toDoubleOrNull()
               if (radius == null) {
                  PointShape.fromGeometry(geometry).apply { this.tag = tag }
               } else {
                  CircleShape.fromGeometry(geometry, radius).apply { this.tag = tag }
               }
            }
            is LineString -> PolylineShape.fromGeometry(geometry).apply { this.tag = tag }
            is Polygon -> PolygonShape.fromGeometry(geometry).apply { this.tag = tag }
            else -> null
         }
      }
   }
}

data class PointShape(
   val latLng: LatLng
): MapShape() {
   companion object {
      fun fromGeometry(point: Point): PointShape {
         return PointShape(LatLng(point.position.y, point.position.x))
      }
   }
}

data class CircleShape(
   val center: LatLng,
   val radius: Double
): MapShape() {
   companion object {
      fun fromGeometry(point: Point, radius: Double): CircleShape {
         return CircleShape(LatLng(point.position.y, point.position.x), radius)
      }
   }
}

data class PolylineShape(
   val points: List<LatLng>
): MapShape() {
   companion object {
      fun fromGeometry(lineString: LineString): PolylineShape {
         return PolylineShape(lineString.coordinates.map { LatLng(it.y, it.x) })
      }
   }
}

data class PolygonShape(
   val points: List<LatLng>
): MapShape() {
   companion object {
      fun fromGeometry(polygon: Polygon): PolygonShape {
         return PolygonShape(polygon.rings.first().coordinates.map { LatLng(it.y, it.x) })
      }
   }
}