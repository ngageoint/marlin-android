package mil.nga.msi.geopackage.export.definition

import mil.nga.color.Color
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.geopackage.features.user.FeatureTable
import mil.nga.geopackage.geom.GeoPackageGeometryData
import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightColor
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.overlay.images.coordinates
import mil.nga.msi.ui.map.overlay.images.sectorCoordinates
import mil.nga.sf.Geometry
import mil.nga.sf.GeometryCollection
import mil.nga.sf.LineString
import mil.nga.sf.Point
import mil.nga.sf.Polygon

private const val METERS_IN_NAUTICAL_MILE = 1852

class LightDefinition : DataSourceDefinition {
   override val tableName = "lights"
   override val icon = MapAnnotation.Type.LIGHT.icon
   override val color = DataSource.LIGHT.color
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("name", "Name", GeoPackageDataType.TEXT),
      FeatureColumn("latitude", "Latitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("longitude", "Longitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("position", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("characteristic", "Characteristic", GeoPackageDataType.TEXT),
      FeatureColumn("characteristicNumber", "Characteristic Number", GeoPackageDataType.INT),
      FeatureColumn("volumeNumber", "Volume Number", GeoPackageDataType.TEXT),
      FeatureColumn("featureNumber", "Feature Number", GeoPackageDataType.TEXT),
      FeatureColumn("noticeNumber", "Notice Number", GeoPackageDataType.INT),
      FeatureColumn("noticeWeek", "Notice Week", GeoPackageDataType.TEXT),
      FeatureColumn("noticeYear", "Notice Year", GeoPackageDataType.TEXT),
      FeatureColumn("aidType", "Aid Type", GeoPackageDataType.TEXT),
      FeatureColumn("geopoliticalHeading", "Geopolitical Heading", GeoPackageDataType.TEXT),
      FeatureColumn("regionHeading", "Region Heading", GeoPackageDataType.TEXT),
      FeatureColumn("subRegionHeading", "Subregion Heading", GeoPackageDataType.TEXT),
      FeatureColumn("localHeading", "Local Heading", GeoPackageDataType.TEXT),
      FeatureColumn("precedingNote", "Preceding Note", GeoPackageDataType.TEXT),
      FeatureColumn("range", "Range", GeoPackageDataType.TEXT),
      FeatureColumn("heightFeet", "Height Feet", GeoPackageDataType.FLOAT),
      FeatureColumn("heightMeters", "Height Meters", GeoPackageDataType.FLOAT),
      FeatureColumn("internationalFeature", "International Feature", GeoPackageDataType.TEXT),
      FeatureColumn("remarks", "Remarks", GeoPackageDataType.TEXT),
      FeatureColumn("structure", "Structure", GeoPackageDataType.TEXT),
      FeatureColumn("postNote", "Post Note", GeoPackageDataType.TEXT),
      FeatureColumn("deleteFlag", "Delete Flag", GeoPackageDataType.TEXT),
      FeatureColumn("removeFromList", "Remove From List", GeoPackageDataType.TEXT)
   )

   override fun getStyles(tableStyles: FeatureTableStyles): List<StyleRow> {
      val styleRows = mutableListOf<StyleRow>()

      tableStyles.styleDao.newRow().apply {
         name = "RedLightStyle"
         color = Color(LightColor.RED.color.red, LightColor.RED.color.green, LightColor.RED.color.blue)
         fillColor = Color(LightColor.RED.color.red, LightColor.RED.color.green, LightColor.RED.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "GreenLightStyle"
         color = Color(LightColor.GREEN.color.red, LightColor.GREEN.color.green, LightColor.GREEN.color.blue)
         fillColor = Color(LightColor.GREEN.color.red, LightColor.GREEN.color.green, LightColor.GREEN.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "BlueLightStyle"
         color = Color(LightColor.BLUE.color.red, LightColor.BLUE.color.green, LightColor.BLUE.color.blue)
         fillColor = Color(LightColor.BLUE.color.red, LightColor.BLUE.color.green, LightColor.BLUE.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "WhiteLightStyle"
         color = Color(LightColor.WHITE.color.red, LightColor.WHITE.color.green, LightColor.WHITE.color.blue)
         fillColor = Color(LightColor.WHITE.color.red, LightColor.WHITE.color.green, LightColor.WHITE.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "YellowLightStyle"
         color = Color(LightColor.YELLOW.color.red, LightColor.YELLOW.color.green, LightColor.YELLOW.color.blue)
         fillColor = Color(LightColor.YELLOW.color.red, LightColor.YELLOW.color.green, LightColor.YELLOW.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "VioletLightStyle"
         color = Color(LightColor.VIOLET.color.red, LightColor.VIOLET.color.green, LightColor.VIOLET.color.blue)
         fillColor = Color(LightColor.VIOLET.color.red, LightColor.VIOLET.color.green, LightColor.VIOLET.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      tableStyles.styleDao.newRow().apply {
         name = "OrangeLightStyle"
         color = Color(LightColor.ORANGE.color.red, LightColor.ORANGE.color.green, LightColor.ORANGE.color.blue)
         fillColor = Color(LightColor.ORANGE.color.red, LightColor.ORANGE.color.green, LightColor.ORANGE.color.blue)
         fillOpacity = 0.3
         width = 2.0
      }?.let { styleRows.add(it) }

      return styleRows
   }
}

class LightFeature(
   private val light: Light
) : Feature {
   override val geometry: Geometry
      get() {
         val geometries = geometryByColor()
         return if (geometries.isNotEmpty()) {
            val collection = GeometryCollection<Geometry>()
            geometries.values.forEach {
               collection.addGeometry(it)
            }
            collection
         } else Point(light.longitude, light.latitude)
      }

   override val values = listOf(
      FeatureData("name", light.name),
      FeatureData("latitude", light.latitude),
      FeatureData("longitude", light.longitude),
      FeatureData("position", MGRS.from(light.longitude, light.latitude).coordinate()),
      FeatureData("characteristic", light.characteristic),
      FeatureData("characteristicNumber", light.characteristicNumber),
      FeatureData("volumeNumber", light.volumeNumber),
      FeatureData("featureNumber", light.featureNumber),
      FeatureData("noticeNumber", light.noticeNumber),
      FeatureData("noticeWeek", light.noticeWeek),
      FeatureData("noticeYear", light.noticeYear),
      FeatureData("aidType", light.aidType),
      FeatureData("geopoliticalHeading", light.geopoliticalHeading),
      FeatureData("regionHeading", light.regionHeading),
      FeatureData("subregionHeading", light.subregionHeading),
      FeatureData("localHeading", light.localHeading),
      FeatureData("precedingNote", light.precedingNote),
      FeatureData("range", light.range),
      FeatureData("heightFeet", light.heightFeet),
      FeatureData("heightMeters", light.heightMeters),
      FeatureData("internationalFeature", light.internationalFeature),
      FeatureData("remarks", light.remarks),
      FeatureData("structure", light.structure),
      FeatureData("postNote", light.postNote),
      FeatureData("deleteFlag", light.deleteFlag),
      FeatureData("removeFromList", light.removeFromList)
   )

   override fun createFeature(
      geoPackage: GeoPackage,
      table: FeatureTable,
      styleRows: List<StyleRow>
   ) {
      val featureDao = geoPackage.getFeatureDao(table)
      val featureTableStyles = FeatureTableStyles(geoPackage, table)
      val geometryColors = geometryByColor()
      featureTableStyles.createStyleRelationship()

      if (geometryColors.isNotEmpty()) {
         geometryColors.forEach { (color, geometry) ->
            val row = featureDao.newRow()
            row.setValue("geometry", GeoPackageGeometryData(geometry))

            values.forEach { (columnName, value) ->
               row.setValue(columnName, value)
            }

            val rowId = featureDao.create(row)
            when (color) {
               LightColor.RED.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[0])
               }
               LightColor.GREEN.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[1])
               }
               LightColor.BLUE.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[2])
               }
               LightColor.WHITE.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[3])
               }
               LightColor.YELLOW.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[4])
               }
               LightColor.VIOLET.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[5])
               }
               LightColor.ORANGE.color -> {
                  featureTableStyles.setStyleDefault(rowId, styleRows[6])
               }
               else -> {}
            }
         }
      } else {
         val row = featureDao.newRow()
         row.setValue("geometry", GeoPackageGeometryData(geometry))
         values.forEach { (columnName, value) ->
            row.setValue(columnName, value)
         }
         featureDao.create(row)
      }
   }

   private fun geometryByColor(): Map<androidx.compose.ui.graphics.Color, Geometry> {
      val geometries = mutableMapOf<androidx.compose.ui.graphics.Color, Geometry>()

      val sectors = light.lightSectors
      if (sectors.isNotEmpty()) {
         val sectorsByColor = sectors.groupBy { it.color }
         sectorsByColor.forEach { (color, sectors) ->
            val collection = GeometryCollection<Geometry>()
            sectors
               .asSequence()
               .filterNot { it.obscured }
               .filterNot {
                  // this could be an error in the data, or sometimes lights are defined as follows:
                  // characteristic Q.W.R.
                  // remarks R. 289°-007°, W.-007°.
                  // that would mean this light flashes between red and white over those angles
                  // TODO: figure out what to do with multi colored lights over the same sector
                  it.startDegrees >= it.endDegrees
               }
               .forEach { sector ->
                  val nauticalMiles = sector.range ?: 0.0
                  val nauticalMilesMeasurement = nauticalMiles * METERS_IN_NAUTICAL_MILE

                  val circleCoordinates = sectorCoordinates(
                     light.latLng,
                     range = nauticalMilesMeasurement,
                     startDegrees = sector.startDegrees + 180.0,
                     endDegrees = sector.endDegrees + 180.0
                  )
                  val ring = LineString()
                  ring.addPoint(Point(light.longitude, light.latitude))
                  circleCoordinates.forEach { coordinate ->
                     ring.addPoint(Point(coordinate.longitude, coordinate.latitude))
                  }

                  collection.addGeometry(Polygon(ring))
               }

            geometries[color] = collection
         }
      } else {
         val colors = light.lightColors
         val range = light.range?.toDoubleOrNull()
         if (range != null && colors.isNotEmpty()) {
            val nauticalMilesMeasurement = range * METERS_IN_NAUTICAL_MILE

            val circleCoordinates = coordinates(light.latLng, radiusInMeters = nauticalMilesMeasurement)
            val ring = LineString()
            ring.addPoint(Point(light.longitude, light.latitude))
            circleCoordinates.forEach { coordinate ->
               ring.addPoint(Point(coordinate.longitude, coordinate.latitude))
            }

            geometries[colors.first()] = Polygon(ring)
         }
      }

      return geometries
   }
}