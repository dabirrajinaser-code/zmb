package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.AdminEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ZihanRepository(private val database: AppDatabase) {
    private val adminDao = database.adminDao()
    private val productDao = database.productDao()
    private val customerDao = database.customerDao()
    private val memoDao = database.memoDao()
    private val settingDao = database.appSettingDao()

    // Admins
    val allAdmins: Flow<List<AdminEntity>> = adminDao.getAllAdmins()

    suspend fun login(username: String, pass: String): AdminEntity? {
        val admin = adminDao.getAdminByUsername(username.trim())
        return if (admin != null && admin.password == pass.trim()) {
            admin
        } else {
            null
        }
    }

    suspend fun insertAdmin(admin: AdminEntity): Long = adminDao.insertAdmin(admin)
    suspend fun updateAdmin(admin: AdminEntity) = adminDao.updateAdmin(admin)
    suspend fun deleteAdmin(admin: AdminEntity) = adminDao.deleteAdmin(admin)

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    // Customers
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    suspend fun getCustomerByPhone(phone: String): CustomerEntity? {
        val cleanPhone = phone.trim()
        if (cleanPhone.isEmpty()) return null
        return customerDao.getCustomerByPhone(cleanPhone)
    }

    suspend fun saveCustomer(customer: CustomerEntity) {
        val existing = customerDao.getCustomerByPhone(customer.phone)
        if (existing == null) {
            customerDao.insertCustomer(customer)
        } else {
            customerDao.updateCustomer(existing.copy(name = customer.name, address = customer.address))
        }
    }

    // Memos
    val allMemos: Flow<List<MemoEntity>> = memoDao.getAllMemos()
    val dueMemos: Flow<List<MemoEntity>> = memoDao.getDueMemos()
    val undeliveredMemos: Flow<List<MemoEntity>> = memoDao.getUndeliveredMemos()
    val allMemoItems: Flow<List<MemoItemEntity>> = memoDao.getAllMemoItems()

    fun getMemosByCustomerPhone(phone: String): Flow<List<MemoEntity>> = memoDao.getMemosByPhone(phone)
    fun getMemosByAdminId(adminId: Int): Flow<List<MemoEntity>> = memoDao.getMemosByAdminId(adminId)
    fun getMemoItems(memoNumber: Int): Flow<List<MemoItemEntity>> = memoDao.getMemoItems(memoNumber)
    suspend fun getMemoItemsSync(memoNumber: Int): List<MemoItemEntity> = memoDao.getMemoItemsSync(memoNumber)

    suspend fun createMemo(memo: MemoEntity, items: List<MemoItemEntity>): Int {
        val currentMax = memoDao.getMaxMemoNumber() ?: 1000
        val newMemoNumber = if (memo.memoNumber >= 1001) memo.memoNumber else (currentMax + 1).coerceAtLeast(1001)

        val finalMemo = memo.copy(memoNumber = newMemoNumber)
        memoDao.insertMemo(finalMemo)

        val finalItems = items.map { it.copy(memoNumber = newMemoNumber) }
        memoDao.insertMemoItems(finalItems)

        // Save or update customer profile
        if (finalMemo.customerPhone.isNotBlank() && finalMemo.customerName.isNotBlank()) {
            saveCustomer(
                CustomerEntity(
                    phone = finalMemo.customerPhone.trim(),
                    name = finalMemo.customerName.trim(),
                    address = finalMemo.customerAddress.trim()
                )
            )
        }

        return newMemoNumber
    }

    suspend fun updateMemo(memo: MemoEntity, items: List<MemoItemEntity>) {
        memoDao.updateMemo(memo)
        memoDao.deleteMemoItemsByNumber(memo.memoNumber)
        memoDao.insertMemoItems(items.map { it.copy(memoNumber = memo.memoNumber) })
    }

    suspend fun markMemoPaid(memoNumber: Int) {
        val memo = memoDao.getMemoByNumber(memoNumber) ?: return
        val updated = memo.copy(
            isPaid = true,
            paidAmount = memo.totalAmount
        )
        memoDao.updateMemo(updated)
    }

    suspend fun markMemoDelivered(memoNumber: Int) {
        val memo = memoDao.getMemoByNumber(memoNumber) ?: return
        val updated = memo.copy(isDelivered = true)
        memoDao.updateMemo(updated)
    }

    suspend fun deleteMemo(memoNumber: Int) {
        memoDao.deleteMemoByNumber(memoNumber)
        memoDao.deleteMemoItemsByNumber(memoNumber)
    }

    // App Settings
    fun getSetting(key: String): Flow<String?> = settingDao.getSetting(key)
    suspend fun getSettingSync(key: String): String? = settingDao.getSettingSync(key)
    suspend fun setSetting(key: String, value: String) {
        settingDao.setSetting(AppSettingEntity(key, value))
    }

    // Initialize initial data check on launch
    suspend fun checkInitialData() {
        AppDatabase.populateInitialData(database)
    }
}
