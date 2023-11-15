package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.ui.route.list.RouteRoute

sealed class RouteAction {
    class Tap(private val radioBeacon: RadioBeacon): RadioBeaconAction() {
        override fun navigate(navController: NavController) {
            navController.navigate( RouteRoute.Main.name )
        }
    }
}
