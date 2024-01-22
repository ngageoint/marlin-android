package mil.nga.msi.ui.route.list

import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.ui.map.AnnotationProvider
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.route.create.RouteCreateScreen
import mil.nga.msi.ui.route.sheet.RouteBottomSheet

sealed class RouteRoute(
    override val name: String,
    override val title: String,
    override val shortTitle: String,
): Route {
    data object Main: RouteRoute("routes/main", "Routes", "Routes")
    data object List: RouteRoute("routes/list", "Routes", "Routes")
    data object Detail: RouteRoute("routes/detail", "Route", "Route")
    data object Create: RouteRoute("routes/create", "Create Route", "Create")
    data object PagerSheet: RouteRoute("routes/annotationPagerSheet", "Routes", "Routes")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.routesGraph(
    appState: MarlinAppState,
    bottomBarVisibility: (Boolean) -> Unit,
    openNavigationDrawer: () -> Unit,
    annotationProvider: AnnotationProvider
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
                    when(action) {
                        else -> { action.navigate(appState.navController) }
                    }
                }
            )
        }
        composable("${RouteRoute.Create.name}") { backstackEntry ->
            bottomBarVisibility(false)

            val navStackBackEntry by appState.navController.currentBackStackEntryAsState()
            if (navStackBackEntry?.destination?.route?.startsWith(RouteRoute.Create.name) == true) {
                annotationProvider.setMapAnnotation(null)
            }
            RouteCreateScreen(
                onBack = { appState.navController.popBackStack() },
                onMapTap = { appState.navController.navigate(RouteRoute.PagerSheet.name) },
            )
        }

        composable("${RouteRoute.Detail.name}?routeId={routeId}") { backstackEntry ->
            bottomBarVisibility(false)

            backstackEntry.arguments?.getString("routeId")?.toLongOrNull()?.let { routeId ->
                RouteCreateScreen(
                    routeId = routeId,
                    onBack = { appState.navController.popBackStack() },
                    onMapTap = { appState.navController.navigate(RouteRoute.PagerSheet.name) }
                )
            }
        }

        bottomSheet(RouteRoute.PagerSheet.name) {
            RouteBottomSheet()
        }
    }
}