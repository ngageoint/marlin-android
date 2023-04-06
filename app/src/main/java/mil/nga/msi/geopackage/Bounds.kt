package mil.nga.msi.geopackage

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.geopackage.tiles.user.TileDao
import mil.nga.sf.proj.GeometryTransform

fun TileDao.latLngBounds(): LatLngBounds {
   return contents.boundingBox
      .transform(GeometryTransform.create(projection, 4326))
      .latLngBounds()
}

fun FeatureDao.latLngBounds(): LatLngBounds {
   return contents.boundingBox
      .transform(GeometryTransform.create(projection, 4326))
      .latLngBounds()
}

fun BoundingBox.latLngBounds(): LatLngBounds {
   return LatLngBounds(
      LatLng(minLatitude, minLongitude),
      LatLng(maxLatitude, maxLongitude)
   )
}