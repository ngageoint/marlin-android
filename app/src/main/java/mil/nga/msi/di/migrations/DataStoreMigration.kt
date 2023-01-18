package mil.nga.msi.di.migrations

import androidx.datastore.core.DataMigration
import mil.nga.msi.type.UserPreferences

class DataStoreMigration: DataMigration<UserPreferences> {
   override suspend fun cleanUp() {
      TODO("Not yet implemented")
   }

   override suspend fun migrate(currentData: UserPreferences): UserPreferences {
      TODO("Not yet implemented")
   }

   // TODO guess I would need to check the current version
   override suspend fun shouldMigrate(currentData: UserPreferences) = true
}