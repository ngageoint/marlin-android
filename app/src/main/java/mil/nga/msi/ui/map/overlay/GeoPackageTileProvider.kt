package mil.nga.msi.ui.map.overlay

import android.content.Context
import android.graphics.Paint
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles
import mil.nga.geopackage.tiles.features.FeatureTiles

class GeoPackageTileProvider(
   context: Context,
   resourceId: Int,
   name: String,
   polygonColor: Int = android.R.color.black,
   polygonFillColor: Int = android.R.color.black
): TileProvider {

   private var featureTiles: FeatureTiles

   init {
      val geopackageManager = GeoPackageFactory.getManager(context)
      val resource = context.resources.openRawResource(resourceId)
      try { geopackageManager.importGeoPackage(name, resource) } catch (e: Exception) { }
      val database = geopackageManager.databasesLike(name).firstOrNull()
      val geopackage = geopackageManager.open(database)
      val features: List<String> = geopackage.featureTables
      val featureTable: String = features[0]
      val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
      featureTiles = DefaultFeatureTiles(context, featureDao, context.resources.displayMetrics.density)
      featureTiles.setPolygonPaint(Paint().apply { color = polygonColor })
      featureTiles.setPolygonFillPaint(Paint().apply { color = polygonFillColor })
   }

   override fun getTile(x: Int, y: Int, z: Int): Tile {
      val bytes = featureTiles.drawTileBytes(x, y, z)
      return Tile(256, 256, bytes)
   }
}