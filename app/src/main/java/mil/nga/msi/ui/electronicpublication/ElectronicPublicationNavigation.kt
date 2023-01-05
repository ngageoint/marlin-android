package mil.nga.msi.ui.electronicpublication

import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.electronicpublication.ElectronicPublicationType
import mil.nga.msi.ui.navigation.Route

sealed class ElectronicPublicationRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
): Route {
    override val color: Color = DataSource.ELECTRONIC_PUBLICATION.color
    object Main: ElectronicPublicationRoute("epubs", "Electronic Publications", "E-Pubs")
    object List: ElectronicPublicationRoute("epubs/types", "Electronic Publications", "E-Pubs")
}

fun routeForPubType(pubType: ElectronicPublicationType): String {
    return "${ElectronicPublicationRoute.List.name}/${pubType.typeId}"
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
            deepLinks = listOf( /* TODO */)
        ) {
            bottomBarVisibility(true)
            ElectronicPublicationsScreen(
                openDrawer = openNavigationDrawer,
                onPubTypeClick = { pubType ->
                    navController.navigate(routeForPubType(pubType))
                }
            )
        }

        composable(
            route = "${ElectronicPublicationRoute.List.name}/{pubType}",
            arguments = listOf(navArgument("pubType") { type = NavType.IntType })
        ) { navBackStackEntry ->
//            val pubTypeCode = navBackStackEntry.arguments?.getInt("pubType")
//            val pubType = ElectronicPublicationType.fromTypeCode(pubTypeCode)
            bottomBarVisibility(true)
            ElectronicPublicationTypeBrowseRoute(
                openDrawer = openNavigationDrawer,
                onBackToRoot = { navController.popBackStack() },
            )
        }
    }
}