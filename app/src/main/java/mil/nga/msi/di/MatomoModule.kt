package mil.nga.msi.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.matomo.sdk.Matomo
import org.matomo.sdk.Tracker
import org.matomo.sdk.TrackerBuilder
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MatomoModule {
   @Provides
   @Singleton
   fun provideMatomo(application: Application): Matomo {
      return Matomo.getInstance(application)
   }

   @Provides
   @Singleton
   fun provideTracker(matomo: Matomo): Tracker {
      val url = "https://webanalytics.nga.mil/matomo.php"
      val siteId = 795

      return TrackerBuilder
         .createDefault(url, siteId)
         .build(matomo)
   }
}