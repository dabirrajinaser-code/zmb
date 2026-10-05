package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.MemoEntity
import com.example.ui.components.MemoDetailDialog
import com.example.ui.screens.AdminControlScreen
import com.example.ui.screens.AdminsScreen
import com.example.ui.screens.CreateMemoScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemosScreen
import com.example.ui.screens.ProductControlScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CrystalWaterLight
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua200
import com.example.ui.theme.WaterAqua600
import com.example.ui.theme.WaterAqua800
import com.example.ui.theme.ZihanFishTheme
import com.example.viewmodel.ZihanViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ZihanViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            val fontScale by viewModel.fontScale.collectAsState()
            val currentAdmin by viewModel.currentAdmin.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()

            var showMenu by remember { mutableStateOf(false) }
            var selectedMemoForDetail by remember { mutableStateOf<MemoEntity?>(null) }

            ZihanFishTheme(darkTheme = isDark, fontMultiplier = fontScale) {
                if (currentAdmin == null) {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            viewModel.navigateTo("home")
                        }
                    )
                } else {
                    // Back button handling
                    BackHandler(enabled = currentScreen != "home") {
                        if (!viewModel.navigateBack()) {
                            viewModel.navigateTo("home")
                        }
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets.navigationBars,
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = "Zihan Fish Logo",
                                            tint = WaterAqua800,
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "জিহান মৎস্য বীজাগার",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = WaterAqua800
                                        )
                                    }
                                },
                                actions = {
                                    // Three-Dot Menu
                                    Box {
                                        IconButton(
                                            onClick = { showMenu = true },
                                            modifier = Modifier.testTag("three_dot_menu_button")
                                        ) {
                                            Icon(
                                                Icons.Default.MoreVert,
                                                contentDescription = "Menu",
                                                tint = WaterAqua800
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            // 1. এডমিন কন্ট্রোল
                                            DropdownMenuItem(
                                                text = { Text("এডমিন কন্ট্রোল", fontWeight = FontWeight.SemiBold) },
                                                leadingIcon = {
                                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Control", tint = WaterAqua800)
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    viewModel.navigateTo("admin_control")
                                                },
                                                modifier = Modifier.testTag("menu_admin_control")
                                            )

                                            // 2. প্রোডাক্ট কন্ট্রোল
                                            DropdownMenuItem(
                                                text = { Text("প্রোডাক্ট কন্ট্রোল", fontWeight = FontWeight.SemiBold) },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Inventory2, contentDescription = "Product Control", tint = WaterAqua800)
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    viewModel.navigateTo("product_control")
                                                },
                                                modifier = Modifier.testTag("menu_product_control")
                                            )

                                            // 3. অ্যাপস সেটিং
                                            DropdownMenuItem(
                                                text = { Text("অ্যাপস সেটিং", fontWeight = FontWeight.SemiBold) },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = WaterAqua800)
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    viewModel.navigateTo("settings")
                                                },
                                                modifier = Modifier.testTag("menu_settings")
                                            )

                                            HorizontalDivider()

                                            // 4. এডমিন লগ আউট
                                            DropdownMenuItem(
                                                text = { Text("এডমিন লগ আউট", color = Color.Red, fontWeight = FontWeight.Bold) },
                                                leadingIcon = {
                                                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    viewModel.logout()
                                                },
                                                modifier = Modifier.testTag("menu_logout")
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            // Water Liquid Glossy Bottom Navigation Bar
                            // 1. হোম, 2. মেমো সমূহ, 3. মেমো তৈরি (মাঝখানে), 4. গ্রাহক সমূহ তথ্য, 5. এডমিনের তথ্য
                            LiquidBottomBar(
                                currentScreen = currentScreen,
                                onNavigate = { screen ->
                                    viewModel.navigateTo(screen)
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "ScreenTransition"
                            ) { screen ->
                                when (screen) {
                                    "home" -> HomeScreen(
                                        viewModel = viewModel,
                                        onMemoClick = { memo -> selectedMemoForDetail = memo }
                                    )
                                    "memos" -> MemosScreen(
                                        viewModel = viewModel,
                                        onMemoClick = { memo -> selectedMemoForDetail = memo }
                                    )
                                    "create_memo" -> CreateMemoScreen(
                                        viewModel = viewModel,
                                        onMemoCreated = { memo ->
                                            viewModel.navigateTo("memos")
                                        }
                                    )
                                    "customers" -> CustomersScreen(
                                        viewModel = viewModel,
                                        onMemoClick = { memo -> selectedMemoForDetail = memo }
                                    )
                                    "admins" -> AdminsScreen(
                                        viewModel = viewModel
                                    )
                                    "admin_control" -> AdminControlScreen(
                                        viewModel = viewModel
                                    )
                                    "product_control" -> ProductControlScreen(
                                        viewModel = viewModel
                                    )
                                    "settings" -> SettingsScreen(
                                        viewModel = viewModel
                                    )
                                    else -> HomeScreen(
                                        viewModel = viewModel,
                                        onMemoClick = { memo -> selectedMemoForDetail = memo }
                                    )
                                }
                            }
                        }
                    }

                    // Full Memo Detail Slip Dialog
                    selectedMemoForDetail?.let { memo ->
                        MemoDetailDialog(
                            memo = memo,
                            viewModel = viewModel,
                            onDismiss = { selectedMemoForDetail = null }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Liquid Glossy Bottom Bar:
 * 1. হোম
 * 2. মেমো সমূহ
 * 3. মেমো তৈরি (মাঝখানে - Highlighted Crystal Aquatic Bubble)
 * 4. গ্রাহক সমূহ তথ্য
 * 5. এডমিনের তথ্য
 */
@Composable
fun LiquidBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, WaterAqua100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. হোম
            BottomBarItem(
                title = "হোম",
                icon = Icons.Default.Home,
                isSelected = currentScreen == "home",
                testTag = "nav_home",
                onClick = { onNavigate("home") }
            )

            // 2. মেমো সমূহ
            BottomBarItem(
                title = "মেমো সমূহ",
                icon = Icons.Default.ReceiptLong,
                isSelected = currentScreen == "memos",
                testTag = "nav_memos",
                onClick = { onNavigate("memos") }
            )

            // 3. মেমো তৈরি (মাঝখানে)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(WaterAqua600, WaterAqua800)
                        )
                    )
                    .border(2.dp, PureWhite, CircleShape)
                    .clickable { onNavigate("create_memo") }
                    .testTag("nav_create_memo"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Memo",
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "তৈরি",
                        color = PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. গ্রাহক সমূহ তথ্য
            BottomBarItem(
                title = "গ্রাহক তথ্য",
                icon = Icons.Default.Group,
                isSelected = currentScreen == "customers",
                testTag = "nav_customers",
                onClick = { onNavigate("customers") }
            )

            // 5. এডমিনের তথ্য
            BottomBarItem(
                title = "এডমিন তথ্য",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = currentScreen == "admins",
                testTag = "nav_admins",
                onClick = { onNavigate("admins") }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) WaterAqua800 else Color.Gray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) WaterAqua800 else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
