package mil.nga.msi.network.layer.wms

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "ContactInformation", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class ContactInformation(
   @field:Element(name = "ContactPersonPrimary", required = false)
   var person: ContactPersonPrimary? = null,

   @field:Element(name = "ContactVoiceTelephone", required = false)
   var phone: String? = null,

   @field:Element(name = "ContactElectronicMailAddress", required = false)
   var email: String? = null
): Parcelable

@Root(name = "ContactPersonPrimary", strict = false)
@Parcelize
@kotlinx.serialization.Serializable
class ContactPersonPrimary(
   @field:Element(name = "ContactPerson", required = false)
   var name: String? = null,

   @field:Element(name = "ContactOrganization", required = false)
   var organization: String? = null
) : Parcelable

