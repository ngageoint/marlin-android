package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.*

@Root(name = "Layer", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class Layer(
   @field:Element(name = "Title")
   var title: String? = null,

   @field:Element(name = "Abstract")
   var abstract: String? = null,

   @field:Element(name = "Name", required = false)
   var name: String? = null,

   @field:Attribute(name = "queryable", required = false)
   var queryable: Int = 0,

   @field:Attribute(name = "opaque", required = false)
   var opaque: Int = 0,

   @field:Element(name = "SRS", required = false)
   var srs: String? = null,

   @field:Path("LatLngBoundingBox")
   var latLngBoundingBox: LatLngBoundingBox? = null,

   @field:Path("BoundingBox")
   var boundingBox: BoundingBox? = null,

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
) : Parcelable