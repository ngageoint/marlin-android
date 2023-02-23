package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.noticetomariners.query.NoticeToMarinersLocationFilter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeToMarinersRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val locationFilter = preferencesDataStore.data.map { preferences ->
      // TODO maybe deserialize here instead of view model
      preferences.noticeToMarinersFilter.locationFilter
   }.distinctUntilChanged()

   val noticeFilter = preferencesDataStore.data.map { preferences ->
      preferences.noticeToMarinersFilter.noticeFilter
   }.distinctUntilChanged()

   suspend fun setLocationFilter(filter: NoticeToMarinersLocationFilter) {
      preferencesDataStore.updateData { preferences ->
         val location = filter.location ?: LatLng(0.0, 0.0)
         val distance = filter.distance ?: 0f

         val builder = preferences.toBuilder()
         val ntmBuilder = builder.noticeToMarinersFilter.toBuilder()
         ntmBuilder.locationFilter = ntmBuilder.locationFilter.toBuilder()
            .setComparator(filter.comparator.name)
            .setLocation("${location.latitude},${location.longitude}")
            .setDistance(distance)
            .build()
         builder.noticeToMarinersFilter = ntmBuilder.build()
         builder.build()
      }
   }

   suspend fun removeLocationFilter() {
      preferencesDataStore.updateData { preferences ->
         val builder = preferences.toBuilder()
         val ntmBuilder = builder.noticeToMarinersFilter.toBuilder()
         ntmBuilder.locationFilter = ntmBuilder.locationFilter.toBuilder()
            .setComparator("")
            .setLocation("")
            .setDistance(0f)
            .build()
         builder.noticeToMarinersFilter = ntmBuilder.build()
         builder.build()
      }
   }
}