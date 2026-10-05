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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.AdminEntity
import com.example.ui.components.AdminDetailDialog
import com.example.ui.theme.CoralRed
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua800
import com.example.viewmodel.ZihanViewModel

@Composable
fun AdminsScreen(
    viewModel: ZihanViewModel
) {
    val allAdmins by viewModel.allAdmins.collectAsState()
    val allMemos by viewModel.allMemos.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var selectedAdminForDetail by remember { mutableStateOf<AdminEntity?>(null) }
    var adminToDelete by remember { mutableStateOf<AdminEntity?>(null) }

    val isSuper = currentAdmin?.isSuperAdmin == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "সকল এডমিনের তালিকা",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = WaterAqua800
        )
        Text(
            text = "এডমিনের ওপর ট্যাপ করে কাজের বিবরণ দেখুন",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(allAdmins) { index, admin ->
                val adminMemos = allMemos.filter { it.adminId == admin.id }
                val totalMemos = adminMemos.size
                val totalAmount = adminMemos.sumOf { it.totalAmount }

                LiquidGlossyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_card_${admin.id}"),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { selectedAdminForDetail = admin }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Serial number badge
                        Surface(
                            shape = CircleShape,
                            color = WaterAqua100,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    color = WaterAqua800,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Admin Photo
                        if (!admin.photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = admin.photoUri,
                                contentDescription = admin.name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Admin Avatar",
                                tint = WaterAqua800,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = admin.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (admin.isSuperAdmin) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = WaterAqua100
                                    ) {
                                        Text(
                                            text = "প্রধান এডমিন",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = WaterAqua800,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "ইউজার আইডি: ${admin.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "তৈরিকৃত মেমো: $totalMemos টি | বিক্রয়: ৳${String.format("%.0f", totalAmount)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = WaterAqua800,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Delete button: "প্রধান এডমিন সমস্ত এডমিনের তথ্য ডিলিট করতে পারবে"
                        if (isSuper && !admin.isSuperAdmin) {
                            IconButton(
                                onClick = { adminToDelete = admin },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Admin",
                                    tint = CoralRed
                                )
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

    // Admin Details Popup
    selectedAdminForDetail?.let { admin ->
        AdminDetailDialog(
            admin = admin,
            memos = allMemos,
            onDismiss = { selectedAdminForDetail = null }
        )
    }

    // Delete Confirmation Dialog
    adminToDelete?.let { admin ->
        AlertDialog(
            onDismissRequest = { adminToDelete = null },
            title = { Text("এডমিন মুছে ফেলা") },
            text = { Text("আপনি কি নিশ্চিত যে '${admin.name}' কে এডমিন তালিকা থেকে মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAdmin(admin)
                        adminToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("মুছে ফেলুন", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { adminToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
