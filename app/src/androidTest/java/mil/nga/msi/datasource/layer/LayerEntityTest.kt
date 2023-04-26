package mil.nga.msi.datasource.layer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertEPubsEqual
import assertLayersEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LayerEntityTest {

    private lateinit var dao: LayerDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.layerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRead() = runTest {
        val layer = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        )
        dao.insert(layer)
        val layers = dao.observeLayers().first()

        assertEquals(layers.size, 1)
        assertLayersEqual(layers.first(), layer)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndRead() = runTest {
        val layer = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        )
        layer.visible = false

        dao.insert(layer)
        val flow = dao.observeLayers()
        var layers = flow.first()

        val newLayer = layers.first()
        assertEquals(layers.size, 1)
        assertLayersEqual(layers.first(), layer)

        newLayer.visible = true
        dao.update(newLayer)
        layers = flow.first()

        assertLayersEqual(layers.first(), newLayer)
    }
}
