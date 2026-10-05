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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua200
import com.example.ui.theme.WaterAqua800
import com.example.util.MemoPrintHelper
import com.example.viewmodel.ZihanViewModel
import kotlinx.coroutines.launch

@Composable
fun MemosScreen(
    viewModel: ZihanViewModel,
    onMemoClick: (MemoEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allMemos by viewModel.allMemos.collectAsState()
    val allMemoItems by viewModel.allMemoItems.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var memoToDelete by remember { mutableStateOf<MemoEntity?>(null) }
    var memoToPreview by remember { mutableStateOf<MemoEntity?>(null) }

    val filteredMemos = allMemos.filter { memo ->
        if (searchQuery.isBlank()) true
        else {
            val q = searchQuery.trim().lowercase()
            memo.memoNumber.toString().contains(q) ||
                    memo.customerName.lowercase().contains(q) ||
                    memo.customerPhone.contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Search Bar by Memo Serial Number
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("মেমোর ক্রমিক নাম্বার বা গ্রাহক দিয়ে খুঁজুন...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = WaterAqua800)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WaterAqua800,
                unfocusedBorderColor = WaterAqua200,
                focusedContainerColor = PureWhite,
                unfocusedContainerColor = PureWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("memo_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "মোট মেমো: ${filteredMemos.size} টি",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = WaterAqua800
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredMemos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "এখনো কোনো মেমো তৈরি হয়নি" else "কোনো মেমো পাওয়া যায়নি",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMemos, key = { it.memoNumber }) { memo ->
                    val itemsForMemo = allMemoItems.filter { it.memoNumber == memo.memoNumber }

                    LiquidGlossyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("memo_card_${memo.memoNumber}"),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { onMemoClick(memo) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            // Top Row: Memo # & Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = WaterAqua100
                                ) {
                                    Text(
                                        text = "মেমো নং #${memo.memoNumber}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = WaterAqua800,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "${memo.date} ${memo.time}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Customer Details
                            Text(
                                text = "গ্রাহক: ${memo.customerName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ফোন: ${memo.customerPhone} | ঠিকানা: ${memo.customerAddress}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Financials
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "মোট: ৳${String.format("%.0f", memo.totalAmount)}",
                                    fontWeight = FontWeight.Bold,
                                    color = WaterAqua800,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "পরিশোধ: ৳${String.format("%.0f", memo.paidAmount)}",
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (memo.dueAmount > 0) "বকেয়া: ৳${String.format("%.0f", memo.dueAmount)}" else "পরিশোধিত",
                                    color = if (memo.dueAmount > 0) CoralRed else EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                            // Action buttons: প্রিন্ট, শেয়ার, ডিলিট
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Print 7x6 button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val items = viewModel.repository.getMemoItemsSync(memo.memoNumber)
                                            MemoPrintHelper.printMemo(context, memo, items)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("প্রিন্ট", style = MaterialTheme.typography.labelSmall)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Share button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val items = viewModel.repository.getMemoItemsSync(memo.memoNumber)
                                            MemoPrintHelper.shareMemo(context, memo, items)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("শেয়ার", style = MaterialTheme.typography.labelSmall)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Delete Button (if permitted)
                                val canDelete = currentAdmin?.hasPermission("DELETE_MEMO") ?: true
                                if (canDelete) {
                                    IconButton(
                                        onClick = { memoToDelete = memo },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralRed)
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
        }
    }

    // Delete Confirmation Dialog
    memoToDelete?.let { memo ->
        AlertDialog(
            onDismissRequest = { memoToDelete = null },
            title = { Text("মেমো মুছে ফেলা নিশ্চিত করুন") },
            text = { Text("আপনি কি নিশ্চিত যে মেমো #${memo.memoNumber} চিরতরে মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMemo(memo.memoNumber)
                        memoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("মুছে ফেলুন", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { memoToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
