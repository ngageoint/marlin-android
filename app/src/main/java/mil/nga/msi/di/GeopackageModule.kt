package mil.nga.msi.di

import android.app.Application
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.GeoPackageManager
import mil.nga.msi.R
import mil.nga.msi.ui.map.overlay.GeoPackageTileProvider
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
   @Named("lowResolution")
   fun provideNaturalEarthLowResTileProvider(application: Application, geoPackageManager: GeoPackageManager): GeoPackageTileProvider {
      return GeoPackageTileProvider(
         geopackageManager = geoPackageManager,
         context = application.applicationContext,
         resourceId = R.raw.natural_earth_1_100,
         name ="natural_earth_1_100",
         polygonLineColor = ContextCompat.getColor(application, R.color.geopackage_land_light_theme),
         polygonFillColor = ContextCompat.getColor(application, R.color.geopackage_land_light_theme)
      )
   }

//   @Provides
//   @Singleton
//   @Named("highResolution")
//   fun provideNaturalEarthHighResTileProvider(application: Application): TileProvider {
//      return TileOverlayOptions().tileProvider(GeoPackageTileProvider(application, R.raw.natural_earth_1_10)).tileProvider!!
//   }

   @Singleton
   @Provides
   fun provideNavigationAreaTileProvider(application: Application, geoPackageManager: GeoPackageManager): GeoPackageTileProvider {
      return GeoPackageTileProvider(
         geopackageManager = geoPackageManager,
         context = application.applicationContext,
         resourceId = R.raw.navigation_areas,
         name ="navigation_areas"
      )
   }
}