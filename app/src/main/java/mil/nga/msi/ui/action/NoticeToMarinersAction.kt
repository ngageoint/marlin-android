package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute

sealed class NoticeToMarinersAction(): Action() {
   class Tap(private val noticeNumber: Int): NoticeToMarinersAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${NoticeToMarinersRoute.Detail.name}?noticeNumber=${noticeNumber}")
      }
   }
}

