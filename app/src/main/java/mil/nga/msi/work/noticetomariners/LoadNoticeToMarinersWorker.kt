package mil.nga.msi.work.noticetomariners

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.network.noticetomariners.NoticeToMarinersTypeAdapter
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadNoticeToMarinersWorker @AssistedInject constructor(
   @Assisted private val context: Context,
   @Assisted params: WorkerParameters,
   private val dataSource: NoticeToMarinersLocalDataSource
) : CoroutineWorker(context, params) {
   override suspend fun doWork(): Result = try {
      if (dataSource.isEmpty()) {
         context.assets.open("ntm.json").use { input ->
            val reader = JsonReader(InputStreamReader(input))
            val noticeToMariners = NoticeToMarinersTypeAdapter().read(reader)
            dataSource.insert(noticeToMariners)
         }
      }

      Result.success()
   } catch (error: Throwable) {
      Result.failure()
   }
}