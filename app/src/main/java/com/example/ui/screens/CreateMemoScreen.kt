package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MemoEntity
import com.example.data.model.MemoItemEntity
import com.example.data.model.ProductEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateMemoScreen(
    viewModel: ZihanViewModel,
    onMemoCreated: (MemoEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allProducts by viewModel.allProducts.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val allMemos by viewModel.allMemos.collectAsState()

    // Customer info states
    var customerPhone by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    // Auto-lookup matching tracking
    var lastLookedUpPhone by remember { mutableStateOf("") }
    var autoFilledPhone by remember { mutableStateOf<String?>(null) }

    // Items list for this memo
    val memoItems = remember { mutableStateListOf<MemoItemEntity>() }

    // Product selector state
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var productDropdownExpanded by remember { mutableStateOf(false) }
    var quantityText by remember { mutableStateOf("1") }
    var unitPriceText by remember { mutableStateOf("") }
    var unitText by remember { mutableStateOf("কেজি") }

    // Payment state
    var paidAmountText by remember { mutableStateOf("") }

    // Memo created dialog / print dialog
    var createdMemoForPrint by remember { mutableStateOf<MemoEntity?>(null) }

    // Next Serial Number calculation
    val nextSerial = (allMemos.maxOfOrNull { it.memoNumber } ?: 1000) + 1

    // Phone Auto-Lookup logic:
    // "মেমো তৈরি সময় ফোন নাম্বার অপশনে ফোন নাম্বার দেওয়ার পরে গ্রাহকের নাম ঠিকানা অটোমেটিক বসে যাবে
    // ফোন নাম্বার একটি সংখ্যা আলাদা হলে নাম ঠিকানায় ঘর থেকে নাম ,ঠিকানা চলে যাবে।"
    LaunchedEffect(customerPhone) {
        val trimmed = customerPhone.trim()
        if (trimmed.length >= 6) {
            coroutineScope.launch {
                val customer = viewModel.lookupCustomerByPhone(trimmed)
                if (customer != null) {
                    customerName = customer.name
                    customerAddress = customer.address
                    autoFilledPhone = trimmed
                } else {
                    // If previously auto-filled by another phone and now digits differ, clear fields
                    if (autoFilledPhone != null && autoFilledPhone != trimmed) {
                        customerName = ""
                        customerAddress = ""
                        autoFilledPhone = null
                    }
                }
            }
        } else {
            if (autoFilledPhone != null) {
                customerName = ""
                customerAddress = ""
                autoFilledPhone = null
            }
        }
    }

    val totalAmount = memoItems.sumOf { it.totalPrice }
    val paidAmount = paidAmountText.toDoubleOrNull() ?: totalAmount
    val dueAmount = (totalAmount - paidAmount).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "নতুন মেমো তৈরি",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WaterAqua800
            )
            Text(
                text = "ক্রমিক নং: #$nextSerial (স্বয়ংক্রিয়)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // 1. Customer Information Card
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
                    Text(
                        text = "গ্রাহকের তথ্য",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number Field (triggers auto-fill)
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("গ্রাহকের ফোন নাম্বার") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = "Phone", tint = WaterAqua800)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaterAqua800,
                            unfocusedBorderColor = WaterAqua200,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("memo_customer_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Customer Name Field
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("গ্রাহকের নাম") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name", tint = WaterAqua800)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaterAqua800,
                            unfocusedBorderColor = WaterAqua200,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("memo_customer_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Customer Address Field
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("গ্রাহকের ঠিকানা") },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = "Address", tint = WaterAqua800)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaterAqua800,
                            unfocusedBorderColor = WaterAqua200,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("memo_customer_address_input")
                    )
                }
            }
        }

        // 2. Product Selection & Add Item Card
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
                    Text(
                        text = "পণ্য নির্বাচন ও যোগ করুন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Product Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedProduct?.name ?: "মাছের রেনু/পোনা সিলেক্ট করুন",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("পণ্য") },
                            trailingIcon = {
                                IconButton(onClick = { productDropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Product")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WaterAqua800,
                                unfocusedBorderColor = WaterAqua200,
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { productDropdownExpanded = true }
                                .testTag("select_product_dropdown")
                        )

                        DropdownMenu(
                            expanded = productDropdownExpanded,
                            onDismissRequest = { productDropdownExpanded = false }
                        ) {
                            allProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${product.name} (৳${String.format("%.0f", product.defaultPrice)}/${product.unit})")
                                    },
                                    onClick = {
                                        selectedProduct = product
                                        unitPriceText = String.format("%.0f", product.defaultPrice)
                                        unitText = product.unit
                                        productDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quantity
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("পরিমাণ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WaterAqua800,
                                unfocusedBorderColor = WaterAqua200,
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_qty_input")
                        )

                        // Unit
                        OutlinedTextField(
                            value = unitText,
                            onValueChange = { unitText = it },
                            label = { Text("একক") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WaterAqua800,
                                unfocusedBorderColor = WaterAqua200,
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            modifier = Modifier.weight(0.8f)
                        )

                        // Unit Price
                        OutlinedTextField(
                            value = unitPriceText,
                            onValueChange = { unitPriceText = it },
                            label = { Text("দর (টাকা)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WaterAqua800,
                                unfocusedBorderColor = WaterAqua200,
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("product_price_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add to item list button
                    Button(
                        onClick = {
                            val product = selectedProduct
                            val qty = quantityText.toDoubleOrNull() ?: 1.0
                            val rate = unitPriceText.toDoubleOrNull() ?: 0.0

                            if (product != null && rate > 0.0 && qty > 0.0) {
                                memoItems.add(
                                    MemoItemEntity(
                                        memoNumber = 0,
                                        productName = product.name,
                                        quantity = qty,
                                        unit = unitText.ifBlank { "কেজি" },
                                        unitPrice = rate,
                                        totalPrice = qty * rate
                                    )
                                )
                                // Reset inputs
                                selectedProduct = null
                                quantityText = "1"
                                unitPriceText = ""
                            } else {
                                Toast.makeText(context, "দয়া করে পণ্য, পরিমাণ ও সঠিক দর দিন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_item_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("তালিকায় যোগ করুন", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Purchased Items List
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
                    Text(
                        text = "ক্রয়কৃত পণ্যের তালিকা (${memoItems.size} টি)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WaterAqua800
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                    if (memoItems.isEmpty()) {
                        Text(
                            text = "এখনো কোনো পণ্য যোগ করা হয়নি। উপরের ফর্ম থেকে পণ্য সিলেক্ট করে যোগ করুন।",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        memoItems.forEachIndexed { index, item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = WaterAqua100.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
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
                                            text = "${index + 1}. ${item.productName}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${item.quantity} ${item.unit} × ৳${String.format("%.0f", item.unitPrice)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray
                                        )
                                    }
                                    Text(
                                        text = "৳ ${String.format("%.0f", item.totalPrice)}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WaterAqua800
                                    )
                                    IconButton(
                                        onClick = { memoItems.removeAt(index) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = CoralRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WaterAqua100)

                        // Totals Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোট মূল্য:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("৳ ${String.format("%.0f", totalAmount)}", fontWeight = FontWeight.ExtraBold, color = WaterAqua800, style = MaterialTheme.typography.titleMedium)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Paid Amount field
                        OutlinedTextField(
                            value = paidAmountText,
                            onValueChange = { paidAmountText = it },
                            label = { Text("পরিশোধিত টাকা (৳)") },
                            placeholder = { Text(String.format("%.0f", totalAmount)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WaterAqua800,
                                unfocusedBorderColor = WaterAqua200,
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paid_amount_input")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("বকেয়া টাকা:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "৳ ${String.format("%.0f", dueAmount)}",
                                fontWeight = FontWeight.Bold,
                                color = if (dueAmount > 0) CoralRed else EmeraldGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // 4. Save & Print Buttons
        item {
            Button(
                onClick = {
                    if (customerName.isBlank()) {
                        Toast.makeText(context, "দয়া করে গ্রাহকের নাম লিখুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (memoItems.isEmpty()) {
                        Toast.makeText(context, "মেমোতে অন্তত একটি পণ্য যোগ করুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.createMemo(
                        customerPhone = customerPhone,
                        customerName = customerName,
                        customerAddress = customerAddress,
                        items = memoItems.toList(),
                        paidAmount = paidAmount,
                        notes = ""
                    ) { createdMemoNumber ->
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val now = Date()

                        val newMemo = MemoEntity(
                            memoNumber = createdMemoNumber,
                            customerPhone = customerPhone,
                            customerName = customerName,
                            customerAddress = customerAddress,
                            date = dateFormat.format(now),
                            time = timeFormat.format(now),
                            totalAmount = totalAmount,
                            paidAmount = paidAmount,
                            isPaid = paidAmount >= totalAmount,
                            isDelivered = false,
                            adminId = currentAdmin?.id ?: 1,
                            adminName = currentAdmin?.name ?: "এডমিন"
                        )
                        createdMemoForPrint = newMemo
                    }
                },
                enabled = memoItems.isNotEmpty() && customerName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WaterAqua800,
                    disabledContainerColor = WaterAqua200
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_and_print_memo_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Memo")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "মেমো সংরক্ষণ ও প্রিন্ট করুন",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Created Memo Success & Print Dialog (Formatted as 7x6 inch slip)
    createdMemoForPrint?.let { memo ->
        Dialog(onDismissRequest = {
            createdMemoForPrint = null
            onMemoCreated(memo)
        }) {
            LiquidGlossyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "মেমো সফলভাবে তৈরি হয়েছে!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "মেমো নং: #${memo.memoNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Print & Share action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                MemoPrintHelper.printMemo(context, memo, memoItems.toList())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WaterAqua800),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("print_button")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("প্রিন্ট (৭×৬\")", color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                MemoPrintHelper.shareMemo(context, memo, memoItems.toList())
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OceanBlue500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("শেয়ার", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(
                        onClick = {
                            createdMemoForPrint = null
                            onMemoCreated(memo)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("মেমো তালিকায় যান", color = WaterAqua800, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
