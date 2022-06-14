package mil.nga.msi.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.network.asam.AsamsTypeAdapter
import mil.nga.msi.network.modu.ModusTypeAdapter
import mil.nga.msi.network.asam.AsamService
import mil.nga.msi.network.modu.ModuService
import mil.nga.msi.network.navigationalwarning.NavigationalWarningService
import mil.nga.msi.network.navigationalwarning.NavigationalWarningsTypeAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.List
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

   @Singleton
   @Provides
   fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

   @Provides
   @Singleton
   fun provideGson(): Gson {
      return GsonBuilder()
         .registerTypeAdapter(object : TypeToken<List<Asam>>() {}.type, AsamsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<Modu>>() {}.type, ModusTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<NavigationalWarning>>() {}.type, NavigationalWarningsTypeAdapter())
         .create()
   }

   @Provides
   @Singleton
   fun provideRetrofit(
      gson: Gson,
      okHttpClient: OkHttpClient,
   ): Retrofit {
      return Retrofit.Builder()
         .addConverterFactory(GsonConverterFactory.create(gson))
         .baseUrl("https://msi.gs.mil/")
         .client(okHttpClient)
         .build()
   }

   @Provides
   @Singleton
   fun provideAsamService(retrofit: Retrofit): AsamService {
      return retrofit.create(AsamService::class.java)
   }

   @Provides
   @Singleton
   fun provideModuService(retrofit: Retrofit): ModuService {
      return retrofit.create(ModuService::class.java)
   }

   @Provides
   @Singleton
   fun provideNavigationalWarningService(retrofit: Retrofit): NavigationalWarningService {
      return retrofit.create(NavigationalWarningService::class.java)
   }
}