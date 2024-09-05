package mil.nga.msi.ui.report

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.R
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route

sealed class ReportRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   val icon: Int = 0,
   val url: String = ""
): Route {
   data object Main: ReportRoute("report", "Submit a Report to NGA")
   data object List: ReportRoute("report/list", "NGA Report Submission")
   
   data object MODU: ReportRoute(
      name = "report/modu",
      title = "Mobile offshore Drilling Unit Movement Report MODU",
      shortTitle = "MODU Report",
      icon = R.drawable.ic_modu_24dp,
      url = "https://msi.nga.mil/submit-report/MODU-Report"
   )

   data object Observer: ReportRoute(
      name = "report/observer",
      title = "Observer Report",
      shortTitle = "Observer Report",
      icon = R.drawable.ic_baseline_remove_red_eye_24,
      url = "https://msi.nga.mil/submit-report/Observ-Report"
   )

   data object PortVisit: ReportRoute(
      name = "report/portVisit",
      title = "US Navy Port Visit Request",
      shortTitle = "Port Visit Request",
      icon = R.drawable.ic_baseline_anchor_24,
      url = "https://msi.nga.mil/submit-report/Visit-Report"
   )

   data object HostileShip: ReportRoute(
      name = "report/hostileShip",
      title = "Ship Hostile Action Report",
      shortTitle = "Ship Hostile Action Report",
      icon = R.drawable.ic_baseline_directions_boat_24,
      url = "https://msi.nga.mil/submit-report/SHAR-Report"
   )
}

fun NavGraphBuilder.reportGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit
) {
   navigation(
      route = ReportRoute.Main.name,
      startDestination = ReportRoute.List.name
   ) {

      composable(ReportRoute.List.name) {
         bottomBarVisibility(true)

         ReportsScreen(
            close = { appState.navController.popBackStack() },
            onTap = { route -> appState.navController.navigate(route.name) }
         )
      }

      composable(ReportRoute.Observer.name) {
         bottomBarVisibility(true)

         ReportPage(ReportRoute.Observer) { appState.navController.popBackStack() }
      }

      composable(ReportRoute.MODU.name) {
         bottomBarVisibility(true)

         ReportPage(ReportRoute.MODU) { appState.navController.popBackStack() }
      }

      composable(ReportRoute.PortVisit.name) {
         bottomBarVisibility(true)

         ReportPage(ReportRoute.PortVisit) { appState.navController.popBackStack() }
      }

      composable(ReportRoute.HostileShip.name) {
         bottomBarVisibility(true)

         ReportPage(ReportRoute.HostileShip) { appState.navController.popBackStack() }
      }
   }
}