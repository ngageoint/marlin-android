package mil.nga.msi.repository.electronicpublication

import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationDao
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import javax.inject.Inject

class ElectronicPublicationLocalDataSource @Inject constructor(private val dao: ElectronicPublicationDao) {

    suspend fun count(): Int = dao.count()
    suspend fun isEmpty(): Boolean = count() == 0
    suspend fun insert(allThese: List<ElectronicPublication>) = dao.insert(allThese)
    fun observeFileCountsByType() = dao.observeFileCountsByType()
    fun observePublicationsOfType(pubType: ElectronicPublicationType) = dao.observeElectronicPublicationsOfType(pubType.typeId)
}