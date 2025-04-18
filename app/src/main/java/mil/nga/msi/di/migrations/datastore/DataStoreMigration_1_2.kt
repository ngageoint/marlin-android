package mil.nga.msi.di.migrations.datastore

import androidx.datastore.core.DataMigration
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.di.DataStoreModule
import mil.nga.msi.type.UserPreferences

val dataStoreMigration_1_2 = object : DataMigration<UserPreferences> {
   override suspend fun migrate(currentData: UserPreferences): UserPreferences {
      // Add Notice To Mariners data source
      val nonTabs = currentData.nonTabsList.toMutableSet().apply {
         add(DataSource.NOTICE_TO_MARINERS.name)
      }.toList()

      return currentData
         .toBuilder()
         .clearNonTabs()
         .addAllNonTabs(nonTabs)
         .setVersion(2)
         .build()
   }

   override suspend fun shouldMigrate(currentData: UserPreferences): Boolean {
      return currentData.version < DataStoreModule.VERSION
   }

   override suspend fun cleanUp() {}
}