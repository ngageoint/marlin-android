package mil.nga.msi.di.migrations.datastore

import androidx.datastore.core.DataMigration
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.di.DataStoreModule
import mil.nga.msi.type.UserPreferences

val dataStoreMigration_4_5 = object : DataMigration<UserPreferences> {
    override suspend fun migrate(currentData: UserPreferences): UserPreferences {
        // Remove Asam data source
        val nonTabs = currentData.nonTabsList.toMutableSet().apply {
            remove(DataSource.ASAM.name)
        }.toList()
        val tabs = currentData.tabsList.toMutableSet().apply {
            remove(DataSource.ASAM.name)
        }

        return currentData
            .toBuilder()
            .clearTabs()
            .addAllTabs(tabs)
            .clearNonTabs()
            .addAllNonTabs(nonTabs)
            .removeMapped(DataSource.ASAM.name)
            .removeFilters(DataSource.ASAM.name)
            .setVersion(5)
            .build()
    }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean {
        return currentData.version < DataStoreModule.VERSION
    }

    override suspend fun cleanUp() {}
}