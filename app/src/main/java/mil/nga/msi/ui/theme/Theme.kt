package mil.nga.msi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorPalette = lightColorScheme(
   primary = SeaGreen,
   primaryContainer = Color.White,
   surface = Color.White,
   surfaceVariant = Color(0x09000000),
   tertiary = SeaGreen,
   secondary = NgaBlue,
)

val DarkColorPalette = darkColorScheme(
   primary = NgaBlue,
   tertiary = NgaBlue,
   secondary = NgaBlue
)

val EmbarkColorPalette = lightColorScheme(
   primary = SeaGreen,
   primaryContainer = Color.White,
   surface = SeaGreen,
   surfaceVariant = Color(0x09000000),
   tertiary = SeaGreen,
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

@Composable
fun MsiEmbarkTheme (
   content: @Composable () -> Unit
) {
   MaterialTheme(
      colorScheme = EmbarkColorPalette,
      content = content
   )
}

val ColorScheme.add: Color @Composable get() = Add
val ColorScheme.remove: Color @Composable get() = Remove
val ColorScheme.onSurfaceDisabled: Color @Composable
   get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)



