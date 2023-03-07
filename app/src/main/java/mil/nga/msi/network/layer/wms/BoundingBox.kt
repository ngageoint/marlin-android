package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "BoundingBox", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class BoundingBox(
   @field:Attribute(name = "SRS", required = false)
   var srs: String? = null,

   @field:Attribute(name = "minx", required = false)
   var minX: Boolean = false,

   @field:Attribute(name = "miny", required = false)
   var minY: Boolean = false,

   @field:Attribute(name = "maxx", required = false)
   var maxX: Boolean = false,

   @field:Attribute(name = "maxy", required = false)
   var maxY: Boolean = false
): Parcelable