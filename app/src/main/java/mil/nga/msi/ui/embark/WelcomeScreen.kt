package mil.nga.msi.ui.embark

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import mil.nga.msi.R

@Composable
fun WelcomeScreen(
   done: () -> Unit
) {
   Surface {
      Welcome {
         done()
      }
   }
}

@Composable
private fun Welcome(
   done: () -> Unit,
) {
   var height by remember { mutableIntStateOf(0) }

   Column(
      modifier = Modifier
         .fillMaxSize()
         .background(
            brush = Brush.verticalGradient(
               startY = height * .37f,
               colors = listOf(
                  MaterialTheme.colorScheme.primary,
                  MaterialTheme.colorScheme.secondary
               )
            )
         )
         .padding(vertical = 48.dp, horizontal = 32.dp)
         .onGloballyPositioned { coordinates ->
            height = coordinates.size.height
         }
   ) {
      Column {
         Text(
            text = "Welcome to Marlin",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
            modifier =
            Modifier
               .align(CenterHorizontally)
               .padding(bottom = 16.dp)
         )

         Text(
            text = "Marlin puts NGA's Maritime Safety Information datasets at your fingertips even when offline.  The next few screens will allow you to customize your experience to meet your needs.",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(CenterHorizontally)
         )
      }

      Box(Modifier.weight(1f)) {
         if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val vectorDrawable = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_marlin_600dp)!!
            val bitmap3 = Bitmap.createBitmap(
               vectorDrawable.intrinsicWidth,
               vectorDrawable.intrinsicHeight,
               Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap3)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)

            val bitmap = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_marlin_600dp)!!.toBitmap().asImageBitmap()
            Icon(
               bitmap = bitmap,
               modifier = Modifier
                  .padding(vertical = 48.dp)
                  .fillMaxSize(),
               tint = Color.Unspecified,
               contentDescription = "Marlin App Icon"
            )
         }
      }

      Button(
         onClick = { done() },
         modifier = Modifier
            .align(CenterHorizontally),
         shape = RoundedCornerShape(38.dp)
      ) {
         Text(
            text = "Set Sail",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
         )
      }
   }
}