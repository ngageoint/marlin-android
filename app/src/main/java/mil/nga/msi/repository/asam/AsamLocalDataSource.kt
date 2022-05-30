package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import javax.inject.Inject

class AsamLocalDataSource @Inject constructor(
   private val asamDao: AsamDao
) {
   fun observeAsams() = asamDao.observeAsams()
   fun observeAsamMapItems() = asamDao.observeAsamMapItems()
   fun observeAsamListItems() = asamDao.getAsamListItems()

   suspend fun getAsams(): List<Asam> = asamDao.getAsams()
   suspend fun getLatestAsam() = asamDao.getLatestAsam()

   suspend fun insert(asams: List<Asam>) = asamDao.insert(asams)
}