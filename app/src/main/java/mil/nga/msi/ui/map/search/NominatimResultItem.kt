package mil.nga.msi.ui.map.search

import com.google.gson.annotations.SerializedName

data class NominatimResultItem(
   val name: String,

   @SerializedName(value="display_name")
   val displayName: String,

   val lat: String,

   val lon: String
)