package mil.nga.msi.ui.route.list

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import mil.nga.msi.ui.navigation.Route

sealed class RouteRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
    override val color: Color = Color.Transparent
): Route {
    data object Main: RouteRoute("routes/main", "Routes", "Routes")
    data object List: RouteRoute("routes/list", "Routes", "Routes")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.routesGraph(
    navController: NavController,
    bottomBarVisibility: (Boolean) -> Unit,
    openNavigationDrawer: () -> Unit
) {
    navigation(
        route = RouteRoute.Main.name,
        startDestination = RouteRoute.List.name
    ) {
        composable(
            route = RouteRoute.List.name,
            deepLinks = listOf(navDeepLink { uriPattern = "marlin://${RouteRoute.List.name}" })
        ) {
            bottomBarVisibility(true)

            RoutesScreen(
                openDrawer = { openNavigationDrawer() },
                onAction = { action ->
                    when (action) {
                        else -> {}
                    }
                }
            )
        }

//        bottomSheet(
//            route = "${RouteRoute.Notes.name}?route={route}",
//            arguments = listOf(navArgument("bookmark") { type = NavType.NavTypeBookmark })
//        ) { backstackEntry ->
//            backstackEntry.arguments?.let { bundle ->
//                BundleCompat.getParcelable(bundle, "bookmark", BookmarkKey::class.java)
//            }?.let { bookmark ->
//                BookmarkNotesScreen(
//                    bookmark = bookmark,
//                    onDone = { navController.popBackStack() }
//                )
//            }
//        }
    }
}