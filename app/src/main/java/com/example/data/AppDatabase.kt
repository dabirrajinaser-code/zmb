package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdminDao
import com.example.data.dao.AppSettingDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.MemoDao
import com.example.data.dao.ProductDao
import com.example.data.model.AdminEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AdminEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
        MemoEntity::class,
        MemoItemEntity::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminDao(): AdminDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun memoDao(): MemoDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zihan_fish_hatchery.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val adminDao = database.adminDao()
            val productDao = database.productDao()
            val appSettingDao = database.appSettingDao()

            // 1. Default Super Admin
            if (adminDao.getAdminCount() == 0) {
                adminDao.insertAdmin(
                    AdminEntity(
                        id = 1,
                        username = "admin",
                        password = "admin",
                        name = "মোঃ ময়নুল সাদিক (জোহা)",
                        photoUri = null,
                        isSuperAdmin = true,
                        permissions = "CREATE_MEMO,EDIT_MEMO,DELETE_MEMO,PAY_DUE,DELIVER_ORDER,MANAGE_PRODUCTS,MANAGE_ADMINS,APP_SETTINGS"
                    )
                )
            }

            // 2. Default Products (as per user brief)
            if (productDao.getProductCount() == 0) {
                val defaultProducts = listOf(
                    ProductEntity(name = "রুই মাছের রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 450.0),
                    ProductEntity(name = "কাতলা মাছের রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 500.0),
                    ProductEntity(name = "মৃগেল মাছের রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 420.0),
                    ProductEntity(name = "বাটা মাছের রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 380.0),
                    ProductEntity(name = "সিলভার কাপ পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 350.0),
                    ProductEntity(name = "গ্লাসকাপ মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 400.0),
                    ProductEntity(name = "স্বরপুটি মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 360.0),
                    ProductEntity(name = "পাঙ্গাস মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 280.0),
                    ProductEntity(name = "হাংরি মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 320.0),
                    ProductEntity(name = "দেশী মাগুর রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 850.0),
                    ProductEntity(name = "কৈ মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 400.0),
                    ProductEntity(name = "টেংরা মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 900.0),
                    ProductEntity(name = "গুলশা মাছের পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 950.0),
                    ProductEntity(name = "পাবদা মাছের রেনু পোনা", category = "রেনু পোনা", unit = "কেজি", defaultPrice = 1200.0),
                    ProductEntity(name = "মাছের খাদ্য (গ্রোয়ার ফিড)", category = "মাছের খাদ্য", unit = "বস্তা", defaultPrice = 2500.0),
                    ProductEntity(name = "মাছের ওষুধ ও ভিটামিন (অ্যাকোয়া কেয়ার)", category = "ওষুধ", unit = "বোতল", defaultPrice = 450.0)
                )
                productDao.insertProducts(defaultProducts)
            }

            // 3. Default Banner settings
            appSettingDao.setSetting(
                AppSettingEntity(
                    key = "home_banners",
                    value = "banner_pond,banner_fry,banner_tank,banner_carp"
                )
            )
        }
    }
}
