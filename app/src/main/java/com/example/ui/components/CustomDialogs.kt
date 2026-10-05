package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AdminEntity
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua600
import com.example.ui.theme.WaterAqua800

/**
 * টাকা পরিশোধ (বকেয়া পরিশোধ) পপ-আপ:
 * মেমো নাম্বার, নাম, কত টাকার পণ্য ক্রয় করেছে, টিক অপশন (টিকে চাপ না দিলে কনফার্ম হবে না)।
 */
@Composable
fun PayDueDialog(
    memo: MemoEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = "Pay Due",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "টাকা পরিশোধ নিশ্চিতকরণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterAqua800
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WaterAqua100)

                // Info Rows
                InfoRow(label = "মেমো নাম্বার:", value = "#${memo.memoNumber}")
                InfoRow(label = "গ্রাহকের নাম:", value = memo.customerName)
                InfoRow(label = "মোবাইল নাম্বার:", value = memo.customerPhone)
                InfoRow(label = "ক্রয়কৃত মোট মূল্য:", value = "৳ ${String.format("%.0f", memo.totalAmount)}")
                InfoRow(label = "বর্তমান বকেয়া:", value = "৳ ${String.format("%.0f", memo.dueAmount)}", isHighlight = true)

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox confirmation
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WaterAqua100.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAgreed = !isAgreed }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAgreed,
                            onCheckedChange = { isAgreed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldGreen,
                                checkmarkColor = PureWhite
                            ),
                            modifier = Modifier.testTag("pay_due_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "গ্রাহকের সম্পূর্ণ টাকা পরিশোধ হয়েছে তা নিশ্চিত করছি",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বাতিল", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isAgreed) {
                                onConfirm()
                            }
                        },
                        enabled = isAgreed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_pay_button")
                    ) {
                        Text("কনফার্ম করুন", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * ডেলিভারি অপশন পপ-আপ:
 * মেমো নাম্বার, গ্রাহকের নাম, টিক অপশন (টিকে চাপ দিয়ে কনফার্ম)।
 */
@Composable
fun DeliveryConfirmDialog(
    memo: MemoEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Delivery",
                            tint = OceanBlue500,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পণ্য ডেলিভারি কনফার্ম",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterAqua800
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WaterAqua100)

                InfoRow(label = "মেমো নাম্বার:", value = "#${memo.memoNumber}")
                InfoRow(label = "গ্রাহকের নাম:", value = memo.customerName)
                InfoRow(label = "ঠিকানা:", value = memo.customerAddress)
                InfoRow(label = "ফোন নাম্বার:", value = memo.customerPhone)
                InfoRow(label = "তারিখ:", value = memo.date)

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WaterAqua100.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAgreed = !isAgreed }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAgreed,
                            onCheckedChange = { isAgreed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OceanBlue500,
                                checkmarkColor = PureWhite
                            ),
                            modifier = Modifier.testTag("delivery_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "গ্রাহক সকল পণ্য সঠিকভাবে গ্রহণ করেছেন তা কনফার্ম করছি",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বাতিল", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isAgreed) {
                                onConfirm()
                            }
                        },
                        enabled = isAgreed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OceanBlue500,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_delivery_button")
                    ) {
                        Text("কনফার্ম", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * মোট মেমো তালিকা পপ-আপ:
 * কয়টি মেমো তৈরি হয়েছে এবং কোন কোন গ্রাহকের মেমো তৈরি হয়েছে।
 */
@Composable
fun TotalMemosDialog(
    memos: List<MemoEntity>,
    onDismiss: () -> Unit,
    onMemoClick: (MemoEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp)
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
                    Column {
                        Text(
                            text = "মোট মেমো তালিকা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterAqua800
                        )
                        Text(
                            text = "সর্বমোট তৈরি মেমো: ${memos.size} টি",
                            style = MaterialTheme.typography.bodySmall,
                            color = OceanBlue500,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                if (memos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("এখনো কোনো মেমো তৈরি হয়নি", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(memos) { memo ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WaterAqua100.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onMemoClick(memo) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "মেমো #${memo.memoNumber}",
                                            fontWeight = FontWeight.Bold,
                                            color = WaterAqua800,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "গ্রাহক: ${memo.customerName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "তারিখ: ${memo.date}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "৳ ${String.format("%.0f", memo.totalAmount)}",
                                            fontWeight = FontWeight.Bold,
                                            color = WaterAqua800,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = if (memo.dueAmount > 0) "বকেয়া ৳${String.format("%.0f", memo.dueAmount)}" else "পরিশোধিত",
                                            color = if (memo.dueAmount > 0) CoralRed else EmeraldGreen,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
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

/**
 * মোট বিক্রয় পপ-আপ:
 * কোন প্রোডাক্ট কত পরিমাণ বিক্রি হয়েছে ও মোট টাকার হিসাব।
 */
@Composable
fun TotalSalesDialog(
    items: List<MemoItemEntity>,
    onDismiss: () -> Unit
) {
    // Group by product name
    val grouped = items.groupBy { it.productName }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp)
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
                    Column {
                        Text(
                            text = "মোট বিক্রয় বিবরণী",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterAqua800
                        )
                        val totalSalesVal = items.sumOf { it.totalPrice }
                        Text(
                            text = "সর্বমোট বিক্রয়: ৳ ${String.format("%.0f", totalSalesVal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                if (grouped.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("এখনো কোনো পণ্য বিক্রি হয়নি", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(grouped.entries.toList()) { entry ->
                            val productName = entry.key
                            val productItems = entry.value
                            val totalQty = productItems.sumOf { it.quantity }
                            val unit = productItems.firstOrNull()?.unit ?: "কেজি"
                            val totalAmount = productItems.sumOf { it.totalPrice }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WaterAqua100.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = productName,
                                            fontWeight = FontWeight.Bold,
                                            color = WaterAqua800,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "মোট বিক্রিত পরিমাণ: $totalQty $unit",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "৳ ${String.format("%.0f", totalAmount)}",
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        style = MaterialTheme.typography.bodyMedium
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

/**
 * বাকি ডেলিভারি পপ-আপ:
 * কোন কোন গ্রাহকের ডেলিভারি বাকি আছে তাদের নাম ও তথ্য।
 */
@Composable
fun PendingDeliveryDialog(
    undeliveredMemos: List<MemoEntity>,
    onDismiss: () -> Unit,
    onDeliverClick: (MemoEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp)
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
                    Column {
                        Text(
                            text = "বাকি ডেলিভারি তালিকা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WaterAqua800
                        )
                        Text(
                            text = "ডেলিভারি বাকি আছে: ${undeliveredMemos.size} টি",
                            style = MaterialTheme.typography.bodySmall,
                            color = CoralRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                if (undeliveredMemos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("সব গ্রাহকের ডেলিভারি সম্পন্ন হয়েছে!", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(undeliveredMemos) { memo ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WaterAqua100.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
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
                                            text = memo.customerName,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "মেমো #${memo.memoNumber} | মোবাইল: ${memo.customerPhone}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = "ঠিকানা: ${memo.customerAddress}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onDeliverClick(memo) },
                                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("ডেলিভারি", style = MaterialTheme.typography.labelSmall)
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

/**
 * এডমিনের বিস্তারিত পপ-আপ:
 * এডমিন কতটি মেমো তৈরি করেছে, কত টাকার মেমো তৈরি করেছে, কত টাকা পরিশোধ করেছে।
 */
@Composable
fun AdminDetailDialog(
    admin: AdminEntity,
    memos: List<MemoEntity>,
    onDismiss: () -> Unit
) {
    val adminMemos = memos.filter { it.adminId == admin.id }
    val totalMemoCount = adminMemos.size
    val totalMemoValue = adminMemos.sumOf { it.totalAmount }
    val totalCollectedPaid = adminMemos.sumOf { it.paidAmount }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "এডমিনের কাজের তথ্য",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WaterAqua100)

                // Admin Avatar & Name
                if (!admin.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = admin.photoUri,
                        contentDescription = "Admin Photo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Admin Avatar",
                        tint = WaterAqua800,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = admin.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ইউজার আইডি: ${admin.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(label = "তৈরিকৃত মোট মেমো:", value = "$totalMemoCount টি")
                InfoRow(label = "তৈরিকৃত মেমোর মোট মূল্য:", value = "৳ ${String.format("%.0f", totalMemoValue)}")
                InfoRow(label = "সংগ্রহকৃত পরিশোধিত টাকা:", value = "৳ ${String.format("%.0f", totalCollectedPaid)}", isHighlight = true)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ঠিক আছে", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) EmeraldGreen else WaterAqua800
        )
    }
}
