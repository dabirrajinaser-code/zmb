package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.LiquidGlossyCard
import com.example.ui.theme.OceanBlue500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WaterAqua100
import com.example.ui.theme.WaterAqua200
import com.example.ui.theme.WaterAqua800
import com.example.viewmodel.ZihanViewModel

@Composable
fun ProductControlScreen(
    viewModel: ZihanViewModel
) {
    val context = LocalContext.current
    val allProducts by viewModel.allProducts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val filteredProducts = allProducts.filter {
        if (searchQuery.isBlank()) true
        else it.name.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "প্রোডাক্ট কন্ট্রোল",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WaterAqua800
                )
                Text(
                    text = "নতুন পণ্য যোগ, নাম ও দর পরিবর্তন",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_product_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন পণ্য", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("মাছের রেনু পোনার নাম দিয়ে খুঁজুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WaterAqua800) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WaterAqua800,
                unfocusedBorderColor = WaterAqua200,
                focusedContainerColor = PureWhite,
                unfocusedContainerColor = PureWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "মোট পণ্য: ${filteredProducts.size} টি",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = WaterAqua800
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                LiquidGlossyCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = WaterAqua100
                                ) {
                                    Text(
                                        text = product.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WaterAqua800,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "দর: ৳${String.format("%.0f", product.defaultPrice)} / ${product.unit}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterAqua800
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { productToEdit = product }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Product", tint = OceanBlue500)
                            }
                            IconButton(onClick = { productToDelete = product }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = CoralRed)
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

    // Add Product Dialog
    if (showAddDialog) {
        ProductDialog(
            title = "নতুন পণ্য যোগ করুন",
            initialName = "",
            initialCategory = "রেনু পোনা",
            initialUnit = "কেজি",
            initialPrice = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, unit, price ->
                viewModel.addProduct(name, cat, unit, price)
                showAddDialog = false
                Toast.makeText(context, "নতুন পণ্য সফলভাবে যুক্ত হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Product Dialog
    productToEdit?.let { product ->
        ProductDialog(
            title = "পণ্যের নাম ও দর এডিট",
            initialName = product.name,
            initialCategory = product.category,
            initialUnit = product.unit,
            initialPrice = String.format("%.0f", product.defaultPrice),
            onDismiss = { productToEdit = null },
            onConfirm = { name, cat, unit, price ->
                viewModel.updateProduct(product.copy(name = name, category = cat, unit = unit, defaultPrice = price))
                productToEdit = null
                Toast.makeText(context, "পণ্য সফলভাবে আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Product Confirmation
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("পণ্য মুছে ফেলা") },
            text = { Text("আপনি কি নিশ্চিত যে '${product.name}' মুছে ফেলতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("মুছে ফেলুন", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun ProductDialog(
    title: String,
    initialName: String,
    initialCategory: String,
    initialUnit: String,
    initialPrice: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var unit by remember { mutableStateOf(initialUnit) }
    var priceText by remember { mutableStateOf(initialPrice) }

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
                    .padding(18.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = WaterAqua800
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("মাছের রেনু/পণ্যর নাম") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("ক্যাটাগরি") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("একক (কেজি/বাটি)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("ডিফল্ট দর (টাকা)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

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
                            val price = priceText.toDoubleOrNull() ?: 0.0
                            if (name.isBlank() || price <= 0.0) {
                                Toast.makeText(context, "সঠিক নাম ও দর দিন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            onConfirm(name, category, unit, price)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("সংরক্ষণ", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
