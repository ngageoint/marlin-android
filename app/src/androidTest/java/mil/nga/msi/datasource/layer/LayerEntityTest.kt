package mil.nga.msi.datasource.layer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertLayersEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun readByReference() = runTest {
        val insert = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        )
        val id = dao.insert(insert)
        val read = dao.getLayer(id)

        Assert.assertNotNull(read)
        assertLayersEqual(insert, read!!)
    }

    @Test
    @Throws(Exception::class)
    fun update() = runTest {
        val layer = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        ).apply { visible = false }

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

    @Test
    @Throws(Exception::class)
    fun enable() = runTest {
        val layer = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        ).apply { visible = false }

        val id = dao.insert(layer)
        dao.enable(id, true)
        val read = dao.getLayer(id)

        assertEquals(true, read!!.visible)
    }

    @Test
    @Throws(Exception::class)
    fun delete() = runTest {
        val layer = Layer(
            type = LayerType.XYZ,
            url = "",
            name = "Test Layer"
        ).apply { visible = false }

        val id = dao.insert(layer)
        val read = dao.getLayer(id)
        dao.delete(read!!)
        val deleted = dao.getLayer(id)

        assertNull(deleted)
    }

    @Test
    fun count() = runTest {
        dao.insert(Layer(
            type = LayerType.XYZ,
            url = "1",
            name = "Test Layer 1"
        ))
        dao.insert(Layer(
            type = LayerType.XYZ,
            url = "2",
            name = "Test Layer 2"
        ))
        val count = dao.count()

        assertEquals(2, count)
    }
}
