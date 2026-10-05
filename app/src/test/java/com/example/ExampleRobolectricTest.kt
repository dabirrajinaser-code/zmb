package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.model.AdminEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.repository.ZihanRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ZihanRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ZihanRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun verifyAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("জিহান মৎস্য বীজাগার", appName)
    }

    @Test
    fun verifyAdminSeedingAndLogin() = runBlocking {
        repository.checkInitialData()

        val admin = repository.login("admin", "admin")
        assertNotNull(admin)
        assertEquals("admin", admin?.username)
        assertTrue(admin?.isSuperAdmin == true)
    }

    @Test
    fun verifyMemoCreationStartsAt1001AndCustomerAutoSaved() = runBlocking {
        val memo = MemoEntity(
            memoNumber = 0,
            customerPhone = "01722736633",
            customerName = "করিম মিয়া",
            customerAddress = "মোকামতলা",
            date = "05/10/2026",
            time = "10:30 AM",
            totalAmount = 1500.0,
            paidAmount = 1000.0,
            isPaid = false,
            isDelivered = false
        )
        val items = listOf(
            MemoItemEntity(
                memoNumber = 0,
                productName = "রুই মাছের রেনু পোনা",
                quantity = 3.0,
                unit = "কেজি",
                unitPrice = 500.0,
                totalPrice = 1500.0
            )
        )

        val assignedNumber = repository.createMemo(memo, items)
        assertEquals(1001, assignedNumber)

        // Verify customer auto saved
        val customer = repository.getCustomerByPhone("01722736633")
        assertNotNull(customer)
        assertEquals("করিম মিয়া", customer?.name)

        // Verify due memos
        val dueList = repository.dueMemos.first()
        assertEquals(1, dueList.size)
        assertEquals(500.0, dueList.first().dueAmount, 0.01)

        // Pay due
        repository.markMemoPaid(assignedNumber)
        val updatedDueList = repository.dueMemos.first()
        assertTrue(updatedDueList.isEmpty())
    }
}
