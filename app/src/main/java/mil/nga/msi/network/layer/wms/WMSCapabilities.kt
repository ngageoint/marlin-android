package mil.nga.msi.network.layer.wms

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "WMS_Capabilities")
data class WMSCapabilities(
   @field:Attribute(name = "version")
   var version: String? = null,

   @field:Element(name = "Service")
   var service: Service? = null,

   @field:Element(name = "Capability")
   var capability: Capability? = null
) {
   fun isValid(): Boolean {
      return capability?.request?.map?.hasImageFormat() == true
   }
}

@Xml(name = "Service")
data class Service(
   @field:PropertyElement(name = "Name")
   var name: String? = null,

   @field:PropertyElement(name = "Title")
   var title: String? = null,

   @field:PropertyElement(name = "Abstract")
   var abstract: String? = null,

   @field:Element(name = "ContactInformation")
   var contactInformation: ContactInformation? = null
)

@Xml(name = "ContactInformation")
data class ContactInformation(
   @field:Element(name = "ContactPersonPrimary")
   var person: ContactPersonPrimary? = null,

   @field:PropertyElement(name = "ContactVoiceTelephone")
   var phone: String? = null,

   @field:PropertyElement(name = "ContactElectronicMailAddress")
   var email: String? = null
)

@Xml(name = "ContactPersonPrimary")
data class ContactPersonPrimary(
   @field:PropertyElement(name = "ContactPerson")
   var name: String? = null,

   @field:PropertyElement(name = "ContactOrganization")
   var organization: String? = null
)

@Xml(name = "Capability")
data class Capability(
   @field:Element(name = "Request")
   var request: Request? = null,

   @field:Element(name = "Layer")
   var layers: List<Layer> = mutableListOf()
)

@Xml(name = "Request")
data class Request(
   @field:Element(name = "GetMap")
   var map: GetMap? = null
)

@Xml(name = "GetMap")
data class GetMap(
   @field:Element(name="Format")
   var formats: List<StringWrapper> = mutableListOf()
) {
   fun hasImageFormat(): Boolean {
      return getImageFormat() != null
   }

   fun getImageFormat(): String? {
      return formats.firstOrNull {
         it.value.equals("image/png", ignoreCase = true) ||
                 it.value.equals("image/jpeg", ignoreCase = true)
      }?.value
   }
}

@Xml(name = "Layer")
data class Layer(
   @field:PropertyElement(name = "Title")
   var title: String? = null,

   @field:PropertyElement(name = "Abstract")
   var abstract: String? = null,

   @field:PropertyElement(name = "Name")
   var name: String? = null,

   @field:Element(name="CRS")
   var crs: List<StringWrapper> = mutableListOf(),

   @field:Element(name = "BoundingBox")
   var boundingBoxes: List<BoundingBox> = mutableListOf(),

   @field:Element(name = "Layer")
   var layers: List<Layer> = mutableListOf()
) {
   fun hasTiles(): Boolean {
      return name != null
   }

   fun isWebMercator(): Boolean {
      return if (crs.any {
            it.value.equals("EPSG:3857", ignoreCase = true) ||
                    it.value.equals("EPSG:900913", ignoreCase = true)
         }) {
         true
      } else {
         layers.any { layer -> layer.isWebMercator() }
      }
   }
}

@Xml(name = "BoundingBox")
class BoundingBox {
   @field:Attribute(name = "CRS")
   lateinit var crs: String

   @field:Attribute(name = "minx")
   var minX: Double = 0.0

   @field:Attribute(name = "miny")
   var minY: Double = 0.0

   @field:Attribute(name = "maxx")
   var maxX: Double = 0.0

   @field:Attribute(name = "maxy")
   var maxY: Double = 0.0
}

@Xml
class StringWrapper {
   @TextContent
   var value: String? = null
}