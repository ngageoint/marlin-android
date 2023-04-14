package mil.nga.msi.repository.preferences

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Credentials(
   val username: String,
   val password: String
) : Parcelable
