package com.example.chef_ai_revan.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val isSurviveMode by viewModel.isSurviveMode.collectAsStateWithLifecycle()
    val isWarning by viewModel.isWeeklyBudgetWarning.collectAsStateWithLifecycle()

    val syncEmail by viewModel.syncEmail.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

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

        // Budget Warning monitor: 80% trigger
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
                            text = "Pengeluaran harian/mingguan Anda sudah mencapai 80% dari batas limit! Dompet kritis, mulailah berhemat ya.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Mode Tanggal Tua Trigger
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
                            text = "Sisa saldo kurang dari Rp 10.000 atau di bawah 15%. AI otomatis memprioritaskan resep paling ekonomis di menu utama.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Wallet Balance details
        item {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSurviveMode) NeoPink.copy(alpha = 0.15f) else NeoYellow
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Limit Mingguan: ${currencyFormatter.format(budget?.limit ?: 0.0)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Limit setting inputs
        item {
            NeoCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ATUR LIMIT MINGGUAN BARU:",
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
                        text = "SET BUDGET MINGGUAN",
                        onClick = {
                            limitInput.toDoubleOrNull()?.let {
                                viewModel.updateBudgetLimit(it)
                                Toast.makeText(context, "Limit budget mingguan diperbarui!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        backgroundColor = NeoBlack,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        borderRadius = 8.dp
                    )
                }
            }
        }

        // Expense tracking inputs
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
                                Toast.makeText(context, "Saldo dipotong!", Toast.LENGTH_SHORT).show()
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

        // Cloud sync section
        item {
            val isSynced = syncEmail != null
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSynced) NeoGreen.copy(alpha = 0.2f) else Color.White
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CLOUDSYNC & KEAMANAN AKUN",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Simpan riwayat resep favorit, histori belanja, dan data dompet limit mingguan secara aman di cloud Firestore.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSyncing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeoWhite, shape = RoundedCornerShape(8.dp))
                                .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = NeoPurple, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Menghubungkan Akun Google...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else if (isSynced) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NeoWhite, shape = RoundedCornerShape(8.dp))
                                    .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(NeoGreen, shape = CircleShape)
                                )
                                Text(
                                    text = "Google Sync Aktif: $syncEmail",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            NeoButton(
                                text = "LOGOUT DARI GOOGLE SYNC",
                                onClick = {
                                    viewModel.logoutGoogleSync()
                                    Toast.makeText(context, "Google Sync Dihentikan!", Toast.LENGTH_SHORT).show()
                                },
                                backgroundColor = NeoPink,
                                modifier = Modifier.fillMaxWidth(),
                                borderRadius = 8.dp
                            )
                        }
                    } else {
                        NeoButton(
                            text = "LOGIN DENGAN GOOGLE",
                            onClick = {
                                viewModel.triggerGoogleCloudSync()
                            },
                            backgroundColor = NeoCyan,
                            modifier = Modifier.fillMaxWidth(),
                            borderRadius = 8.dp
                        )
                    }
                }
            }
        }
    }
}
