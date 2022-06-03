package mil.nga.msi.ui.map

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MapAnnotation(val type: Type, val id: String) : Parcelable {
   enum class Type { ASAM, MODU }
}