package mil.nga.msi.ui.route.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.route.create.RouteCreateScreen

sealed class RouteRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
): Route {
    data object Main: RouteRoute("routes/main", "Routes", "Routes")
    data object List: RouteRoute("routes/list", "Routes", "Routes")
    data object Create: RouteRoute("routes/create", "Create Route", "Create")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.routesGraph(
    appState: MarlinAppState,
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
                onCreate = {
                    appState.navController.navigate(RouteRoute.Create.name)
                },
                onAction = { action ->
                    when (action) {
                        else -> {}
                    }
                }
            )
        }
        composable("${RouteRoute.Create.name}") { backstackEntry ->
            bottomBarVisibility(false)

            RouteCreateScreen(
                onBack = { appState.navController.popBackStack() },
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