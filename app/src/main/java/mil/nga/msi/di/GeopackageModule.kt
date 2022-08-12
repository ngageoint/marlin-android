package mil.nga.msi.di

import android.app.Application
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.TileProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles
import mil.nga.msi.R
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GeopackageModule {

   @Provides
   @Singleton
   fun provideGeoPackageManager(application: Application): GeoPackageManager {
      return GeoPackageFactory.getManager(application.applicationContext)
   }

   @Singleton
   @Provides
   @Named("naturalEarth_1_100")
   fun provideNaturalEarthLowResTileProvider(application: Application, geopackageManager: GeoPackageManager): TileProvider {
      val resource = application.resources.openRawResource(R.raw.natural_earth_1_100)
      try { geopackageManager.importGeoPackage("natural_earth_1_100", resource) } catch (e: Exception) { }
      val database = geopackageManager.databasesLike("natural_earth_1_100").firstOrNull()
      val geopackage = geopackageManager.open(database)
      val features: List<String> = geopackage.featureTables
      val featureTable: String = features[0]
      val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
      val featureTiles = DefaultFeatureTiles(application, geopackage, featureDao)
      featureTiles.isFillPolygon = true
      featureTiles.setLinePaint(Paint().apply { color = ContextCompat.getColor(application, R.color.geopackage_land_light_theme) })
      featureTiles.setPolygonFillPaint(Paint().apply { color = ContextCompat.getColor(application, R.color.geopackage_land_light_theme) })
      return FeatureOverlay(featureTiles)
   }

   @Singleton
   @Provides
   @Named("navigationAreas")
   fun provideNavigationAreaTileProvider(application: Application, geopackageManager: GeoPackageManager): TileProvider {
      val resource = application.resources.openRawResource(R.raw.navigation_areas)
      try { geopackageManager.importGeoPackage("navigation_areas", resource) } catch (e: Exception) { }
      val database = geopackageManager.databasesLike("navigation_areas").firstOrNull()
      val geopackage = geopackageManager.open(database)
      val features: List<String> = geopackage.featureTables
      val featureTable: String = features[0]
      val featureDao: FeatureDao = geopackage.getFeatureDao(featureTable)
      val featureTiles = DefaultFeatureTiles(application, geopackage, featureDao)
      return FeatureOverlay(featureTiles)
   }
}