package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AdminEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua200
import com.example.ui.theme.WaterAqua800
import com.example.viewmodel.ZihanViewModel

val ALL_PERMISSIONS = listOf(
    "CREATE_MEMO" to "মেমো তৈরি করার অনুমতি",
    "EDIT_MEMO" to "মেমো এডিট করার অনুমতি",
    "DELETE_MEMO" to "মেমো ডিলিট করার অনুমতি",
    "PAY_DUE" to "টাকা পরিশোধ (বকেয়া) কনফার্ম করার অনুমতি",
    "DELIVER_ORDER" to "অর্ডার ডেলিভারি কনফার্ম করার অনুমতি",
    "MANAGE_PRODUCTS" to "প্রোডাক্ট কন্ট্রোল অনুমতি",
    "MANAGE_ADMINS" to "এডমিন তৈরি ও কন্ট্রোল অনুমতি",
    "APP_SETTINGS" to "অ্যাপস সেটিংস পরিবর্তনের অনুমতি"
)

@Composable
fun AdminControlScreen(
    viewModel: ZihanViewModel
) {
    val context = LocalContext.current
    val allAdmins by viewModel.allAdmins.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var adminToEdit by remember { mutableStateOf<AdminEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "এডমিন কন্ট্রোল",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )
                    Text(
                        text = "এডমিন তৈরি, অনুমতি ও তথ্য পরিচালনা",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("create_admin_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন এডমিন", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List existing admins for editing
        items(allAdmins, key = { it.id }) { admin ->
            LiquidGlossyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            text = "অনুমোদিত ক্ষমতা: ${if (admin.isSuperAdmin) "সকল ক্ষমতা" else "${admin.permissions.split(",").size} টি অনুমতি"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OceanBlue500
                        )
                    }

                    // Edit button
                    Button(
                        onClick = { adminToEdit = admin },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এডিট", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Create New Admin Dialog
    if (showCreateDialog) {
        CreateAdminDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { name, username, pass, photoUri, permissions ->
                viewModel.addAdmin(name, username, pass, photoUri, permissions)
                showCreateDialog = false
                Toast.makeText(context, "নতুন এডমিন সফলভাবে তৈরি হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Admin Dialog
    adminToEdit?.let { admin ->
        EditAdminDialog(
            admin = admin,
            isSuperUser = currentAdmin?.isSuperAdmin == true,
            onDismiss = { adminToEdit = null },
            onSave = { updatedAdmin ->
                viewModel.updateAdmin(updatedAdmin)
                adminToEdit = null
                Toast.makeText(context, "এডমিনের তথ্য সফলভাবে আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CreateAdminDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String?, String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri?.toString()
    }

    val selectedPermissions = remember {
        mutableStateMapOf<String, Boolean>().apply {
            ALL_PERMISSIONS.forEach { (perm, _) -> put(perm, true) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "নতুন এডমিন তৈরি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Photo Selection: "নতুন অ্যাডমিন তৈরি করতে হলে এডমিনের ছবি দিতেই হবে"
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(WaterAqua100)
                            .clickable { photoPickerLauncher.launch("image/*") }
                            .border(2.dp, WaterAqua800, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Admin Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Photo", tint = WaterAqua800)
                                Text("ছবি দিন*", style = MaterialTheme.typography.labelSmall, color = WaterAqua800)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("এডমিনের নাম*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("ইউজার আইডি*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("পাসওয়ার্ড*") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "কাজের অনুমতি নির্বাচন করুন:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(ALL_PERMISSIONS) { (perm, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPermissions[perm] = !(selectedPermissions[perm] ?: false) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedPermissions[perm] == true,
                            onCheckedChange = { selectedPermissions[perm] = it },
                            colors = CheckboxDefaults.colors(checkedColor = WaterAqua800)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("বাতিল")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || username.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "নাম, ইউজার আইডি ও পাসওয়ার্ড আবশ্যক", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val permsString = selectedPermissions.filter { it.value }.keys.joinToString(",")
                                onSave(name, username, password, photoUri, permsString)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("এডমিন তৈরি করুন", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditAdminDialog(
    admin: AdminEntity,
    isSuperUser: Boolean,
    onDismiss: () -> Unit,
    onSave: (AdminEntity) -> Unit
) {
    var name by remember { mutableStateOf(admin.name) }
    var password by remember { mutableStateOf(admin.password) }

    val adminPermsList = admin.permissions.split(",").map { it.trim() }
    val selectedPermissions = remember {
        mutableStateMapOf<String, Boolean>().apply {
            ALL_PERMISSIONS.forEach { (perm, _) -> put(perm, adminPermsList.contains(perm)) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlossyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "এডমিনের তথ্য এডিট",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("এডমিনের নাম") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("নতুন পাসওয়ার্ড") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "অনুমতি পরিবর্তন (প্রধান এডমিন):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(ALL_PERMISSIONS) { (perm, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isSuperUser) {
                                if (isSuperUser) {
                                    selectedPermissions[perm] = !(selectedPermissions[perm] ?: false)
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedPermissions[perm] == true,
                            onCheckedChange = { if (isSuperUser) selectedPermissions[perm] = it },
                            enabled = isSuperUser,
                            colors = CheckboxDefaults.colors(checkedColor = WaterAqua800)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(label, style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("বাতিল")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val permsString = selectedPermissions.filter { it.value }.keys.joinToString(",")
                                onSave(admin.copy(name = name, password = password, permissions = permsString))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("সংরক্ষণ করুন", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
