package mil.nga.msi.ui.geopackage.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.geopackage.*
import mil.nga.msi.ui.main.TopBar

@Composable
fun GeoPackageMediaScreen(
   key: GeoPackageMediaKey,
   close: () -> Unit,
   viewModel: GeoPackageViewModel = hiltViewModel()
) {
   val media by viewModel.media.observeAsState()

   LaunchedEffect(key) {
      viewModel.setMedia(key)
   }

   Column {
      TopBar(
         title = "GeoPackage Media",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      media?.let {
         Box(Modifier.fillMaxSize()) {
            GeoPackageMedia(media = it)
         }
      }
   }
}

@Composable
private fun GeoPackageMedia(
   media: MediaProperty
) {
   when {
      media.contentType.contains("image/") -> {
         val bitmap = BitmapFactory.decodeByteArray(media.value, 0, media.value.size)
         GeoPackageImage(bitmap)
      }
      media.contentType.contains("video/") -> {
         GeoPackageMediaIcon(Icons.Default.PlayArrow)
      }
      media.contentType.contains("audio/") -> {
         GeoPackageMediaIcon(Icons.Default.VolumeUp)
      }
      else -> {
         GeoPackageMediaIcon(Icons.Default.AttachFile)
      }
   }
}

@Composable
private fun GeoPackageImage(
   bitmap: Bitmap
) {
   Box(
      Modifier
         .fillMaxSize()
         .height(200.dp)
         .clip(androidx.compose.material.MaterialTheme.shapes.medium)
         .background(androidx.compose.material.MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
         .background(Color(0x19FFFFFF))
   ) {
      Image(
         bitmap = bitmap.asImageBitmap(),
         contentDescription = "Image from GeoPackage",
         modifier = Modifier.fillMaxSize()
      )
   }
}

@Composable
private fun GeoPackageMediaIcon(
   icon: ImageVector,
) {
   Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
         .fillMaxSize()
         .height(200.dp)
         .clip(androidx.compose.material.MaterialTheme.shapes.medium)
         .background(androidx.compose.material.MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
   ) {

      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .height(144.dp)
            .width(144.dp)
            .clip(CircleShape)
            .background(Color(0x54000000))
      ) {
         androidx.compose.material.Icon(
            imageVector = icon,
            contentDescription = "Media Icon",
            tint = Color(0xDEFFFFFF),
            modifier = Modifier
               .height(84.dp)
               .width(84.dp)
         )
      }
   }
}