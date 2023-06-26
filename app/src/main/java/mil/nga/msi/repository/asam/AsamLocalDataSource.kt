package mil.nga.msi.repository.asam

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import javax.inject.Inject

class AsamLocalDataSource @Inject constructor(
   private val dao: AsamDao
) {
   suspend fun insert(asams: List<Asam>) = dao.insert(asams)

   fun observeAsams() = dao.observeAsams()
   fun observeAsam(reference: String) = dao.observeAsam(reference)
   fun observeAsamMapItems(query: SimpleSQLiteQuery) = dao.observeAsamMapItems(query)
   fun observeAsamListItems(query: SimpleSQLiteQuery) = dao.getAsamListItems(query)

   fun getAsams(query: SimpleSQLiteQuery) = dao.getAsams(query)

   fun isEmpty() = dao.count() == 0

   suspend fun getAsam(reference: String) = dao.getAsam(reference)
   suspend fun getAsams(): List<Asam> = dao.getAsams()

   suspend fun existingAsams(references: List<String>) = dao.getAsams(references)
}