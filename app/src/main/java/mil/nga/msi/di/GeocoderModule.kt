package mil.nga.msi.di

import android.app.Application
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GeocoderModule {
   @Provides
   @Singleton
   fun provideGeocoder(application: Application): Geocoder {
      return Geocoder(application)
   }
}