package mil.nga.msi.di

import android.app.Application
import com.google.android.gms.maps.model.TileProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.gars.tile.GARSTileProvider
import mil.nga.mgrs.tile.MGRSTileProvider
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.ui.map.overlay.*
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MapModule {
   @Singleton
   @Provides
   @Named("osmTileProvider")
   fun provideOSMTileProvider(
      layerService: LayerService
   ): TileProvider {
      return OsmTileProvider(layerService)
   }

   @Singleton
   @Provides
   @Named("mgrsTileProvider")
   fun provideMgrsTileProvider(application: Application): TileProvider {
      return MGRSTileProvider.create(application)
   }

   @Singleton
   @Provides
   @Named("garsTileProvider")
   fun provideGarsTileProvider(application: Application): TileProvider {
      return GARSTileProvider.create(application)
   }
}