package mil.nga.msi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorPalette = lightColorScheme(
   primary = SeaGreen,
   primaryContainer = Color.White,
   tertiary = SeaGreen,
   secondary = NgaBlue
)

val DarkColorPalette = darkColorScheme(
   primary = NgaBlue,
   tertiary = NgaBlue,
   secondary = NgaBlue
)

@Composable
fun MsiTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   content: @Composable () -> Unit
) {
   val colorScheme = if (darkTheme) {
      LightColorPalette
   } else {
      LightColorPalette
   }

   MaterialTheme(
      colorScheme = colorScheme,
      content = content
   )
}

val Colors.screenBackground: Color @Composable get() = Color(0x09000000)
val Colors.add: Color @Composable get() = Add
val Colors.remove: Color @Composable get() = Remove

