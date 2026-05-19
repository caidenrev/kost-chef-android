package com.example.chef_ai_revan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chef_ai_revan.data.entity.GroceryItem
import com.example.chef_ai_revan.ui.components.*
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GroceryScreen(viewModel: BudgetViewModel) {
    val context = LocalContext.current
    val items by viewModel.groceryItems.collectAsStateWithLifecycle()
    var itemName by remember { mutableStateOf("") }
    var itemCost by remember { mutableStateOf("") }

    val localeID = Locale("in", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)
    currencyFormatter.maximumFractionDigits = 0

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DAFTAR BELANJA WARUNG",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = NeoBlack
            )

            // Bulk Actions: Export PDF
            if (items.isNotEmpty()) {
                NeoButton(
                    text = "EKSPOR DAFTAR BELANJA PDF",
                    onClick = {
                        val itemsStr = items.map { 
                            val checkSymbol = if (it.isChecked) "[x]" else "[ ]"
                            "$checkSymbol ${it.name} (${currencyFormatter.format(it.estimatedCost)})"
                        }
                        PdfExporter.exportGroceryPdf(context, itemsStr)
                        Toast.makeText(context, "Daftar belanja siap diekspor!", Toast.LENGTH_SHORT).show()
                    },
                    backgroundColor = NeoCyan,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Add item card
            NeoCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TAMBAH BAHAN BELANJAAN BARU:",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    NeoTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        placeholder = "Nama bahan (misal: Sosis, Telur, Sawi)",
                        modifier = Modifier.fillMaxWidth(),
                        borderRadius = 8.dp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeoTextField(
                            value = itemCost,
                            onValueChange = { itemCost = it },
                            placeholder = "Estimasi Harga (Rp)",
                            modifier = Modifier.weight(1f),
                            borderRadius = 8.dp
                        )
                        NeoButton(
                            text = "TAMBAH",
                            onClick = {
                                if (itemName.isNotBlank()) {
                                    viewModel.addGroceryItem(itemName, itemCost.toDoubleOrNull() ?: 0.0)
                                    itemName = ""
                                    itemCost = ""
                                    Toast.makeText(context, "Item dimasukkan!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            backgroundColor = NeoGreen,
                            modifier = Modifier.width(135.dp).fillMaxHeight(),
                            borderRadius = 8.dp
                        )
                    }
                }
            }
        }

        // Scrollable checklist items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.LightGray
                    ) {
                        Text(
                            text = "Catatan belanja masih kosong! Tambahkan resep dari Menu Utama atau catat bahan belanjaan manual di atas.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(items) { item ->
                    GroceryItemRow(
                        item = item,
                        currencyFormatter = currencyFormatter,
                        onCheckedChange = { viewModel.toggleGroceryItem(item) },
                        onDelete = {
                            viewModel.deleteGroceryItem(item)
                            Toast.makeText(context, "Item dihapus dari catatan!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroceryItemRow(
    item: GroceryItem,
    currencyFormatter: NumberFormat,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neoShadow(
                color = NeoBlack,
                offsetX = 6.dp,
                offsetY = 6.dp,
                borderRadius = 12.dp
            )
            .background(if (item.isChecked) Color(0xFFEEEEEE) else Color.White, shape)
            .border(3.dp, NeoBlack, shape)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = NeoBlack,
                    uncheckedColor = NeoBlack,
                    checkmarkColor = NeoGreen
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    color = if (item.isChecked) Color.Gray else NeoBlack
                )
                Text(
                    text = "Harga: " + currencyFormatter.format(item.estimatedCost),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (item.isChecked) Color.Gray else NeoPrimary
                )
            }
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                modifier = Modifier
                    .clickable { onDelete() }
                    .size(24.dp),
                tint = NeoPink
            )
        }
    }
}
