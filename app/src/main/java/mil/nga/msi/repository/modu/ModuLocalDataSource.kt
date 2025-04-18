package mil.nga.msi.repository.modu

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao
import javax.inject.Inject

class ModuLocalDataSource @Inject constructor(
   private val dao: ModuDao
) {
   fun observeModus() = dao.observeModus()
   fun observeModu(name: String) = dao.observeModu(name)
   fun observeModuMapItems(query: SimpleSQLiteQuery) = dao.observeModuMapItems(query)
   fun observeModuListItems(query: SimpleSQLiteQuery) = dao.observeModuListItems(query)

   fun isEmpty() = dao.count() == 0
   suspend fun count(query: SimpleSQLiteQuery) = dao.count(query)

   suspend fun getModu(name: String) = dao.getModu(name)
   suspend fun getModus() = dao.getModus()
   suspend fun getLatestModu() = dao.getLatestModu()

   suspend fun existingModus(names: List<String>) = dao.getModus(names)

   fun getModus(query: SimpleSQLiteQuery) = dao.getModus(query)

   suspend fun insert(modus: List<Modu>) = dao.insert(modus)
}