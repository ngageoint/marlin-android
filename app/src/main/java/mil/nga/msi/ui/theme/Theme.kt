package mil.nga.msi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
   primary = NgaBlue,
   primaryVariant = NgaBlue,
   secondary = NgaBlue
)

private val LightColorPalette = lightColors(
   primary = SeaGreen,
   primaryVariant = SeaGreen,
   secondary = SeaGreen
)

@Composable
fun MsiTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   content: @Composable () -> Unit
) {
   val colors = if (darkTheme) {
      DarkColorPalette
   } else {
      LightColorPalette
   }

   MaterialTheme(
      colors = colors,
      content = content
   )
}

val Colors.screenBackground: Color @Composable get() = Color(0x09000000)
val Colors.add: Color @Composable get() = Add
val Colors.remove: Color @Composable get() = Remove

