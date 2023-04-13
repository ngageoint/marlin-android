package mil.nga.msi.di

import android.app.Application
import android.app.DownloadManager
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.network.QualifiedTypeConverterFactory
import mil.nga.msi.network.asam.AsamService
import mil.nga.msi.network.asam.AsamsTypeAdapter
import mil.nga.msi.network.asam.AsamResponse
import mil.nga.msi.network.dgpsstations.DgpsStationResponse
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.network.dgpsstations.DgpsStationsTypeAdapter
import mil.nga.msi.network.electronicpublication.ElectronicPublicationTypeAdapter
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.network.light.LightService
import mil.nga.msi.network.light.LightsTypeAdapter
import mil.nga.msi.network.light.LightResponse
import mil.nga.msi.network.modu.ModuService
import mil.nga.msi.network.modu.ModusTypeAdapter
import mil.nga.msi.network.modu.ModuResponse
import mil.nga.msi.network.navigationalwarning.NavigationalWarningResponse
import mil.nga.msi.network.navigationalwarning.NavigationalWarningService
import mil.nga.msi.network.navigationalwarning.NavigationalWarningsTypeAdapter
import mil.nga.msi.network.noticetomariners.*
import mil.nga.msi.network.port.PortResponse
import mil.nga.msi.network.port.PortService
import mil.nga.msi.network.port.PortsTypeAdapter
import mil.nga.msi.network.radiobeacon.RadioBeaconResponse
import mil.nga.msi.network.radiobeacon.RadioBeaconService
import mil.nga.msi.network.radiobeacon.RadioBeaconsTypeAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
         .registerTypeAdapter(object : TypeToken<AsamResponse>() {}.type, AsamsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<ModuResponse>() {}.type, ModusTypeAdapter())
         .registerTypeAdapter(object : TypeToken<NavigationalWarningResponse>() {}.type, NavigationalWarningsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<LightResponse>() {}.type, LightsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<PortResponse>() {}.type, PortsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<RadioBeaconResponse>() {}.type, RadioBeaconsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<DgpsStationResponse>() {}.type, DgpsStationsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<NoticeToMarinersResponse>() {}.type, NoticeToMarinersTypeAdapter())
         .registerTypeAdapter(object : TypeToken<NoticeToMarinersGraphicsResponse>() {}.type, NoticeToMarinersGraphicsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<ChartCorrectionResponse>() {}.type, ChartCorrectionsTypeAdapter())
         .registerTypeAdapter(object : TypeToken<ElectronicPublication>() {}.type, ElectronicPublicationTypeAdapter())
         .create()
   }

   @Provides
   @Singleton
   fun provideTikXml(): TikXml {
      return TikXml.Builder()
         .exceptionOnUnreadXml(false)
         .build()
   }

   @Provides
   @Singleton
   fun provideRetrofit(
      gson: Gson,
      tikXml: TikXml,
      okHttpClient: OkHttpClient,
   ): Retrofit {
      return Retrofit.Builder()
         .addConverterFactory(
           QualifiedTypeConverterFactory(
              GsonConverterFactory.create(gson),
              TikXmlConverterFactory.create(tikXml)
           )
         )
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
   fun provideLayerService(retrofit: Retrofit): LayerService {
      return retrofit.create(LayerService::class.java)
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