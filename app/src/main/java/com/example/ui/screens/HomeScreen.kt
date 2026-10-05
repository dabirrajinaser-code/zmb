package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MemoEntity
import com.example.ui.components.DeliveryConfirmDialog
import com.example.ui.components.HomeCarousel
import com.example.ui.components.PayDueDialog
import com.example.ui.components.PendingDeliveryDialog
import com.example.ui.components.ShopHeaderCard
import com.example.ui.components.TotalMemosDialog
import com.example.ui.components.TotalSalesDialog
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua800
import com.example.viewmodel.ZihanViewModel

@Composable
fun HomeScreen(
    viewModel: ZihanViewModel,
    onMemoClick: (MemoEntity) -> Unit
) {
    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val homeBanners by viewModel.homeBanners.collectAsState()
    val allMemos by viewModel.allMemos.collectAsState()
    val dueMemos by viewModel.dueMemos.collectAsState()
    val undeliveredMemos by viewModel.undeliveredMemos.collectAsState()
    val allMemoItems by viewModel.allMemoItems.collectAsState()

    // Dialog States
    var showTotalMemosDialog by remember { mutableStateOf(false) }
    var showTotalSalesDialog by remember { mutableStateOf(false) }
    var showPendingDeliveryDialog by remember { mutableStateOf(false) }
    var memoForPayment by remember { mutableStateOf<MemoEntity?>(null) }
    var memoForDelivery by remember { mutableStateOf<MemoEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 1. Top 4-5 Carousel Banners
        item {
            HomeCarousel(
                banners = homeBanners,
                modifier = Modifier.testTag("home_carousel")
            )
        }

        // 2. Shop Header: Name, Address, Director, Fish Breeds, Admin Name & Photo, Quick Call
        item {
            ShopHeaderCard(
                currentAdmin = currentAdmin,
                modifier = Modifier.testTag("shop_header_card")
            )
        }

        // 3. Three Stat Cards (মোট বিক্রয়, মোট মেমো, বাকি ডেলিভারি)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left: মোট বিক্রয়
                StatLiquidCard(
                    modifier = Modifier.weight(1f),
                    title = "মোট বিক্রয়",
                    value = "৳ ${String.format("%.0f", allMemos.sumOf { it.totalAmount })}",
                    icon = Icons.Default.TrendingUp,
                    iconColor = EmeraldGreen,
                    testTag = "total_sales_card",
                    onClick = { showTotalSalesDialog = true }
                )

                // Center: মোট মেমো
                StatLiquidCard(
                    modifier = Modifier.weight(1f),
                    title = "মোট মেমো",
                    value = "${allMemos.size} টি",
                    icon = Icons.Default.Receipt,
                    iconColor = WaterAqua800,
                    testTag = "total_memos_card",
                    onClick = { showTotalMemosDialog = true }
                )

                // Right: বাকি ডেলিভারি
                StatLiquidCard(
                    modifier = Modifier.weight(1f),
                    title = "বাকি ডেলিভারি",
                    value = "${undeliveredMemos.size} টি",
                    icon = Icons.Default.LocalShipping,
                    iconColor = CoralRed,
                    testTag = "pending_delivery_card",
                    onClick = { showPendingDeliveryDialog = true }
                )
            }
        }

        // 4. টাকা পরিশোধ (বকেয়া তালিকা)
        item {
            LiquidGlossyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = "Due Payments",
                                tint = CoralRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "টাকা পরিশোধ (বকেয়া তালিকা)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaterAqua800
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CoralRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${dueMemos.size} জন",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = CoralRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                    if (dueMemos.isEmpty()) {
                        Text(
                            text = "আলহামদুলিল্লাহ, কোনো বকেয়া টাকা পাওনা নেই!",
                            color = EmeraldGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        dueMemos.take(6).forEach { memo ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WaterAqua100.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = memo.customerName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "মেমো #${memo.memoNumber} | মোবাইল: ${memo.customerPhone}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "বকেয়া: ৳ ${String.format("%.0f", memo.dueAmount)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CoralRed
                                        )
                                    }
                                    Button(
                                        onClick = { memoForPayment = memo },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("pay_button_${memo.memoNumber}")
                                    ) {
                                        Text("পরিশোধ", color = PureWhite, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. প্রোডাক্ট সমূহ ও অর্ডার ডেলিভারি অপশন
        item {
            LiquidGlossyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = "Products & Delivery",
                                tint = OceanBlue500,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "প্রোডাক্ট সমূহ ও অর্ডার ডেলিভারি অপশন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaterAqua800
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                    if (allMemos.isEmpty()) {
                        Text(
                            text = "এখনো কোনো অর্ডার বা মেমো নেই।",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        // Display ordered products with customer name and quantity
                        allMemos.take(10).forEach { memo ->
                            val itemsForMemo = allMemoItems.filter { it.memoNumber == memo.memoNumber }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WaterAqua100.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "গ্রাহক: ${memo.customerName}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "মেমো #${memo.memoNumber} | তারিখ: ${memo.date}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        if (itemsForMemo.isNotEmpty()) {
                                            itemsForMemo.forEach { item ->
                                                Text(
                                                    text = "• ${item.productName}: ${item.quantity} ${item.unit}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = WaterAqua800
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "মাছের রেনু পোনা ও সামগ্রী",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (!memo.isDelivered) {
                                        Button(
                                            onClick = { memoForDelivery = memo },
                                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("received_button_${memo.memoNumber}")
                                        ) {
                                            Text("পেয়েছে", color = PureWhite, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldGreen.copy(alpha = 0.15f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Delivered",
                                                    tint = EmeraldGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "ডেলিভারি সম্পন্ন",
                                                    color = EmeraldGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialogs
    if (showTotalMemosDialog) {
        TotalMemosDialog(
            memos = allMemos,
            onDismiss = { showTotalMemosDialog = false },
            onMemoClick = { memo ->
                showTotalMemosDialog = false
                onMemoClick(memo)
            }
        )
    }

    if (showTotalSalesDialog) {
        TotalSalesDialog(
            items = allMemoItems,
            onDismiss = { showTotalSalesDialog = false }
        )
    }

    if (showPendingDeliveryDialog) {
        PendingDeliveryDialog(
            undeliveredMemos = undeliveredMemos,
            onDismiss = { showPendingDeliveryDialog = false },
            onDeliverClick = { memo ->
                memoForDelivery = memo
            }
        )
    }

    memoForPayment?.let { memo ->
        PayDueDialog(
            memo = memo,
            onConfirm = {
                viewModel.markMemoPaid(memo.memoNumber)
                memoForPayment = null
            },
            onDismiss = { memoForPayment = null }
        )
    }

    memoForDelivery?.let { memo ->
        DeliveryConfirmDialog(
            memo = memo,
            onConfirm = {
                viewModel.markMemoDelivered(memo.memoNumber)
                memoForDelivery = null
            },
            onDismiss = { memoForDelivery = null }
        )
    }
}

@Composable
fun StatLiquidCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    LiquidGlossyCard(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = WaterAqua800
            )
        }
    }
}
