package mil.nga.msi.di

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPreferencesModule {
   @Provides
   @Singleton
   fun provideSharedPreferences(application: Application): SharedPreferences {
      val masterKey = MasterKey.Builder(application)
         .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
         .build()

      return EncryptedSharedPreferences.create(
         application,
         FILE_NAME,
         masterKey,
         EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
         EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
   }

   companion object {
      private const val FILE_NAME = "EncryptedSharedPreferences"
   }
}