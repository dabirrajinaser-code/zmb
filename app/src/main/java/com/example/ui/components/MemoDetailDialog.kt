package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua800
import com.example.util.MemoPrintHelper
import com.example.viewmodel.ZihanViewModel

@Composable
fun MemoDetailDialog(
    memo: MemoEntity,
    viewModel: ZihanViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf<List<MemoItemEntity>>(emptyList()) }

    LaunchedEffect(memo.memoNumber) {
        items = viewModel.repository.getMemoItemsSync(memo.memoNumber)
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top close & title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মেমোর পূর্ণাঙ্গ রসিদ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = WaterAqua100)

                // Memo Slip Container (Simulating 7x6 slip)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(2.dp, WaterAqua800),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            // Bismillah
                            Text(
                                text = "بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيْمِ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WaterAqua800,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Shop Name
                            Text(
                                text = "জিহান মৎস্য বীজাগার",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = WaterAqua800,
                                textAlign = TextAlign.Center
                            )
                            // Address
                            Text(
                                text = "ঠিকানা: জগন্নাথপুর (হাজির বাজার), মোকামতলা, বগুড়া-৫৮১০",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                            // Phone
                            Text(
                                text = "ফোন: +8801722736633, +8801711428073",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = OceanBlue500,
                                textAlign = TextAlign.Center
                            )
                            // Director
                            Text(
                                text = "পরিচালক: মোঃ ময়নুল সাদিক (জোহা)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // 2-line variety fish list
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WaterAqua100.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "এখানে উন্নতমানের রুই, মৃগেল, কাতলা, বাটা, সীলভার, গ্লাসকাপ, স্বরপুটি,",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = WaterAqua800,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "পাঙ্গাস, হাংরি ,দেশী মাগুর , কৈ, টেংরা, গুলশা, পাবদা মাছের রেনু পোনা পাওয়া যায়",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = WaterAqua800, thickness = 2.dp)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Memo # & Date
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "মেমো নং: #${memo.memoNumber}",
                                    fontWeight = FontWeight.Bold,
                                    color = WaterAqua800,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "তারিখ: ${memo.date} ${memo.time}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Customer Details
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("গ্রাহকের নাম: ${memo.customerName}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                Text("ঠিকানা: ${memo.customerAddress}", style = MaterialTheme.typography.bodySmall)
                                Text("মোবাইল: ${memo.customerPhone}", style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Items Table Header
                            Surface(
                                color = WaterAqua100,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("পণ্যের নাম", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(2f))
                                    Text("পরিমাণ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                                    Text("দর", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                                    Text("মোট", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                            }
                        }

                        items(items) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.productName, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(2f))
                                Text("${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("৳${String.format("%.0f", item.unitPrice)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("৳${String.format("%.0f", item.totalPrice)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Financial Totals
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                                Text("সর্বমোট: ৳${String.format("%.0f", memo.totalAmount)}", fontWeight = FontWeight.Bold, color = WaterAqua800, style = MaterialTheme.typography.bodyMedium)
                                Text("পরিশোধ: ৳${String.format("%.0f", memo.paidAmount)}", fontWeight = FontWeight.SemiBold, color = EmeraldGreen, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = if (memo.dueAmount > 0) "বকেয়া: ৳${String.format("%.0f", memo.dueAmount)}" else "বকেয়া: ৳০ (পরিশোধিত)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (memo.dueAmount > 0) CoralRed else EmeraldGreen,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Signatures
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("....................................", color = Color.Gray)
                                    Text("ক্রেতার স্বাক্ষর", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("....................................", color = Color.Gray)
                                    Text("বিক্রেতা স্বাক্ষর", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                    Text("(${memo.adminName.ifBlank { "এডমিন" }})", style = MaterialTheme.typography.labelSmall, color = WaterAqua800)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom Notice
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WaterAqua100.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "এখানে মাছের খাদ্য ও ওষুধ পাওয়া যায়",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WaterAqua800,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            MemoPrintHelper.printMemo(context, memo, items)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্রিন্ট (৭×৬\")", color = PureWhite, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            MemoPrintHelper.shareMemo(context, memo, items)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("শেয়ার", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
