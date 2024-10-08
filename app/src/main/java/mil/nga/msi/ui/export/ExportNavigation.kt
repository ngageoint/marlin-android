package mil.nga.msi.ui.export

import android.net.Uri
import androidx.core.os.BundleCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.NavTypeDataSources
import mil.nga.msi.ui.navigation.Route

sealed class ExportRoute(
   override val name: String,
   override val title: String = "GeoPackage Export",
   override val shortTitle: String = "GeoPackage Export"
): Route {
   data object Main: ExportRoute("exportMain")
   data object Export: ExportRoute("export")
}

fun NavGraphBuilder.exportGraph(
   appState: MarlinAppState,
   share: (Uri) -> Unit,
   bottomBarVisibility: (Boolean) -> Unit
) {
   navigation(
      route = ExportRoute.Main.name,
      startDestination = ExportRoute.Export.name
   ) {
      composable(
         route = "${ExportRoute.Export.name}?dataSources={dataSources}",
         arguments = listOf(navArgument("dataSources") { type = NavType.NavTypeDataSources })
      ) { backstackEntry ->
         bottomBarVisibility(true)

         val dataSources = backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelableArray(bundle, "dataSources", ExportDataSource::class.java)?.map { it as ExportDataSource }?.toList()
         } ?: emptyList()

         GeoPackageExportScreen(
            exportDataSources = dataSources,
            close = { appState.navController.popBackStack() },
            onExport = { share(it) }
         )
      }
   }
}