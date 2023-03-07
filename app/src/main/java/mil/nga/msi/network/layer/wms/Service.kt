package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "Service", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
data class Service(
   @field:Element(name = "Name", required = false)
   var name: String? = null,

   @field:Element(name = "Title", required = false)
   var title: String? = null,

   @field:Element(name = "Abstract", required = false)
   var abstract: String? = null,

   @field:Element(name = "ContactInformation", required = false)
   var contactInformation: ContactInformation? = null
): Parcelable