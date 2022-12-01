package mil.nga.msi.repository.electronicpublication

import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import javax.inject.Inject

class ElectronicPublicationRepository @Inject constructor() {

    suspend fun syncFromRemote(): Unit {
    }

    suspend fun observeElectronicPublications(): Flow<List<ElectronicPublication>> {
        throw Error("unimplemented")
    }
}