package mil.nga.msi.repository.modu

import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao
import javax.inject.Inject

class ModuLocalDataSource @Inject constructor(
   private val dao: ModuDao
) {
   fun observeModus() = dao.observeModus()
   fun observeModu(name: String) = dao.observeModu(name)
   fun observeModuMapItems() = dao.observeModuMapItems()
   fun observeModuListItems() = dao.getModuListItems()

   suspend fun getModu(name: String) = dao.getModu(name)
   suspend fun getModus() = dao.getModus()
   suspend fun getLatestModu() = dao.getLatestModu()

   fun getModus(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getModus(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun insert(modus: List<Modu>) = dao.insert(modus)
}