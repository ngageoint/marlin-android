package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "WMS_Capabilities", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class WMSCapabilities(
   @field:Attribute(name = "version", required = false)
   var version: String? = null,

   @field:Element(name = "Service")
   var service: Service? = null,

   @field:Element(name = "Capability")
   var capability: Capability? = null
): Parcelable