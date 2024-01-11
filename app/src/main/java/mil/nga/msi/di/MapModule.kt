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
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.map.*
import mil.nga.msi.repository.map.AsamsTileRepository
import mil.nga.msi.repository.map.DgpsStationsTileRepository
import mil.nga.msi.repository.map.LightsTileRepository
import mil.nga.msi.repository.map.ModusTileRepository
import mil.nga.msi.repository.map.PortsTileRepository
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
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

   @Singleton
   @Provides
   @Named("asamTileProvider")
   fun provideAsamTileProvider(application: Application, dataSource: AsamLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return AsamTileProvider(application, AsamsTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("moduTileProvider")
   fun provideModuTileProvider(application: Application, dataSource: ModuLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return ModuTileProvider(application, ModusTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("lightTileProvider")
   fun provideLightTileProvider(application: Application, dataSource: LightLocalDataSource, filterRepository: FilterRepository, mapRepository: MapRepository): TileProvider {
      return LightTileProvider(application, LightsTileRepository(dataSource, filterRepository, mapRepository))
   }

   @Singleton
   @Provides
   @Named("portTileProvider")
   fun providePortTileProvider(application: Application, dataSource: PortLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return PortTileProvider(application, PortsTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("radioBeaconTileProvider")
   fun provideRadioBeaconProvider(application: Application, dataSource: RadioBeaconLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return RadioBeaconTileProvider(application, RadioBeaconsTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("dgpsStationTileProvider")
   fun provideDgpsStationProvider(application: Application, dataSource: DgpsStationLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return DgpsStationTileProvider(application, DgpsStationsTileRepository(dataSource, filterRepository))
   }
}