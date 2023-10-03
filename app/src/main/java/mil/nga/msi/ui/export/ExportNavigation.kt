package mil.nga.msi.ui.export

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.navigation.Route

sealed class ExportRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.ASAM.color
): Route {
   object Main: ExportRoute("exportMain", "GeoPackage Export", "GeoPackage Export")
   object Export: ExportRoute("export", "GeoPackage Export", "GeoPackage Export")
}

fun NavGraphBuilder.exportGraph(
   navController: NavController,
   share: (Uri) -> Unit,
   bottomBarVisibility: (Boolean) -> Unit
) {
   navigation(
      route = ExportRoute.Main.name,
      startDestination = ExportRoute.Export.name
   ) {
      composable("${ExportRoute.Export.name}?dataSource={dataSource}") { backstackEntry ->
         bottomBarVisibility(true)

         val dataSource = backstackEntry.arguments?.getString("dataSource")?.let { dataSource ->
            try { DataSource.valueOf(dataSource) } catch(_: Exception) { null }
         }

         GeoPackageExportScreen(
            dataSource = dataSource,
            close = {
               navController.popBackStack()
            },
            onExport = { share(it) }
         )
      }
   }
}