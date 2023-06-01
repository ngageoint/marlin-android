package mil.nga.msi.location

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.FeatureCollection
import mil.nga.sf.geojson.LineString
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Polygon

fun FeatureCollection.bounds(): LatLngBounds? {
   val builder = LatLngBounds.builder()
   features?.forEach { feature: Feature ->
      try {
         when (val geometry = feature.geometry) {
            is Point -> {
               builder.include(LatLng(geometry.point.y, geometry.point.x))
            }
            is LineString, is Polygon -> {
               val envelope = geometry.geometry.envelope
               builder.include(LatLng(envelope.minY, envelope.minX))
               builder.include(LatLng(envelope.maxY,  envelope.minX))
               builder.include(LatLng(envelope.maxY,  envelope.maxX))
               builder.include(LatLng(envelope.minY,  envelope.maxX))
            }
         }
      } catch (_: Exception) { }
   }

   return try { builder.build() } catch(e: Exception) { null }
}