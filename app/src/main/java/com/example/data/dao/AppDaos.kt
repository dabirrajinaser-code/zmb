package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins ORDER BY id ASC")
    fun getAllAdmins(): Flow<List<AdminEntity>>

    @Query("SELECT * FROM admins WHERE username = :username LIMIT 1")
    suspend fun getAdminByUsername(username: String): AdminEntity?

    @Query("SELECT * FROM admins WHERE id = :id LIMIT 1")
    suspend fun getAdminById(id: Int): AdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity): Long

    @Update
    suspend fun updateAdmin(admin: AdminEntity)

    @Delete
    suspend fun deleteAdmin(admin: AdminEntity)

    @Query("SELECT COUNT(*) FROM admins")
    suspend fun getAdminCount(): Int
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)
}

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY memoNumber DESC")
    fun getAllMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE memoNumber = :memoNumber LIMIT 1")
    suspend fun getMemoByNumber(memoNumber: Int): MemoEntity?

    @Query("SELECT MAX(memoNumber) FROM memos")
    suspend fun getMaxMemoNumber(): Int?

    @Query("SELECT * FROM memos WHERE isPaid = 0 OR (totalAmount - paidAmount) > 0 ORDER BY memoNumber DESC")
    fun getDueMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE isDelivered = 0 ORDER BY memoNumber DESC")
    fun getUndeliveredMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE customerPhone = :phone ORDER BY memoNumber DESC")
    fun getMemosByPhone(phone: String): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE adminId = :adminId ORDER BY memoNumber DESC")
    fun getMemosByAdminId(adminId: Int): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE memoNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' OR customerPhone LIKE '%' || :query || '%' ORDER BY memoNumber DESC")
    fun searchMemos(query: String): Flow<List<MemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: MemoEntity)

    @Update
    suspend fun updateMemo(memo: MemoEntity)

    @Delete
    suspend fun deleteMemo(memo: MemoEntity)

    @Query("DELETE FROM memos WHERE memoNumber = :memoNumber")
    suspend fun deleteMemoByNumber(memoNumber: Int)

    // Memo Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoItems(items: List<MemoItemEntity>)

    @Query("SELECT * FROM memo_items WHERE memoNumber = :memoNumber")
    fun getMemoItems(memoNumber: Int): Flow<List<MemoItemEntity>>

    @Query("SELECT * FROM memo_items WHERE memoNumber = :memoNumber")
    suspend fun getMemoItemsSync(memoNumber: Int): List<MemoItemEntity>

    @Query("SELECT * FROM memo_items")
    fun getAllMemoItems(): Flow<List<MemoItemEntity>>

    @Query("DELETE FROM memo_items WHERE memoNumber = :memoNumber")
    suspend fun deleteMemoItemsByNumber(memoNumber: Int)
}

@Dao
interface AppSettingDao {
    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    fun getSetting(key: String): Flow<String?>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingSync(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingEntity)
}
