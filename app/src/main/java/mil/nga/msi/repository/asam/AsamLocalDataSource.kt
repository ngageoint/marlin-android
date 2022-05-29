package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import javax.inject.Inject

class AsamLocalDataSource @Inject constructor(
   private val asamDao: AsamDao
) {
   suspend fun getAsams(): List<Asam> = asamDao.asams()
   suspend fun insert(asams: List<Asam>) = asamDao.insert(asams)
   suspend fun getLatestAsam() = asamDao.latestAsam()
   fun getAsamPages() = asamDao.asamPagingSource()
}