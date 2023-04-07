package mil.nga.msi.repository.layer

import android.app.Application
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.inject.Inject
import kotlin.io.path.name

class LayerRepository @Inject constructor(
   private val application: Application,
   private val localDataSource: LayerLocalDataSource,
   private val remoteDataSource: LayerRemoteDataSource
) {
   private val mimeTypeMap = MimeTypeMap.getSingleton()
   private val contentResolver = application.contentResolver

   fun observeLayers() = localDataSource.observeLayers()
   fun observeVisibleLayers() = localDataSource.observeVisibleLayers()

   suspend fun getTile(url: String) = remoteDataSource.getTile(url)
   suspend fun getWMSCapabilities(url: String) = remoteDataSource.getWMSCapabilities(url)

   suspend fun getLayer(id: Long) = localDataSource.getLayer(id)
   suspend fun insertLayer(layer: Layer) = localDataSource.insertLayer(layer)
   suspend fun updateLayer(layer: Layer) = localDataSource.updateLayer(layer)
   suspend fun enabledLayer(layer: Layer, enabled: Boolean) = localDataSource.enableLayer(layer, enabled)
   suspend fun deleteLayer(layer: Layer) = localDataSource.deleteLayer(layer)

   suspend fun stageGeoPackageFile(uri: Uri): File = withContext(Dispatchers.IO) {
      val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ?: GEOPACKAGE_EXTENSION
      val filename = "${UUID.randomUUID()}.$extension"

      val path = Paths.get(application.cacheDir.absolutePath, GEOPACKAGE_PATH, filename)
      Files.createDirectories(path.parent)

      contentResolver.openInputStream(uri)?.use { input ->
         Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
      }

      path.toFile()
   }

   suspend fun createLayer(layer: Layer) = withContext(Dispatchers.IO) {
      if (layer.type == LayerType.GEOPACKAGE) {
         layer.filePath?.let {
            val file = saveGeoPackageFile(it)
            val geoPackageLayer = layer.copy(filePath = file.absolutePath)
            localDataSource.insertLayer(geoPackageLayer)
         }
      } else {
         localDataSource.insertLayer(layer)
      }
   }

   private suspend fun saveGeoPackageFile(geoPackage: String): File = withContext(Dispatchers.IO) {
      // Move  geopackage file to non cache directory
      val cacheFile = Paths.get(geoPackage)
      val file = Paths.get(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath, GEOPACKAGE_PATH, cacheFile.name)

      Files.createDirectories(file.parent)
      Files.copy(cacheFile, file, StandardCopyOption.REPLACE_EXISTING)
      Files.delete(cacheFile)

      file.toFile()
   }

   companion object {
      private const val GEOPACKAGE_PATH = "geopackages"
      private const val GEOPACKAGE_EXTENSION = "gpkg"
   }
}