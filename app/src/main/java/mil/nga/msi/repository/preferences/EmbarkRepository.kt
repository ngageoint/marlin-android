package mil.nga.msi.repository.preferences

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject

class EmbarkRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val disclaimer: Flow<Boolean> = preferencesDataStore.data.map { it.embark.disclaimer }
   val location: Flow<Boolean> = preferencesDataStore.data.map { it.embark.location }
   val notification: Flow<Boolean> = preferencesDataStore.data.map { it.embark.notification }
   val tabs: Flow<Boolean> = preferencesDataStore.data.map { it.embark.tabs }
   val map: Flow<Boolean> = preferencesDataStore.data.map { it.embark.map }

   val embark = MediatorLiveData<Boolean?>().apply {
      value = null

      var disclaimerCheck: Boolean? = null
      var locationCheck: Boolean? = null
      var notificationCheck: Boolean? = null
      var tabsCheck: Boolean? = null
      var mapCheck: Boolean? = null

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
         notificationCheck = true
      }

      fun check() {
         if (disclaimerCheck != null &&
            locationCheck != null &&
            notificationCheck != null &&
            tabsCheck != null &&
            mapCheck != null
         ) {
            value = disclaimerCheck == true &&
                    locationCheck == true &&
                    notificationCheck == true &&
                    tabsCheck == true &&
                    mapCheck == true
         }
      }

      addSource(disclaimer.asLiveData()) {
         disclaimerCheck = it
         check()
      }

      addSource(location.asLiveData()) {
         locationCheck = it
         check()
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         addSource(notification.asLiveData()) {
            notificationCheck = it
            check()
         }
      }

      addSource(tabs.asLiveData()) {
         tabsCheck = it
         check()
      }

      addSource(map.asLiveData()) {
         mapCheck = it
         check()
      }
   }.asFlow()

   suspend fun setEmbark() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setEmbark(true)
            .build()

         builder.build()
      }
   }

   suspend fun setDisclaimer() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setDisclaimer(true)
            .build()

         builder.build()
      }
   }

   suspend fun setLocation() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setLocation(true)
            .build()

         builder.build()
      }
   }

   suspend fun setNotification() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setNotification(true)
            .build()

         builder.build()
      }
   }

   suspend fun setTabs() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setTabs(true)
            .build()

         builder.build()
      }
   }

   suspend fun setMap() {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()

         builder.embark = builder.embark
            .toBuilder()
            .setMap(true)
            .build()

         builder.build()
      }
   }
}