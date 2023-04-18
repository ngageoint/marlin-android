package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.preferences.Credentials
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GridTileProvider
import mil.nga.msi.ui.map.settings.layers.geopackage.GeopackageLayerViewModel
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerViewModel
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerViewModel

@Composable
fun MapNewLayerScreen(
   onClose: () -> Unit,
   onLayer: (Layer, Credentials?) -> Unit,
   wmsViewModel: MapWMSLayerViewModel = hiltViewModel(),
   gridViewModel: MapGridLayerViewModel = hiltViewModel(),
   geoPackageViewModel: GeopackageLayerViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val scrollState = rememberScrollState()
   var url by remember { mutableStateOf("") }
   var type by remember { mutableStateOf<LayerType?>(null) }
   val tileUrl by gridViewModel.tileUrl.observeAsState()
   val tileError by gridViewModel.fetchError.observeAsState(false)
   val wmsState by wmsViewModel.wmsState.observeAsState()
   val wmsError by wmsViewModel.fetchError.observeAsState(false)
   val credentials by wmsViewModel.credentials.observeAsState()

   LaunchedEffect(tileUrl) {
      if (tileUrl != null && type == null) {
         type = LayerType.XYZ
      }
   }

   LaunchedEffect(wmsState?.wmsCapabilities) {
      if (wmsState?.wmsCapabilities?.isValid() == true && type == null) {
         type = LayerType.WMS
      }
   }

   Column {
      TopBar(
         title = MapLayerRoute.NewLayer.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { onClose() }
      )

      Surface {
         Column(
            Modifier
               .padding(horizontal = 16.dp)
               .fillMaxHeight()
               .verticalScroll(scrollState)
         ) {
            Layer(
               url = url,
               type = type,
               serverError = tileError && wmsError,
               onUrlChanged = {
                  url = it
                  wmsViewModel.setUrl(it, credentials)
                  gridViewModel.setUrl(it, credentials)
               },
               onTypeChanged = { type = it },
               onGeoPackageUri = { uri ->
                  scope.launch {
                     val geopackageFile = geoPackageViewModel.getGeoPackage(uri)
                     onLayer(
                        Layer(
                           name = "",
                           type = LayerType.GEOPACKAGE,
                           url = Uri.fromFile(geopackageFile).toString(),
                           filePath = geopackageFile?.absolutePath
                        ),
                        credentials
                     )
                  }
               }
            )

            tileUrl?.let { uri ->
               MapLayer(
                  url = uri,
                  type = type,
                  service = wmsViewModel.layerService,
                  modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f)
               )
            }

            wmsState?.wmsCapabilities?.let { wmsCapabilities ->
               WMSCapabilities(wmsCapabilities)
            }

            Credentials(
               username = credentials?.username ?: "",
               password = credentials?.password ?: "",
               onUsernameChange = { wmsViewModel.setUsername(it) },
               onPasswordChange = { wmsViewModel.setPassword(it) }
            )

            if (tileUrl != null || wmsState?.wmsCapabilities?.isValid() == true) {
               Button(
                  onClick = {
                     scope.launch {
                        if (tileUrl != null) {
                           type?.let {
                              val layer = Layer(
                                 name = "",
                                 type = it,
                                 url = tileUrl.toString()
                              )
                              onLayer(layer, credentials)
                           }
                        }

                        wmsState?.wmsCapabilities?.let {
                           val layer = Layer(
                              name = "",
                              type = LayerType.WMS,
                              url = url
                           )

                           onLayer(layer, credentials)
                        }
                     }
                  },
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(16.dp)
               ) {
                  Text(text = "Confirm URL")
               }
            }
         }
      }
   }
}

@Composable
private fun NoServer() {
   Card {
      Column(Modifier.padding(16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = "Unable to retrieve WMS Capabilities Document or an X/Y/Z tile.  If you believe the url is correct please choose the tile server type below to continue.",
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "- or -",
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = "Continue typing",
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.fillMaxWidth()
            )
         }
      }
   }
}

@Composable
private fun Layer(
   url: String,
   type: LayerType?,
   serverError: Boolean,
   onUrlChanged: (String) -> Unit,
   onTypeChanged: (LayerType) -> Unit,
   onGeoPackageUri: (Uri) -> Unit
) {
   val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      uri?.let { onGeoPackageUri(uri) }
   }

   Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
         .fillMaxWidth()
         .padding(vertical = 24.dp)
   ) {
      Button(
         onClick = {
            launcher.launch(arrayOf("*/gpkg", "*/gpkx", "application/octet-stream"))
         }
      ) {
         Text("Import GeoPackage File")
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "- or -",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = buildAnnotatedString {
               append("Enter a layer URL, Marlin will do its best to auto detect the type of your layer.")

               withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(" NOTE: If tiles appear misaligned toggle between XYZ and TMS types.")
               }
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
               .fillMaxWidth()
               .padding(bottom = 24.dp)
         )
      }

      TextField(
         value = url,
         label = { Text("Layer URL") },
         onValueChange = { onUrlChanged(it) },
         modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
      )

      if (serverError && type != null) {
         NoServer()
      }

      Row(
         horizontalArrangement = Arrangement.Center,
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
      ) {

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.XYZ,
               onClick = { onTypeChanged(LayerType.XYZ) }
            )

            Text(text = "XYZ")
         }

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.TMS,
               onClick = { onTypeChanged(LayerType.TMS) }
            )

            Text(text = "TMS")
         }

         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
         ) {
            RadioButton(
               selected = type == LayerType.WMS,
               onClick = { onTypeChanged(LayerType.WMS) }
            )

            Text(text = "WMS")
         }
      }
   }
}

@Composable
private fun MapLayer(
   url: Uri,
   type: LayerType?,
   service: LayerService,
   modifier: Modifier = Modifier
) {
   GoogleMap(
      properties = MapProperties(mapType = MapType.NORMAL),
      uiSettings = MapUiSettings(compassEnabled = false),
      modifier = modifier
   ) {
      tileOverlayOptions {
         TileOverlay(
            tileProvider = GridTileProvider(
               service = service,
               baseUrl = url,
               invertYAxis = type == LayerType.TMS
            )
         )
      }
   }
}

@Composable
private fun WMSCapabilities(
   wmsCapabilities: WMSCapabilities
) {
   var expanded by remember { mutableStateOf(false) }

   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "WMS SERVER INFORMATION",
         style = MaterialTheme.typography.bodyMedium,
         modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
      )
   }

   Card {
      Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
         Text(
            text = wmsCapabilities.service?.title ?: "WMS Server",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
               .fillMaxWidth()
               .padding(top = 8.dp, bottom = 16.dp)
         )

         wmsCapabilities.service?.abstract?.let { abstract ->
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = abstract,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(bottom = 16.dp)
               )
            }
         }

         if (wmsCapabilities.capability?.request?.map?.hasImageFormat() != true) {
            Text(
               text = "WMS server does not support image tiles",
               style = MaterialTheme.typography.titleMedium,
               color = MaterialTheme.colorScheme.error,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp, bottom = 16.dp)
            )
         }

         if (!expanded) {
            IconButton(
               onClick = { expanded = true },
               modifier = Modifier.align(Alignment.End)
            ) {
               Icon(Icons.Default.MoreHoriz,
                  tint = MaterialTheme.colorScheme.primary,
                  contentDescription = "Zoom to ASAM"
               )
            }
         }

         Column(Modifier.animateContentSize()) {
            if (expanded) {
               wmsCapabilities.version?.let {
                  WMSServiceProperty(name = "WMS Version", value = it)
               }

               wmsCapabilities.service?.contactInformation?.person?.name?.let {
                  WMSServiceProperty(name = "Contact Person", value = it)
               }

               wmsCapabilities.service?.contactInformation?.person?.organization?.let {
                  WMSServiceProperty(name = "Contact Organization", value = it)
               }

               wmsCapabilities.service?.contactInformation?.phone?.let {
                  WMSServiceProperty(name = "Contact Phone", value = it)
               }

               wmsCapabilities.service?.contactInformation?.email?.let {
                  WMSServiceProperty(name = "Contact Phone", value = it)
               }
            }
         }
      }
   }
}

@Composable
private fun WMSServiceProperty(
   name: String,
   value: String
) {
   Column(
      Modifier.padding(vertical = 8.dp)
   ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
         Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
         )
      }
   }
}

@Composable
private fun Credentials(
   username: String,
   password: String,
   onUsernameChange: (String) -> Unit,
   onPasswordChange: (String) -> Unit
) {
   var showCredentials by remember { mutableStateOf(false) }
   var passwordVisible by rememberSaveable { mutableStateOf(false) }

   Column {
      TextButton(
         onClick = { showCredentials = !showCredentials }
      ) {
         Text("Add Credentials")
      }

      if (showCredentials) {
         TextField(
            value = username,
            label = { Text("Username") },
            onValueChange = { onUsernameChange(it) },
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 8.dp)
         )

         TextField(
            value = password,
            label = { Text("Password") },
            onValueChange = { onPasswordChange(it) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
               val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
               IconButton(onClick = {passwordVisible = !passwordVisible}){
                  Icon(
                     imageVector = image,
                     contentDescription = "hide/show password"
                  )
               }
            },
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 8.dp)
         )
      }
   }
}