package mil.nga.msi.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic

val NavType.Companion.NavTypeNoticeToMarinersGraphic: NavType<NoticeToMarinersGraphic?>
   get() = graphic

   private val graphic = object : NavType<NoticeToMarinersGraphic?>(true) {
      override fun put(bundle: Bundle, key: String, value: NoticeToMarinersGraphic?) {
         bundle.putParcelable(key, value)
      }

      override fun get(bundle: Bundle, key: String): NoticeToMarinersGraphic? {
         return bundle.getParcelable(key)
      }

      override fun parseValue(value: String): NoticeToMarinersGraphic? {
         return Json.decodeFromString(value)
      }
   }