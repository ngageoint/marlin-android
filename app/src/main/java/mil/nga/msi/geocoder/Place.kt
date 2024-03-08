package mil.nga.msi.geocoder

import android.location.Address
import com.google.android.gms.maps.model.LatLng

data class Place(
   val name: String,
   val address: String? = null,
   val location: LatLng
) {
   companion object {
      fun fromAddress(address: Address): Place {
         return Place(
            name = address.featureName,
            address = address.getAddressLine(0)?.toString(),
            location = LatLng(address.latitude, address.longitude)
         )
      }
   }
}