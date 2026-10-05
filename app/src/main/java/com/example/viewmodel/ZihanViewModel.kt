package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.AdminEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.model.ProductEntity
import com.example.data.repository.ZihanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZihanViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = ZihanRepository(database)

    // Current Navigation Destination
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Navigation Stack for back handling
    private val screenStack = mutableListOf<String>()

    fun navigateTo(screen: String) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        return false
    }

    // Authenticated Admin
    private val _currentAdmin = MutableStateFlow<AdminEntity?>(null)
    val currentAdmin: StateFlow<AdminEntity?> = _currentAdmin.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // App Preferences
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    // Data streams
    val allAdmins: StateFlow<List<AdminEntity>> = repository.allAdmins.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCustomers: StateFlow<List<CustomerEntity>> = repository.allCustomers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMemos: StateFlow<List<MemoEntity>> = repository.allMemos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dueMemos: StateFlow<List<MemoEntity>> = repository.dueMemos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val undeliveredMemos: StateFlow<List<MemoEntity>> = repository.undeliveredMemos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMemoItems: StateFlow<List<MemoItemEntity>> = repository.allMemoItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Banners for Home Carousel
    private val _homeBanners = MutableStateFlow<List<String>>(
        listOf("banner_pond", "banner_fry", "banner_tank", "banner_carp")
    )
    val homeBanners: StateFlow<List<String>> = _homeBanners.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkInitialData()
            // Load saved settings
            val savedBanners = repository.getSettingSync("home_banners")
            if (!savedBanners.isNullOrBlank()) {
                _homeBanners.value = savedBanners.split(",").filter { it.isNotBlank() }
            }
            val savedDark = repository.getSettingSync("dark_mode")
            if (savedDark != null) {
                _isDarkMode.value = savedDark.toBoolean()
            }
            val savedFont = repository.getSettingSync("font_scale")
            if (savedFont != null) {
                _fontScale.value = savedFont.toFloatOrNull() ?: 1.0f
            }
        }
    }

    // Login Action
    fun login(username: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            val admin = repository.login(username, pass)
            if (admin != null) {
                _currentAdmin.value = admin
                onSuccess()
            } else {
                _loginError.value = "ইউজার আইডি অথবা পাসওয়ার্ড সঠিক নয়!"
            }
        }
    }

    fun logout() {
        _currentAdmin.value = null
        screenStack.clear()
        _currentScreen.value = "home"
    }

    // Settings
    fun toggleDarkMode(enable: Boolean) {
        _isDarkMode.value = enable
        viewModelScope.launch {
            repository.setSetting("dark_mode", enable.toString())
        }
    }

    fun setFontSizeScale(scale: Float) {
        _fontScale.value = scale
        viewModelScope.launch {
            repository.setSetting("font_scale", scale.toString())
        }
    }

    fun updateBanners(banners: List<String>) {
        _homeBanners.value = banners
        viewModelScope.launch {
            repository.setSetting("home_banners", banners.joinToString(","))
        }
    }

    fun addBanner(banner: String) {
        val current = _homeBanners.value.toMutableList()
        if (banner.isNotBlank() && !current.contains(banner)) {
            current.add(banner)
            updateBanners(current)
        }
    }

    fun removeBanner(banner: String) {
        val current = _homeBanners.value.toMutableList()
        current.remove(banner)
        if (current.isEmpty()) {
            current.add("banner_pond")
        }
        updateBanners(current)
    }

    // Memos Actions
    fun createMemo(
        customerPhone: String,
        customerName: String,
        customerAddress: String,
        items: List<MemoItemEntity>,
        paidAmount: Double,
        notes: String,
        onComplete: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val totalAmount = items.sumOf { it.totalPrice }
            val isPaid = paidAmount >= totalAmount
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val now = Date()

            val memo = MemoEntity(
                memoNumber = 0, // will be auto-assigned >= 1001
                customerPhone = customerPhone.trim(),
                customerName = customerName.trim(),
                customerAddress = customerAddress.trim(),
                date = dateFormat.format(now),
                time = timeFormat.format(now),
                timestamp = System.currentTimeMillis(),
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                isPaid = isPaid,
                isDelivered = false,
                adminId = currentAdmin.value?.id ?: 1,
                adminName = currentAdmin.value?.name ?: "এডমিন",
                notes = notes
            )
            val newId = repository.createMemo(memo, items)
            onComplete(newId)
        }
    }

    fun deleteMemo(memoNumber: Int) {
        viewModelScope.launch {
            repository.deleteMemo(memoNumber)
        }
    }

    fun markMemoPaid(memoNumber: Int) {
        viewModelScope.launch {
            repository.markMemoPaid(memoNumber)
        }
    }

    fun markMemoDelivered(memoNumber: Int) {
        viewModelScope.launch {
            repository.markMemoDelivered(memoNumber)
        }
    }

    // Customer Auto-Lookup
    suspend fun lookupCustomerByPhone(phone: String): CustomerEntity? {
        return repository.getCustomerByPhone(phone)
    }

    // Product Control Actions
    fun addProduct(name: String, category: String, unit: String, price: Double) {
        viewModelScope.launch {
            repository.insertProduct(
                ProductEntity(
                    name = name.trim(),
                    category = category.trim(),
                    unit = unit.trim(),
                    defaultPrice = price
                )
            )
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Admin Control Actions
    fun addAdmin(name: String, username: String, pass: String, photoUri: String?, permissions: String) {
        viewModelScope.launch {
            repository.insertAdmin(
                AdminEntity(
                    name = name.trim(),
                    username = username.trim(),
                    password = pass.trim(),
                    photoUri = photoUri,
                    isSuperAdmin = false,
                    permissions = permissions
                )
            )
        }
    }

    fun updateAdmin(admin: AdminEntity) {
        viewModelScope.launch {
            repository.updateAdmin(admin)
            // if updated self
            if (_currentAdmin.value?.id == admin.id) {
                _currentAdmin.value = admin
            }
        }
    }

    fun deleteAdmin(admin: AdminEntity) {
        viewModelScope.launch {
            if (!admin.isSuperAdmin) {
                repository.deleteAdmin(admin)
            }
        }
    }
}
