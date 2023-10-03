package mil.nga.msi.geopackage.export

import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.asam.Asam
import mil.nga.sf.Point

class AsamFeature(
   asam: Asam
) : Feature {
   override val geometry = Point(asam.longitude, asam.latitude)

   override val properties = mapOf<String, Any?>(
      "date" to asam.date,
      "location" to MGRS.from(asam.longitude, asam.latitude).coordinate(),
      "reference" to asam.reference,
      "latitude" to  asam.latitude,
      "longitude" to  asam.longitude,
      "navigation_area" to asam.navigationArea,
      "subregion" to asam.subregion,
      "description" to asam.description,
      "hostility" to asam.hostility,
      "victim" to asam.victim
   ).mapNotNull { (key, value) -> value?.let { key to it } }.toMap()
}