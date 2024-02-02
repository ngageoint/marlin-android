package mil.nga.msi.di.migrations.datastore

import androidx.datastore.core.DataMigration
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.di.DataStoreModule
import mil.nga.msi.type.UserPreferences

val dataStoreMigration_2_3 = object : DataMigration<UserPreferences> {
   override suspend fun migrate(currentData: UserPreferences): UserPreferences {
      // Remove last tab in tabs and place in non-tabs
      val lastTab = currentData.tabsList.last()
      val tabs = currentData.tabsList.toMutableList().apply {
         removeLast()
      }

      // Add Bookmark data source
      val nonTabs = currentData.nonTabsList.toMutableSet().apply {
         add(DataSource.BOOKMARK.name)
      }.toList().toMutableList().apply {
         add(0, lastTab)
      }

      return currentData
         .toBuilder()
         .clearTabs()
         .addAllTabs(tabs)
         .clearNonTabs()
         .addAllNonTabs(nonTabs)
         .setVersion(3)
         .build()
   }

   override suspend fun shouldMigrate(currentData: UserPreferences): Boolean {
      return currentData.version < DataStoreModule.VERSION
   }

   override suspend fun cleanUp() {}
}