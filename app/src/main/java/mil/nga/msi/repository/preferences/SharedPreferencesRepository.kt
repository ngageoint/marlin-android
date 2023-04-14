package mil.nga.msi.repository.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(
   private val sharedPreferences: SharedPreferences
) {
   fun setLayerCredentials(
      layerId: Long,
      credentials: Credentials
   ) {
      sharedPreferences
         .edit()
         .putString(layerId.toString(), listOf(credentials.username, credentials.password).joinToString(SEPARATOR))
         .apply()
   }

   fun getLayerCredentials(layerId: Long): Credentials? {
      return sharedPreferences.getString(layerId.toString(), null)?.let { credentials ->
         val (username, password) = credentials.split(SEPARATOR)
         Credentials(username, password)
      }
   }

   fun deleteLayerCredentials(layerId: Long) {
      sharedPreferences
         .edit()
         .remove(layerId.toString())
         .apply()
   }

   companion object {
      private const val SEPARATOR = ","
   }
}