package mil.nga.msi.geopackage.export

import mil.nga.sf.Geometry

interface Feature {
   val geometry: Geometry
   val properties: Map<String, Any>
}