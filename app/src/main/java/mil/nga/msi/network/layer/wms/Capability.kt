package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Capability", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class Capability(
   @field:Element(name = "Request", required = false)
   var request: Request? = null,

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
) : Parcelable


@Root(name = "Request", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class Request(
   @field:Element(name = "GetMap", required = false)
   var getMap: GetMap? = null
) : Parcelable

@Root(name = "GetMap", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class GetMap(
   @field:ElementList(name = "Format", inline = true, required = false)
   var format: List<String> = mutableListOf()
) : Parcelable