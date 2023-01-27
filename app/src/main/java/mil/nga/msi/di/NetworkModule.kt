package mil.nga.msi.di

import android.app.Application
import android.app.DownloadManager
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.network.asam.AsamService
import mil.nga.msi.network.asam.AsamsTypeAdapter
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.network.dgpsstations.DgpsStationsTypeAdapter
import mil.nga.msi.network.electronicpublication.ElectronicPublicationTypeAdapter
import mil.nga.msi.network.light.LightService
import mil.nga.msi.network.light.LightsTypeAdapter
import mil.nga.msi.network.modu.ModuService
import mil.nga.msi.network.modu.ModusTypeAdapter
import mil.nga.msi.network.navigationalwarning.NavigationalWarningService
import mil.nga.msi.network.navigationalwarning.NavigationalWarningsTypeAdapter
import mil.nga.msi.network.noticetomariners.NoticeToMarinersGraphicsTypeAdapter
import mil.nga.msi.network.noticetomariners.NoticeToMarinersService
import mil.nga.msi.network.noticetomariners.NoticeToMarinersTypeAdapter
import mil.nga.msi.network.port.PortService
import mil.nga.msi.network.port.PortsTypeAdapter
import mil.nga.msi.network.radiobeacon.RadioBeaconService
import mil.nga.msi.network.radiobeacon.RadioBeaconsTypeAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.List
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

   @Singleton
   @Provides
   fun provideOkHttpClient(): OkHttpClient {
      return OkHttpClient.Builder()
         .connectTimeout(60, TimeUnit.SECONDS)
         .readTimeout(60, TimeUnit.SECONDS)
         .writeTimeout(60, TimeUnit.SECONDS)
         .build()
   }

   @Provides
   @Singleton
   fun provideGson(): Gson {
      return GsonBuilder()
         .registerTypeAdapter(object : TypeToken<List<Asam>>() {}.type, AsamsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<Modu>>() {}.type, ModusTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<NavigationalWarning>>() {}.type, NavigationalWarningsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<Light>>() {}.type, LightsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<Port>>() {}.type, PortsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<RadioBeacon>>() {}.type, RadioBeaconsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<DgpsStation>>() {}.type, DgpsStationsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<NoticeToMariners>>() {}.type, NoticeToMarinersTypeAdapter())
         .registerTypeAdapter(object : TypeToken<List<NoticeToMarinersGraphics>>() {}.type, NoticeToMarinersGraphicsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<ElectronicPublication>() {}.type, ElectronicPublicationTypeAdapter())
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
         .baseUrl("https://msi.nga.mil/api/")
         .client(okHttpClient)
         .build()
   }

   @Provides
   @Singleton
   @Inject
   fun provideDownloadManager(context: Application) = context.getSystemService<DownloadManager>()!!

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

   @Provides
   @Singleton
   fun provideLightService(retrofit: Retrofit): LightService {
      return retrofit.create(LightService::class.java)
   }

   @Provides
   @Singleton
   fun providePortService(retrofit: Retrofit): PortService {
      return retrofit.create(PortService::class.java)
   }

   @Provides
   @Singleton
   fun provideRadioBeaconService(retrofit: Retrofit): RadioBeaconService {
      return retrofit.create(RadioBeaconService::class.java)
   }

   @Provides
   @Singleton
   fun provideDgpsStationService(retrofit: Retrofit): DgpsStationService {
      return retrofit.create(DgpsStationService::class.java)
   }

   @Provides
   @Singleton
   fun provideNoticeToMarinersService(retrofit: Retrofit): NoticeToMarinersService {
      return retrofit.create(NoticeToMarinersService::class.java)
   }
}