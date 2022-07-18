package mil.nga.msi.ui.map.overlay

import android.content.Context
import android.graphics.Paint
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles
import mil.nga.geopackage.tiles.features.FeatureTiles

class GeoPackageTileProvider(
   geopackageManager: GeoPackageManager,
   context: Context,
   resourceId: Int,
   name: String,
   polygonLineColor: Int? = null,
   polygonFillColor: Int? = null
): TileProvider {

   private var featureTiles: FeatureTiles

   init {
      val resource = context.resources.openRawResource(resourceId)
      try { geopackageManager.importGeoPackage(name, resource) } catch (e: Exception) { }
      val database = geopackageManager.databasesLike(name).firstOrNull()
      val geopackage = geopackageManager.open(database)
      val features: List<String> = geopackage.featureTables
      val featureTable: String = features[0]
      val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
      featureTiles = DefaultFeatureTiles(context, geopackage, featureDao)

      polygonLineColor?.let {
         featureTiles.setLinePaint(Paint().apply { color = it })
      }

      polygonFillColor?.let {
         featureTiles.isFillPolygon = true
         featureTiles.setPolygonFillPaint(Paint().apply { color = it })
      }
   }

   override fun getTile(x: Int, y: Int, z: Int): Tile? {
      val bytes = featureTiles.drawTileBytes(x, y, z)
      return bytes?.let { Tile(256, 256, it) }
   }
}