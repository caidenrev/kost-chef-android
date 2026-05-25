package com.example.chef_ai_revan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.chef_ai_revan.data.entity.WeeklyPlan
import com.example.chef_ai_revan.ui.components.NeoButton
import com.example.chef_ai_revan.ui.components.NeoCard
import com.example.chef_ai_revan.ui.components.neoShadow
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlannerScreen(viewModel: BudgetViewModel) {
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val weeklyPlan by viewModel.weeklyPlan.collectAsStateWithLifecycle()

    val localeID = Locale("in", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)
    currencyFormatter.maximumFractionDigits = 0

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

        // Summary card
        item {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isLimitExceeded) NeoPrimary else if (isWarning) NeoYellow else NeoWhite
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "RINGKASAN RENCANA MAKAN:",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isLimitExceeded) Color.White else NeoBlack
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Rencana:",
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
                    HorizontalDivider(
                        color = if (isLimitExceeded) Color.White else NeoBlack,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isLimitExceeded) Icons.Default.Warning
                                          else if (isWarning) Icons.Default.Warning
                                          else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isLimitExceeded) Color.White
                                   else if (isWarning) NeoPrimary
                                   else NeoGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = when {
                                isLimitExceeded -> "OVER LIMIT! Rencana makan melebihi batas saldo mingguan."
                                isWarning -> "Rencana makan sudah mencapai 80% dari batas limit."
                                else -> "Aman! Tersisa ${currencyFormatter.format(remainingForPlan)} untuk slot berikutnya."
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isLimitExceeded) Color.White
                                    else if (isWarning) NeoPrimary
                                    else Color.DarkGray
                        )
                    }
                }
            }
        }

        item {
            NeoButton(
                text = "BERSIHKAN SEMUA RENCANA",
                onClick = {
                    viewModel.clearAllWeeklyPlans()
                    NeoToastState.show("Jadwal mingguan dikosongkan!", NeoToastType.DELETE)
                },
                backgroundColor = NeoBlack,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                        NeoToastState.show("Rencana hari ${dayPlan.dayName} dihapus!", NeoToastType.DELETE)
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
    var isExpanded by remember { mutableStateOf(false) }

    // Parse ingredients dan steps dari string tersimpan
    val ingredients: List<Pair<String, Double>> = remember(plan.ingredientsList) {
        plan.ingredientsList
            ?.split("||")
            ?.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size >= 2) {
                    val name = parts.dropLast(1).joinToString(":")
                    val price = parts.last().toDoubleOrNull() ?: 0.0
                    name to price
                } else null
            } ?: emptyList()
    }
    val steps: List<String> = remember(plan.steps) {
        plan.steps?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
    }

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // ── Header baris: badge hari + tombol hapus ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(accentColor, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = plan.dayName,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = NeoBlack
                    )
                }
                if (plan.recipeName != null) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = NeoPink,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onClear() }
                    )
                }
            }

            // ── Konten: ada resep atau kosong ───────────────────────────────
            if (plan.recipeName != null) {
                // Nama resep + biaya
                Text(
                    text = plan.recipeName.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = NeoBlack
                )
                // Badge biaya — neo-brutalist style
                Box(
                    modifier = Modifier
                        .neoShadow(offsetX = 3.dp, offsetY = 3.dp, borderRadius = 4.dp)
                        .background(accentColor, shape = RoundedCornerShape(4.dp))
                        .border(2.dp, NeoBlack, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "BIAYA: " + currencyFormatter.format(plan.estimatedCost),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = NeoBlack
                    )
                }

                // Tombol lihat/tutup detail (hanya tampil kalau ada data)
                if (ingredients.isNotEmpty() || steps.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isExpanded) NeoBlack else Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.5.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                            .clickable { isExpanded = !isExpanded }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = if (isExpanded) Color.White else NeoBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isExpanded) "TUTUP DETAIL" else "LIHAT DETAIL RESEP",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isExpanded) Color.White else NeoBlack
                            )
                        }
                        Text(
                            text = "${ingredients.size} bahan  •  ${steps.size} langkah",
                            fontSize = 11.sp,
                            color = if (isExpanded) Color.White.copy(alpha = 0.7f) else Color.Gray
                        )
                    }
                }

                // ── Detail expanded ─────────────────────────────────────────
                if (isExpanded) {
                    HorizontalDivider(color = NeoBlack.copy(alpha = 0.1f))

                    // Bahan-bahan
                    if (ingredients.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = NeoBlack
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BAHAN-BAHAN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                            ingredients.forEach { (name, cost) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(accentColor, shape = CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = name, fontSize = 12.sp)
                                    }
                                    if (cost > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = currencyFormatter.format(cost),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoPrimary
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = NeoBlack.copy(alpha = 0.1f))
                    }

                    // Cara memasak
                    if (steps.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = NeoBlack
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CARA MEMASAK",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                            steps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(NeoBlack, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Text(
                                        text = step.trimStart { it.isDigit() || it == '.' || it == ' ' },
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                // Slot kosong
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Belum ada rencana. Buka tab MENU untuk generate resep AI.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
