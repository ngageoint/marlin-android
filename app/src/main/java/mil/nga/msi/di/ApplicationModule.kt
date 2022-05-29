package mil.nga.msi.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.MsiApplication
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {
   @Singleton
   @Provides
   internal fun provideWorkManager(application: Application): WorkManager {
      return WorkManager.getInstance(application.applicationContext)
   }
}