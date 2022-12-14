package mil.nga.msi.ui.electronicpublication

import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.navigation.Route

sealed class ElectronicPublicationRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
    override val color: Color = DataSource.ELECTRONIC_PUBLICATION.color
): Route {
    object Main: ElectronicPublicationRoute("epubs", "Electronic Publications", "E-Pubs")
    object Detail: ElectronicPublicationRoute("epubs/detail", "Electronic Publication Details", "E-Pub Details")
    object List: ElectronicPublicationRoute("epubs/list", "Electronic Publications", "E-Pubs")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.electronicPublicationGraph(
    navController: NavController,
    bottomBarVisibility: (Boolean) -> Unit,
    openNavigationDrawer: () -> Unit,
    share: (Pair<String, String>) -> Unit,
    showSnackbar: (String) -> Unit
) {
    navigation(
        route = ElectronicPublicationRoute.Main.name,
        startDestination = ElectronicPublicationRoute.List.name
    ) {
        composable(
            route = ElectronicPublicationRoute.List.name,
            deepLinks = listOf(navDeepLink { uriPattern = "marlin://${ElectronicPublicationRoute.List.name}" })
        ) {
            bottomBarVisibility(true)
            ElectronicPublicationsScreen(
                openDrawer = openNavigationDrawer,
//                onTap = { key ->
//                    val encoded = Uri.encode(Json.encodeToString(key))
//                    navController.navigate( "${DgpsStationRoute.Detail.name}?key=$encoded")
//                },
//                onAction = { action ->
//                    when(action) {
//                        is DgpsStationAction.Zoom -> zoomTo(action.point)
//                        is DgpsStationAction.Share -> shareLight(action.text)
//                        is DgpsStationAction.Location -> showSnackbar("${action.text} copied to clipboard")
//                    }
//                }
            )
        }

//        composable(
//            route = "${ElectronicPublicationRoute.Detail.name}?key={key}",
//            arguments = listOf(navArgument("key") { type = NavType.DgpsStation })
//        ) { backstackEntry ->
//            bottomBarVisibility(false)
//
//            backstackEntry.arguments?.getParcelable<DgpsStationKey>("key")?.let { key ->
//                DgpsStationDetailScreen(
//                    key,
//                    close = { navController.popBackStack() },
//                    onAction = { action ->
//                        when(action) {
//                            is DgpsStationAction.Zoom -> zoomTo(action.point)
//                            is DgpsStationAction.Share -> shareLight(action.text)
//                            is DgpsStationAction.Location -> showSnackbar("${action.text} copied to clipboard")
//                        }
//                    }
//                )
//            }
//        }
//
//        bottomSheet(
//            route = "${DgpsStationRoute.Sheet.name}?key={key}",
//            arguments = listOf(navArgument("key") { type = NavType.DgpsStation })
//        ) { backstackEntry ->
//            backstackEntry.arguments?.getParcelable<DgpsStationKey>("key")?.let { key ->
//                DgpsStationSheetScreen(key, onDetails = {
//                    val encoded = Uri.encode(Json.encodeToString(key))
//                    navController.navigate( "${DgpsStationRoute.Detail.name}?key=$encoded")
//                })
//            }
//        }
//
//        bottomSheet(DgpsStationRoute.Filter.name) {
//            FilterScreen(
//                dataSource = DataSource.DGPS_STATION,
//                close = {
//                    navController.popBackStack()
//                }
//            )
//        }
    }
}