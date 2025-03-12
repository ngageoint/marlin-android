package mil.nga.msi.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import mil.nga.msi.R

class BitmapDescriptorFactory {
   companion object {
      @SuppressLint("InflateParams")
      fun fromResource(
         context: Context,
         resource: Int,
         label: String
      ): BitmapDescriptor {
         val iconGenerator = IconGenerator(context)
         val markerView: View = LayoutInflater.from(context).inflate(R.layout.view_label_marker, null)
         val marker = markerView.findViewById<ImageView>(R.id.marker)
         marker.setImageResource(resource)
         val labelTextView = markerView.findViewById<TextView>(R.id.label)
         labelTextView.text = label
         iconGenerator.setContentView(markerView)
         iconGenerator.setBackground(null)
         val icon = iconGenerator.makeIcon(label)
         return BitmapDescriptorFactory.fromBitmap(icon)
      }
   }
}