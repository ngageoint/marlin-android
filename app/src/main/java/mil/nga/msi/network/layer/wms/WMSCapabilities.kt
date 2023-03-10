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
class ContactInformation(
   @field:Element(name = "ContactPersonPrimary", required = false)
   var person: ContactPersonPrimary? = null,

   @field:Element(name = "ContactVoiceTelephone", required = false)
   var phone: String? = null,

   @field:Element(name = "ContactElectronicMailAddress", required = false)
   var email: String? = null
)

@Root(name = "ContactPersonPrimary", strict = false)
class ContactPersonPrimary(
   @field:Element(name = "ContactPerson", required = false)
   var name: String? = null,

   @field:Element(name = "ContactOrganization", required = false)
   var organization: String? = null
)

@Root(name = "Capability", strict = false)
class Capability(
   @field:Element(name = "Request", required = false)
   var request: Request? = null,

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
)


@Root(name = "Request", strict = false)
class Request(
   @field:Element(name = "GetMap", required = false)
   var map: GetMap? = null
)

@Root(name = "GetMap", strict = false)
class GetMap(
   @field:ElementList(name = "Format", inline = true)
   var formats: List<Format> = mutableListOf()
) {
   fun hasImageFormat(): Boolean {
      return getImageFormat() != null
   }

   fun getImageFormat(): String? {
      return formats.firstOrNull {
         it.format.equals("image/png", ignoreCase = true) ||
         it.format.equals("image/jpeg", ignoreCase = true)
      }?.format
   }
}

@Root(name = "Format", strict = false)
class Format(
   @field:Text
   var format: String? = null
)

@Root(name = "Layer", strict = false)
class Layer(
   @field:Element(name = "Title")
   var title: String? = null,

   @field:Element(name = "Abstract")
   var abstract: String? = null,

   @field:Element(name = "Name", required = false)
   var name: String? = null,

   @field:ElementList(name = "CRS", inline = true, required = false)
   var crs: List<CRS> = mutableListOf(),

   @field:Path("BoundingBox")
   var boundingBox: BoundingBox? = null,

   @field:ElementList(name = "Layer", inline = true, required = false)
   var layers: List<Layer> = mutableListOf()
) {
   fun hasTiles(): Boolean {
      return name != null
   }

   fun isWebMercator(): Boolean {
      return if (crs.any {
            it.crs.equals("EPSG:3857", ignoreCase = true) ||
            it.crs.equals("EPSG:900913", ignoreCase = true)
         }) {
         true
      } else {
         layers.any { layer -> layer.isWebMercator() }
      }
   }
}

@Root(name = "CRS", strict = false)
class CRS(
   @field:Text
   var crs: String? = null
)

@Root(name = "BoundingBox", strict = false)
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
)