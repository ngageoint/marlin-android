package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.ui.route.list.RouteRoute

sealed class RouteAction: Action() {
    class Tap(private val route: Route): RouteAction() {
        override fun navigate(navController: NavController) {
            navController.navigate("${RouteRoute.Detail.name}?routeId=${route.id}")
        }
    }
}
