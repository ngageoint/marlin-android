package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.coordinate.CoordinateSystem
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject

class MapRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val coordinateSystem: Flow<CoordinateSystem> = preferencesDataStore.data.map {
      CoordinateSystem.fromName(it.map.coordinateSystem)
   }.distinctUntilChanged()

   suspend fun setCoordinateSystem(coordinateSystem: CoordinateSystem) {
      preferencesDataStore.updateData {
         val builder = it.toBuilder()
         builder.map = builder.map.toBuilder()
            .setCoordinateSystem(coordinateSystem.name)
            .build()

         builder.build()
      }
   }
}