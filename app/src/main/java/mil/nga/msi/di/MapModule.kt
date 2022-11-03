package mil.nga.msi.di

import android.app.Application
import com.google.android.gms.maps.model.TileProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.gars.tile.GARSTileProvider
import mil.nga.mgrs.tile.MGRSTileProvider
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.map.*
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
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
   fun provideOSMTileProvider(): TileProvider {
      return OsmTileProvider()
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
      return AsamTileProvider(application, AsamTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("moduTileProvider")
   fun provideModuTileProvider(application: Application, dataSource: ModuLocalDataSource, filterRepository: FilterRepository): TileProvider {
      return ModuTileProvider(application, ModuTileRepository(dataSource, filterRepository))
   }

   @Singleton
   @Provides
   @Named("lightTileProvider")
   fun provideLightTileProvider(application: Application, dataSource: LightLocalDataSource, filterRepository: FilterRepository, userPreferencesRepository: UserPreferencesRepository): TileProvider {
      return LightTileProvider(application, LightTileRepository(dataSource, filterRepository, userPreferencesRepository))
   }

   @Singleton
   @Provides
   @Named("portTileProvider")
   fun providePortTileProvider(application: Application, dataSource: PortLocalDataSource): TileProvider {
      return PortTileProvider(application, PortTileRepository(dataSource))
   }

   @Singleton
   @Provides
   @Named("radioBeaconTileProvider")
   fun provideRadioBeaconProvider(application: Application, dataSource: RadioBeaconLocalDataSource): TileProvider {
      return RadioBeaconTileProvider(application, RadioBeaconTileRepository(dataSource))
   }

   @Singleton
   @Provides
   @Named("dgpsStationTileProvider")
   fun provideDgpsStationProvider(application: Application, dataSource: DgpsStationLocalDataSource): TileProvider {
      return DgpsStationTileProvider(application, DgpsStationTileRepository(dataSource))
   }
}