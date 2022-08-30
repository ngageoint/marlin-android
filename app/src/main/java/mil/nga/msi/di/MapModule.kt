package mil.nga.msi.di

import android.app.Application
import com.google.android.gms.maps.model.TileProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.ui.map.overlay.LightTileProvider
import mil.nga.msi.ui.map.overlay.PortTileProvider
import mil.nga.msi.ui.map.overlay.RadioBeaconTileProvider
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MapModule {
   @Singleton
   @Provides
   @Named("lightTileProvider")
   fun provideLightTileProvider(application: Application, repository: LightRepository): TileProvider {
      return LightTileProvider(application, repository)
   }

   @Singleton
   @Provides
   @Named("portTileProvider")
   fun providePortTileProvider(application: Application, repository: PortRepository): TileProvider {
      return PortTileProvider(application, repository)
   }

   @Singleton
   @Provides
   @Named("radioBeaconTileProvider")
   fun provideRadioBeaconProvider(application: Application, repository: RadioBeaconRepository): TileProvider {
      return RadioBeaconTileProvider(application, repository)
   }
}