package mil.nga.msi.work.electronicpublication

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.network.electronicpublication.ElectronicPublicationTypeAdapter
import mil.nga.msi.repository.electronicpublication.ElectronicPublicationLocalDataSource
import java.io.InputStreamReader

@HiltWorker
class LoadElectronicPublicationsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val dataSource: ElectronicPublicationLocalDataSource
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        if (dataSource.isEmpty()) {
            context.assets.open("epubs.json").use { input ->
                val reader = JsonReader(InputStreamReader(input))
                val gson = GsonBuilder().registerTypeAdapter(object: TypeToken<ElectronicPublication>() {}.type, ElectronicPublicationTypeAdapter()).create()
                val ePubs = gson.fromJson<List<ElectronicPublication>>(reader, object: TypeToken<ArrayList<ElectronicPublication>>() {}.type)
                dataSource.insert(ePubs)
            }
        }
        Result.success()
    } catch (error: Throwable) {
        Log.e(this.javaClass.simpleName, "seeding failed", error)
        Result.failure()
    }

    companion object {
        const val SEED_ELECTRONIC_PUBLICATIONS_WORK: String = "SeedElectronicPublications"
    }
}