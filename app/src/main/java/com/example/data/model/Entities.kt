package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "admins",
    indices = [Index(value = ["username"], unique = true)]
)
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val name: String,
    val photoUri: String? = null,
    val isSuperAdmin: Boolean = false,
    val permissions: String = "CREATE_MEMO,EDIT_MEMO,DELETE_MEMO,PAY_DUE,DELIVER_ORDER,MANAGE_PRODUCTS,MANAGE_ADMINS,APP_SETTINGS"
) {
    fun hasPermission(permission: String): Boolean {
        if (isSuperAdmin) return true
        return permissions.split(",").map { it.trim() }.contains(permission)
    }
}

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String = "রেনু পোনা",
    val unit: String = "কেজি",
    val defaultPrice: Double = 0.0
)

@Entity(
    tableName = "customers",
    indices = [Index(value = ["phone"], unique = true)]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phone: String,
    val name: String,
    val address: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memos")
data class MemoEntity(
    @PrimaryKey
    val memoNumber: Int, // Starts from 1001
    val customerPhone: String,
    val customerName: String,
    val customerAddress: String,
    val date: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val paidAmount: Double,
    val isPaid: Boolean = false,
    val isDelivered: Boolean = false,
    val adminId: Int = 0,
    val adminName: String = "",
    val notes: String = ""
) {
    val dueAmount: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)
}

@Entity(
    tableName = "memo_items",
    indices = [Index(value = ["memoNumber"])]
)
data class MemoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val memoNumber: Int,
    val productName: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val totalPrice: Double
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
