package mil.nga.msi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorPalette = lightColorScheme(
   primary = SeaGreen,
   primaryContainer = Color.White,
   secondaryContainer = NgaBlue,
   surfaceVariant = Color(red = 245, green = 242, blue = 245),
   tertiary = SeaGreen,
   secondary = NgaBlue
)

val DarkColorPalette = darkColorScheme(
   primary = NgaBlue,
   secondary = Color(0xDDFFFFFF),
   primaryContainer = NgaBlue,
   secondaryContainer = SeaGreen,
   tertiary = Color(0xDDFFFFFF),
   surfaceVariant = Color(red = 42, green = 41, blue = 45),
   onPrimary = Color.White
)

val EmbarkLightColorPalette = lightColorScheme(
   primary = SeaGreen,
   primaryContainer = Color.White,
   surface = SeaGreen,
   surfaceVariant = Color(0x09000000),
   tertiary = SeaGreen,
   secondary = NgaBlue
)

val EmbarkDarkColorPalette = lightColorScheme(
   primary = NgaBlue,
   primaryContainer = Color.White,
   surface = SeaGreen,
   surfaceVariant = Color(0x09000000),
   tertiary = SeaGreen,
   secondary = SeaGreen
)

@Composable
fun MsiTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   content: @Composable () -> Unit
) {
   val colorScheme = if (darkTheme) {
      DarkColorPalette
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
   darkTheme: Boolean = isSystemInDarkTheme(),
   content: @Composable () -> Unit
) {
   val colorScheme = if (darkTheme) {
      EmbarkDarkColorPalette
   } else {
      EmbarkLightColorPalette
   }

   MaterialTheme(
      colorScheme = colorScheme,
      content = content
   )
}

val ColorScheme.onSurfaceDisabled: Color @Composable
   get() = onSurface.copy(alpha = 0.38f)

val ColorScheme.screenBackground: Color @Composable
   get() = Color(0x09000000)

val ColorScheme.add: Color @Composable get() = Add

val ColorScheme.remove: Color @Composable get() = Remove
