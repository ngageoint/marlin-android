package mil.nga.msi.network.layer.wms

import org.simpleframework.xml.*

@Root(name = "WMS_Capabilities", strict = false)
data class WMSCapabilities(
   @field:Attribute(name = "version", required = false)
   var version: String? = null,

   @field:Element(name = "Service", required = false)
   var service: Service? = null,

   @field:Element(name = "Capability")
   var capability: Capability? = null
) {
   fun isValid(): Boolean {
      return capability?.request?.map?.hasImageFormat() == true
   }
}

@Root(name = "Service", strict = false)
data class Service(
   @field:Element(name = "Name", required = false)
   var name: String? = null,

   @field:Element(name = "Title", required = false)
   var title: String? = null,

   @field:Element(name = "Abstract", required = false)
   var abstract: String? = null,

   @field:Element(name = "ContactInformation", required = false)
   var contactInformation: ContactInformation? = null
)

@Root(name = "ContactInformation", strict = false)
data class ContactInformation(
   @field:Element(name = "ContactPersonPrimary", required = false)
   var person: ContactPersonPrimary? = null,

   @field:Element(name = "ContactVoiceTelephone", required = false)
   var phone: String? = null,

   @field:Element(name = "ContactElectronicMailAddress", required = false)
   var email: String? = null
)

@Root(name = "ContactPersonPrimary", strict = false)
data class ContactPersonPrimary(
   @field:Element(name = "ContactPerson", required = false)
   var name: String? = null,

   @field:Element(name = "ContactOrganization", required = false)
   var organization: String? = null
)

@Root(name = "Capability", strict = false)
data class Capability(
   @field:Element(name = "Request", required = false)
   var request: Request? = null,

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
)

@Root(name = "Request", strict = false)
data class Request(
   @field:Element(name = "GetMap", required = false)
   var map: GetMap? = null
)

@Root(name = "GetMap", strict = false)
data class GetMap(
   @field:ElementList(name="Format", entry="Format", inline = true, required = false, type = String::class)
   var formats: List<String> = mutableListOf()
) {
   fun hasImageFormat(): Boolean {
      return getImageFormat() != null
   }

   fun getImageFormat(): String? {
      return formats.firstOrNull {
         it.equals("image/png", ignoreCase = true) ||
         it.equals("image/jpeg", ignoreCase = true)
      }
   }
}

@Root(name = "Layer", strict = false)
data class Layer(
   @field:Element(name = "Title")
   var title: String? = null,

   @field:Element(name = "Abstract")
   var abstract: String? = null,

   @field:Element(name = "Name", required = false)
   var name: String? = null,

   @field:ElementList(name="CRS", entry="CRS", inline = true, required = false, type = String::class)
   var crs: List<String> = mutableListOf(),

   @field:ElementList(name = "BoundingBox", inline = true, required = false)
   var boundingBoxes: List<BoundingBox> = mutableListOf(),

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
) {
   fun hasTiles(): Boolean {
      return name != null
   }

   fun isWebMercator(): Boolean {
      return if (crs.any {
            it.equals("EPSG:3857", ignoreCase = true) ||
            it.equals("EPSG:900913", ignoreCase = true)
         }) {
         true
      } else {
         layers.any { layer -> layer.isWebMercator() }
      }
   }
}

@Root(name = "BoundingBox", strict = false)
class BoundingBox {
   @field:Attribute(name = "CRS", required = false)
   lateinit var crs: String

   @field:Attribute(name = "minx", required = false)
   var minX: Double = 0.0

   @field:Attribute(name = "miny", required = false)
   var minY: Double = 0.0

   @field:Attribute(name = "maxx", required = false)
   var maxX: Double = 0.0

   @field:Attribute(name = "maxy", required = false)
   var maxY: Double = 0.0
}