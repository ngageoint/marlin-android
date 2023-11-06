package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeToMarinersRepository @Inject constructor(
   preferencesDataStore: DataStore<UserPreferences>
) {
   val locationFilter = preferencesDataStore.data.map { preferences ->
      // TODO maybe deserialize here instead of view model
      preferences.noticeToMarinersFilter.locationFilter
   }.distinctUntilChanged()

}