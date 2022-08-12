package mil.nga.msi.datasource.light

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

enum class LightColor(val color: Color) {
   WHITE(Color(0xDEFFFF00)),
   YELLOW(Color(0xFFFFFF00)),
   GREEN(Color(0xFF0DE319)),
   RED(Color(0xFFFA0000)),
   BLUE(Color(0xFF0000FF)),
   VIOLET(Color(0xFFAF52DE)),
   ORANGE(Color(0xFFFF9500)),
   BUOY(Color(0xFF87978b)),
   RACON(Color(0xFFB52BB5))
}

@Entity(
   tableName = "lights",
   primaryKeys = ["volume_number", "feature_number", "characteristic_number"]
)
data class Light(
   @ColumnInfo(name = "volume_number")
   val volumeNumber: String,

   @ColumnInfo(name = "feature_number")
   val featureNumber: String,

   @ColumnInfo(name = "characteristic_number")
   val characteristicNumber: Int,

   @ColumnInfo(name = "notice_week")
   var noticeWeek: String,

   @ColumnInfo(name = "notice_year")
   var noticeYear: String,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "international_feature")
   var internationalFeature: String? = null

   @ColumnInfo(name = "aid_type")
   var aidType: String? = null

   @ColumnInfo(name = "geopolitical_heading")
   var geopoliticalHeading: String? = null

   @ColumnInfo(name = "region_heading")
   var regionHeading: String? = null

   @ColumnInfo(name = "subregion_heading")
   var subregionHeading: String? = null

   @ColumnInfo(name = "local_heading")
   var localHeading: String? = null

   @ColumnInfo(name = "preceding_note")
   var precedingNote: String? = null

   @ColumnInfo(name = "name")
   var name: String? = null

   @ColumnInfo(name = "position")
   var position: String? = null

   @ColumnInfo(name = "characteristic")
   var characteristic: String? = null

   @ColumnInfo(name = "height_feet")
   var heightFeet: Float? = null

   @ColumnInfo(name = "height_meters")
   var heightMeters: Float? = null

   @ColumnInfo(name = "range")
   var range: String? = null

   @ColumnInfo(name = "structure")
   var structure: String? = null

   @ColumnInfo(name = "remarks")
   var remarks: String? = null

   @ColumnInfo(name = "post_note")
   var postNote: String? = null

   @ColumnInfo(name = "notice_number")
   var noticeNumber: Int? = null

   @ColumnInfo(name = "remove_from_list")
   var removeFromList: String? = null

   @ColumnInfo(name = "delete_flag")
   var deleteFlag: String? = null

   @ColumnInfo(name = "section_header")
   var sectionHeader: String = ""

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))

   fun isFogSignal(): Boolean {
      return name?.contains("fog signal", ignoreCase = true) ?: false
   }

   fun isBuoy(): Boolean {
      return structure?.contains("pillar", ignoreCase = true) == true ||
         structure?.contains("spar", ignoreCase = true) == true ||
         structure?.contains("conical", ignoreCase = true) == true ||
         structure?.contains("can", ignoreCase = true) == true
   }

   fun lightColors(): List<Color>  {
      val lightColors = mutableListOf<Color>()

      characteristic?.let { characteristic->
         if (characteristic.contains("W.")) {
            lightColors.add(LightColor.WHITE.color)
         }
         if (characteristic.contains("R.")) {
            lightColors.add(LightColor.RED.color)
         }
         // why does green have so many variants without a .?
         if (characteristic.contains("G.")
               || characteristic.contains("Oc.G")
               || characteristic.contains("G\n")
               || characteristic.contains("F.G")
               || characteristic.contains("Fl.G")
               || characteristic.contains("(G)")) {
            lightColors.add(LightColor.GREEN.color)
         }

         if (characteristic.contains("Y.")) {
            lightColors.add(LightColor.YELLOW.color)
         }

         if (characteristic.contains("Bu.")) {
            lightColors.add(LightColor.BLUE.color)
         }

         if (characteristic.contains("Vi.")) {
            lightColors.add(LightColor.VIOLET.color)
         }

         if (characteristic.contains("Or.")) {
            lightColors.add(LightColor.ORANGE.color)
         }

         if (lightColors.isEmpty() && characteristic.lowercase().contains("lit")) {
            lightColors.add(LightColor.WHITE.color)
         }
      }

      return lightColors
   }

   fun lightSectors(): List<LightSector> {
      val sectors = mutableListOf<LightSector>()

      val regex = Regex("(?<visible>(Visible)?)((?<color>[A-Z]+)?)\\.?(?<unintensified>(\\(unintensified\\))?)( (?<startdeg>(\\d*))°)?((?<startminutes>[0-9]*)[\\`'])?(-(?<enddeg>(\\d*))°)(?<endminutes>[0-9]*)[\\`']?")


      remarks?.let { remarks ->
         var previousEnd = 0.0

         regex.findAll(remarks).forEach {  matchResult ->
            val groups =  matchResult.groups as? MatchNamedGroupCollection

            if (groups != null) {
               var end = 0.0
               var start: Double? = null
               var color = ""
               var visibleColor: Color? = null

               arrayOf("visible", "color", "startdeg", "startminutes", "enddeg", "endminutes").forEach { name ->
                  val component = groups[name]
                  if (component != null) {
                     if (name == "visible") {
                        visibleColor = lightColors()[0]
                     } else if (name == "color") {
                        color = remarks.substring(component.range)
                     } else if (name == "startdeg") {
                        start = (start ?: 0.0) + (remarks.substring(component.range).toDoubleOrNull() ?: 0.0)
                     } else if (name == "startminutes") {
                        start = (start ?: 0.0) + ((remarks.substring(component.range).toDoubleOrNull() ?: 0.0)  / 60)
                     } else if (name == "enddeg") {
                        end = remarks.substring(component.range).toDoubleOrNull() ?: 0.0
                     } else if (name == "endminutes") {
                        end += (remarks.substring(component.range).toDoubleOrNull() ?: 0.0) / 60
                     }
                  }
               }

               val uiColor = when(color) {
                  "W" -> LightColor.WHITE.color
                  "R" -> LightColor.RED.color
                  "G" -> LightColor.GREEN.color
                  else -> visibleColor ?: Color.Transparent
               }

               val startDegrees = start
               if (startDegrees != null) {
                  sectors.add(LightSector(startDegrees, end, uiColor, color))
               } else {
                  if (end < previousEnd) {
                     end += 360.0
                  }
                  sectors.add(LightSector(previousEnd, end, uiColor, color))
               }

               previousEnd = end
            }
         }
      }

      return sectors
   }

   @Transient
   val expandedCharacteristic = {
      var expanded = characteristic
      expanded = expanded?.replace("Al.", "Alternating ")
      expanded = expanded?.replace("lt.","Lit ")
      expanded = expanded?.replace("bl.","Blast ")
      expanded = expanded?.replace("Mo.","Morse code ")
      expanded = expanded?.replace("Bu.","Blue ")
      expanded = expanded?.replace("min.","Minute ")
      expanded = expanded?.replace("Dir.","Directional ")
      expanded = expanded?.replace("obsc.","Obscured ")
      expanded = expanded?.replace("ec.","Eclipsed ")
      expanded = expanded?.replace("Oc.","Occulting ")
      expanded = expanded?.replace("ev.","Every ")
      expanded = expanded?.replace("Or.","Orange ")
      expanded = expanded?.replace("F.","Fixed ")
      expanded = expanded?.replace("Q.","Quick Flashing ")
      expanded = expanded?.replace("L.Fl.","Long Flashing ")
      expanded = expanded?.replace("Fl.","Flashing ")
      expanded = expanded?.replace("R.","Red ")
      expanded = expanded?.replace("fl.","Flash ")
      expanded = expanded?.replace("s.","Seconds ")
      expanded = expanded?.replace("G.","Green ")
      expanded = expanded?.replace("si.","Silent ")
      expanded = expanded?.replace("horiz.","Horizontal ")
      expanded = expanded?.replace("U.Q.","Ultra Quick ")
      expanded = expanded?.replace("flashing intes.","Intensified ")
      expanded = expanded?.replace("I.Q.","Interrupted Quick ")
      expanded = expanded?.replace("flashing unintens.","Unintensified ")
      expanded = expanded?.replace("vert.","Vertical ")
      expanded = expanded?.replace("Iso.","Isophase ")
      expanded = expanded?.replace("Vi.", "Violet ")
      expanded = expanded?.replace("I.V.Q.","Interrupted Very Quick Flashing ")
      expanded = expanded?.replace("vis.","Visible ")
      expanded = expanded?.replace("V.Q.","Very Quick ")
      expanded = expanded?.replace("Km.","Kilometer ")
      expanded = expanded?.replace("W.","White ")
      expanded = expanded?.replace("Y.","Yellow ")

      expanded
   }

   fun isRacon(): Boolean {
      val name = name ?: return false
      return name.contains("RACON") || remarks?.contains("(3 & 10cm)") == true
   }

   fun morseCode(): String? {
      val firstIndex = characteristic?.indexOfFirst { it == '(' }?.takeIf { it >= 0 }
      val lastIndex = characteristic?.indexOfLast { it == ')' }?.takeIf { it >= 0 }
      return if (firstIndex != null && lastIndex != null) {
         characteristic?.substring(IntRange(firstIndex + 1, lastIndex - 1))
      } else null
   }
}