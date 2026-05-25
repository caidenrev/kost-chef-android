package com.example.chef_ai_revan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.chef_ai_revan.ui.components.NeoToastState
import com.example.chef_ai_revan.ui.components.NeoToastType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chef_ai_revan.ui.components.NeoButton
import com.example.chef_ai_revan.ui.components.NeoCard
import com.example.chef_ai_revan.ui.components.NeoTextField
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetScreen(viewModel: BudgetViewModel) {
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val isSurviveMode by viewModel.isSurviveMode.collectAsStateWithLifecycle()
    val isWarning by viewModel.isWeeklyBudgetWarning.collectAsStateWithLifecycle()

    var limitInput by remember { mutableStateOf("") }
    var expenseInput by remember { mutableStateOf("") }

    val localeID = Locale("in", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)
    currencyFormatter.maximumFractionDigits = 0

    LaunchedEffect(budget) {
        if (limitInput.isEmpty() && budget != null) {
            limitInput = budget?.limit?.toInt()?.toString() ?: ""
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "RUPIAH BUDGET TRACKER",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = NeoBlack
            )
        }

        if (budget == null) {
            // Data Kosoh: Tampilkan form inisialisasi agar user bisa input pertama kali
            item {
                NeoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "SET BUDGET PERTAMA ANDA:",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        NeoTextField(
                            value = limitInput,
                            onValueChange = { limitInput = it },
                            placeholder = "Contoh: 500000",
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                        NeoButton(
                            text = "MULAI TRACKING",
                            onClick = {
                                limitInput.toDoubleOrNull()?.let {
                                    viewModel.updateBudgetLimit(it)
                                    NeoToastState.show("Budget diinisialisasi!", NeoToastType.SUCCESS)
                                }
                            },
                            backgroundColor = NeoCyan,
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                    }
                }
            }
            item { com.example.chef_ai_revan.ui.components.NeoShimmerCard(height = 100.dp) }
            item { com.example.chef_ai_revan.ui.components.NeoShimmerCard(height = 100.dp) }
        } else {
            // Data Ada: Tampilkan UI Lengkap
            if (isWarning) {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeoPrimary
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ALARM PENGELUARAN 80%!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Pengeluaran harian/mingguan Anda sudah mencapai 80% dari batas limit!",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            if (isSurviveMode) {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeoPink
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "MODE TANGGAL TUA AKTIF",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Sisa saldo kritis. AI otomatis memprioritaskan resep paling ekonomis.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeoYellow
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "SISA SALDO AKTIF:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = currencyFormatter.format(budget?.currentBalance ?: 0.0),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = NeoBlack
                        )
                        Text(
                            text = "Total Limit Mingguan: ${currencyFormatter.format(budget?.limit ?: 0.0)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            item {
                NeoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "UPDATE LIMIT MINGGUAN:",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        NeoTextField(
                            value = limitInput,
                            onValueChange = { limitInput = it },
                            placeholder = "Contoh: 500000",
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                        NeoButton(
                            text = "UPDATE BUDGET",
                            onClick = {
                                limitInput.toDoubleOrNull()?.let {
                                    viewModel.updateBudgetLimit(it)
                                    NeoToastState.show("Limit diperbarui!", NeoToastType.SUCCESS)
                                }
                            },
                            backgroundColor = NeoPurple,
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                    }
                }
            }

            item {
                NeoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "CATAT PENGELUARAN MANUAL:",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        NeoTextField(
                            value = expenseInput,
                            onValueChange = { expenseInput = it },
                            placeholder = "Contoh: 15000",
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                        NeoButton(
                            text = "POTONG SALDO DOMPET",
                            onClick = {
                                expenseInput.toDoubleOrNull()?.let {
                                    viewModel.addExpense(it)
                                    expenseInput = ""
                                    NeoToastState.show("Saldo dipotong!", NeoToastType.WARNING)
                                }
                            },
                            backgroundColor = NeoPrimary,
                            contentColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                    }
                }
            }
        }
    }
}
