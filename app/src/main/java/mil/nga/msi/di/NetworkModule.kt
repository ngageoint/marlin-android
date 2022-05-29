package mil.nga.msi.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.asam.AsamsTypeAdapter
import mil.nga.msi.repository.asam.AsamService
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
   fun provideGson(application: Application): Gson {
      return GsonBuilder()
         .registerTypeAdapter(object : TypeToken<List<Asam>>() {}.type, AsamsTypeAdapter())
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
}