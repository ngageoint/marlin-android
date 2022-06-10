package mil.nga.msi.repository.asam

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import javax.inject.Inject

class AsamLocalDataSource @Inject constructor(
   private val dao: AsamDao
) {
   fun observeAsams() = dao.observeAsams()
   fun observeAsam(reference: String) = dao.observeAsam(reference)
   fun observeAsamMapItems() = dao.observeAsamMapItems()
   fun observeAsamListItems() = dao.getAsamListItems()

   suspend fun getAsam(reference: String) = dao.getAsam(reference)
   suspend fun getAsams(): List<Asam> = dao.getAsams()
   suspend fun getLatestAsam() = dao.getLatestAsam()

   suspend fun insert(asams: List<Asam>) = dao.insert(asams)
}