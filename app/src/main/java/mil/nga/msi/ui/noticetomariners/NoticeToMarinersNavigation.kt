package mil.nga.msi.ui.noticetomariners

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.navigation.Route

sealed class NoticeToMarinersRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.ASAM.color
): Route {
   object Main: NoticeToMarinersRoute("ntms", "Notice To Mariners", "NTMs")
   object Home: NoticeToMarinersRoute("ntms/home", "Notice To Mariners", "NTMs")
   object All: NoticeToMarinersRoute("ntms/all", "Notice To Mariners", "NTMs")
}

fun NavGraphBuilder.noticeToMarinersGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
) {
   navigation(
      route = NoticeToMarinersRoute.Main.name,
      startDestination = NoticeToMarinersRoute.Home.name
   ) {
      composable(
         route = NoticeToMarinersRoute.Home.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${NoticeToMarinersRoute.Home.name}" })
      ) {
         bottomBarVisibility(true)

         NoticeToMarinersHomeScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { type ->
               when (type) {
                  NoticeToMarinersHomeChoice.ALL -> {
                     navController.navigate(NoticeToMarinersRoute.All.name)
                  }
                  NoticeToMarinersHomeChoice.CHART_CORRECTIONS -> {}
               }
            }
         )
      }

      composable(
         route = NoticeToMarinersRoute.All.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${NoticeToMarinersRoute.All.name}" })
      ) {
         bottomBarVisibility(false)

         NoticeToMarinersAllScreen(
            close = { navController.popBackStack() }
         )
      }
   }
}