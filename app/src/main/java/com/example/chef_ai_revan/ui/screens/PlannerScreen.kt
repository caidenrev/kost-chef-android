package com.example.chef_ai_revan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chef_ai_revan.data.entity.WeeklyPlan
import com.example.chef_ai_revan.ui.components.NeoButton
import com.example.chef_ai_revan.ui.components.NeoCard
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlannerScreen(viewModel: BudgetViewModel) {
    val context = LocalContext.current
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val weeklyPlan by viewModel.weeklyPlan.collectAsStateWithLifecycle()

    val localeID = Locale("in", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)
    currencyFormatter.maximumFractionDigits = 0

    // Calculations
    val limit = budget?.limit ?: 0.0
    val totalPlannedCost = weeklyPlan.sumOf { it.estimatedCost }
    val remainingForPlan = limit - totalPlannedCost
    val isLimitExceeded = totalPlannedCost > limit
    val isWarning = limit > 0.0 && (totalPlannedCost / limit) >= 0.8

    val dayColors = listOf(NeoGreen, NeoPink, NeoCyan, NeoYellow, NeoPurple, Color(0xFFFF9900), NeoCyan)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "PERENCANAAN MINGGUAN",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = NeoBlack
            )
        }

        // Summary dashboard card
        item {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isLimitExceeded) NeoPrimary else if (isWarning) NeoYellow else NeoWhite
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SUMMARY PLANNED SPENDING:",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isLimitExceeded) Color.White else NeoBlack
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rencana Belanja:",
                            fontSize = 14.sp,
                            color = if (isLimitExceeded) Color.White else NeoBlack
                        )
                        Text(
                            text = currencyFormatter.format(totalPlannedCost),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (isLimitExceeded) Color.White else NeoPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Limit Mingguan:",
                            fontSize = 14.sp,
                            color = if (isLimitExceeded) Color.White else NeoBlack
                        )
                        Text(
                            text = currencyFormatter.format(limit),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (isLimitExceeded) Color.White else NeoBlack
                        )
                    }
                    Divider(color = if (isLimitExceeded) Color.White else NeoBlack, modifier = Modifier.padding(vertical = 4.dp))
                    
                    if (isLimitExceeded) {
                        Text(
                            text = "⚠️ OVER LIMIT! Rencana makan melebihi batas saldo mingguan Anda!",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    } else if (isWarning) {
                        Text(
                            text = "⚠️ ALARM PENGELUARAN! Rencana makan sudah mencapai 80% dari batas limit!",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = NeoPrimary
                        )
                    } else {
                        Text(
                            text = "Aman! Tersedia Rp ${currencyFormatter.format(remainingForPlan)} untuk slot menu berikutnya.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Action to clear entire plan
        item {
            NeoButton(
                text = "BERSIHKAN SEMUA RENCANA",
                onClick = {
                    viewModel.clearAllWeeklyPlans()
                    Toast.makeText(context, "Jadwal mingguan dikosongkan!", Toast.LENGTH_SHORT).show()
                },
                backgroundColor = NeoBlack,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 7 Day Meal planner rows
        if (weeklyPlan.isEmpty()) {
            item {
                CircularProgressIndicator(color = NeoPurple, modifier = Modifier.padding(16.dp))
            }
        } else {
            items(weeklyPlan) { dayPlan ->
                val dayColor = dayColors[dayPlan.dayIndex % dayColors.size]
                DayPlannerCard(
                    plan = dayPlan,
                    accentColor = dayColor,
                    currencyFormatter = currencyFormatter,
                    onClear = {
                        viewModel.clearWeeklyPlanForDay(dayPlan.dayIndex, dayPlan.estimatedCost)
                        Toast.makeText(context, "Rencana hari ${dayPlan.dayName} dihapus!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun DayPlannerCard(
    plan: WeeklyPlan,
    accentColor: Color,
    currencyFormatter: NumberFormat,
    onClear: () -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day Badge
                Row(
                    modifier = Modifier
                        .background(accentColor, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.dayName,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = NeoBlack
                    )
                }

                // If meal assigned, show clear button
                if (plan.recipeName != null) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = NeoPink,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onClear() }
                    )
                }
            }

            if (plan.recipeName != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = plan.recipeName.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = NeoBlack
                    )
                    Text(
                        text = "Biaya Terpotong: " + currencyFormatter.format(plan.estimatedCost),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = NeoPrimary
                    )
                    Text(
                        text = plan.description ?: "",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = Color.Gray)
                    Text(
                        text = "Belum ada rencana makan. Buka tab MENU untuk cari dan generasikan resep AI favorit Anda!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
