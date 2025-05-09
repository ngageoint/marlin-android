package mil.nga.msi.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.startup.dgpsstation.DgpsStationInitializer
import mil.nga.msi.startup.electronicpublication.ElectronicPublicationInitializer
import mil.nga.msi.startup.light.LightInitializer
import mil.nga.msi.startup.modu.ModuInitializer
import mil.nga.msi.startup.navigationalwarning.NavigationalWarningInitializer
import mil.nga.msi.startup.noticetomariners.NoticeToMarinersInitializer
import mil.nga.msi.startup.port.PortInitializer
import mil.nga.msi.startup.radiobeacon.RadioBeaconInitializer

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppInitializer {

   companion object {
      // Resolve the InitializerEntryPoint from a context
      fun resolve(context: Context): AppInitializer {
         val appContext = context.applicationContext ?: throw IllegalStateException()
         return EntryPointAccessors.fromApplication(
            appContext,
            AppInitializer::class.java
         )
      }
   }

   fun inject(workManagerInitializer: WorkManagerInitializer)
//   fun inject(asamInitializer: AsamInitializer)
   fun inject(moduInitializer: ModuInitializer)
   fun inject(navigationalWarningInitializer: NavigationalWarningInitializer)
   fun inject(lightInitializer: LightInitializer)
   fun inject(portInitializer: PortInitializer)
   fun inject(radioBeaconInitializer: RadioBeaconInitializer)
   fun inject(dgpsStationInitializer: DgpsStationInitializer)
   fun inject(electronicPublicationInitializer: ElectronicPublicationInitializer)
   fun inject(noticeToMarinersInitializer: NoticeToMarinersInitializer)
}