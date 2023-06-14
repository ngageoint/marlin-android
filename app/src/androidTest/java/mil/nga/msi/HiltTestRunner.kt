package mil.nga.msi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        Log.i("Billy", "HiltTestRunner newApplication")
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}