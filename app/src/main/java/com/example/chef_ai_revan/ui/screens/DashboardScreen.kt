package com.example.chef_ai_revan.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chef_ai_revan.data.entity.FavoriteRecipe
import com.example.chef_ai_revan.ui.components.*
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel
import com.example.chef_ai_revan.viewmodel.GeneratedRecipeMock
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: BudgetViewModel) {
    val context = LocalContext.current
    val budget by viewModel.budget.collectAsStateWithLifecycle()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationProgress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val generatedRecipes by viewModel.generatedRecipes.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("GENERATOR") } // GENERATOR or WISHLIST

    // UI Configuration Inputs
    var selectedIngredients by remember { mutableStateOf(setOf<String>()) }
    var inputBudgetLimit by remember { mutableStateOf(20000f) }

    // Dialog State
    var showDayPickerDialog by remember { mutableStateOf(false) }
    var selectedRecipeForDay by remember { mutableStateOf<GeneratedRecipeMock?>(null) }

    val localeID = Locale("in", "ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)
    currencyFormatter.maximumFractionDigits = 0

    val availableIngredients = listOf("Telur", "Tempe", "Tahu", "Mie Instan", "Sarden", "Kangkung", "Ayam", "Beras")
    val colors = listOf(NeoGreen, NeoPink, NeoCyan, NeoYellow, NeoPurple)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp)
    ) {
        // App branding banner
        item {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoPurple
            ) {
                Column {
                    Text(
                        text = "MAKAN ENAK AKHIR BULAN!",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sesuaikan budget kantong anak kost & racik resep kreasi mandiri dibantu AI cerdas.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sub Navigation Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeoButton(
                    text = "AI GENERATOR",
                    onClick = { activeSubTab = "GENERATOR" },
                    backgroundColor = if (activeSubTab == "GENERATOR") NeoYellow else NeoWhite,
                    modifier = Modifier.weight(1f)
                )
                NeoButton(
                    text = "WISHLIST",
                    onClick = { activeSubTab = "WISHLIST" },
                    backgroundColor = if (activeSubTab == "WISHLIST") NeoPink else NeoWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (activeSubTab == "GENERATOR") {
            // Ingredient select
            item {
                NeoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "PILIH BAHAN DI WARUNG/KOST:",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableIngredients.forEach { ingredient ->
                                val isSelected = selectedIngredients.contains(ingredient)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) NeoBlack else NeoWhite,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .border(2.dp, NeoBlack, shape = RoundedCornerShape(20.dp))
                                        .clickable {
                                            selectedIngredients = if (isSelected) {
                                                selectedIngredients - ingredient
                                            } else {
                                                selectedIngredients + ingredient
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = ingredient,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (isSelected) NeoWhite else NeoBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Budget selection slider
            item {
                NeoCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIMIT HARGA HARIAN:",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = currencyFormatter.format(inputBudgetLimit.toDouble()),
                                fontWeight = FontWeight.Black,
                                color = NeoPrimary,
                                fontSize = 16.sp
                            )
                        }
                        Slider(
                            value = inputBudgetLimit,
                            onValueChange = { inputBudgetLimit = it },
                            valueRange = 5000f..30000f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = NeoBlack,
                                activeTrackColor = NeoBlack,
                                inactiveTrackColor = Color.LightGray
                            )
                        )
                    }
                }
            }

            // Trigger AI Generation Button
            item {
                NeoButton(
                    text = "GENERASI RESEP AI",
                    onClick = {
                        viewModel.generateRecipesWithAI(selectedIngredients.toList(), inputBudgetLimit.toDouble())
                    },
                    backgroundColor = NeoGreen,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Terminal style loader or AI Recipe Results
            if (isGenerating) {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeoBlack
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(color = NeoCyan)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TERMINAL DOKUMEN GEMINI AI:",
                                color = NeoGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "> $generationProgress",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else if (generatedRecipes.isNotEmpty()) {
                item {
                    Text(
                        text = "HASIL REKOMENDASI AI RESEP:",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                itemsIndexed(generatedRecipes) { index, recipe ->
                    val isFavorite = favoriteRecipes.any { it.name == recipe.name }
                    RecipeItemCard(
                        recipe = recipe,
                        accentColor = colors[index % colors.size],
                        isFavorite = isFavorite,
                        currencyFormatter = currencyFormatter,
                        onFavoriteToggle = {
                            if (isFavorite) {
                                viewModel.removeRecipeFromFavoritesByName(recipe.name)
                                Toast.makeText(context, "Dihapus dari Wishlist!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addRecipeToFavorites(recipe)
                                Toast.makeText(context, "Disimpan ke Wishlist!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onAddGrocery = {
                            recipe.ingredients.forEach { (name, cost) ->
                                viewModel.addGroceryItem(name, cost)
                            }
                            Toast.makeText(context, "Bahan dimasukkan ke daftar belanja!", Toast.LENGTH_SHORT).show()
                        },
                        onExportPdf = {
                            val ingredientsStrList = recipe.ingredients.map { "${it.first} (${currencyFormatter.format(it.second)})" }
                            PdfExporter.exportRecipePdf(
                                context = context,
                                title = recipe.name,
                                cost = currencyFormatter.format(recipe.estimatedCost),
                                description = recipe.description + "\n\nInstruksi:\n" + recipe.instructions,
                                ingredients = ingredientsStrList
                            )
                        },
                        onAddToPlanner = {
                            selectedRecipeForDay = recipe
                            showDayPickerDialog = true
                        }
                    )
                }
            } else {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.LightGray
                    ) {
                        Text(
                            text = "Belum ada resep tergenerasi. Tekan tombol GENERASI RESEP AI di atas untuk meracik ide masakan lezat!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // WISHLIST TAB
            if (favoriteRecipes.isEmpty()) {
                item {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.LightGray
                    ) {
                        Text(
                            text = "Daftar Wishlist kosong! Buka tab AI Generator dan tekan tanda Bintang di resep untuk menyimpannya di sini.",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "RESEP FAVORIT ANDA (${favoriteRecipes.size}):",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                items(favoriteRecipes) { fav ->
                    FavoriteItemCard(
                        favorite = fav,
                        currencyFormatter = currencyFormatter,
                        onDelete = {
                            viewModel.removeFavorite(fav)
                            Toast.makeText(context, "Dihapus dari Wishlist!", Toast.LENGTH_SHORT).show()
                        },
                        onAddGrocery = {
                            // Extract ingredient list string "Telur:2000,Tempe:3000"
                            val parts = fav.ingredientsList.split(",")
                            parts.forEach { part ->
                                val subparts = part.split(":")
                                if (subparts.size == 2) {
                                    val name = subparts[0]
                                    val cost = subparts[1].toDoubleOrNull() ?: 0.0
                                    viewModel.addGroceryItem(name, cost)
                                }
                            }
                            Toast.makeText(context, "Bahan dimasukkan ke daftar belanja!", Toast.LENGTH_SHORT).show()
                        },
                        onExportPdf = {
                            val parts = fav.ingredientsList.split(",")
                            val parsedIngredients = parts.map { part ->
                                val subparts = part.split(":")
                                if (subparts.size == 2) {
                                    "${subparts[0]} (${currencyFormatter.format(subparts[1].toDoubleOrNull() ?: 0.0)})"
                                } else {
                                    part
                                }
                            }
                            PdfExporter.exportRecipePdf(
                                context = context,
                                title = fav.name,
                                cost = currencyFormatter.format(fav.estimatedCost),
                                description = fav.description,
                                ingredients = parsedIngredients
                            )
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Day picker Dialog
    if (showDayPickerDialog && selectedRecipeForDay != null) {
        val days = listOf("SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "MINGGU")
        Dialog(onDismissRequest = { showDayPickerDialog = false }) {
            NeoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = NeoYellow
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PILIH HARI JADWAL",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tambahkan '${selectedRecipeForDay!!.name}' ke perencanaan jadwal makan mingguan Anda.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEachIndexed { index, day ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NeoWhite, shape = RoundedCornerShape(8.dp))
                                    .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.addRecipeToWeeklyPlan(
                                            dayIndex = index,
                                            recipeName = selectedRecipeForDay!!.name,
                                            cost = selectedRecipeForDay!!.estimatedCost,
                                            description = selectedRecipeForDay!!.description
                                        )
                                        showDayPickerDialog = false
                                        selectedRecipeForDay = null
                                        Toast.makeText(context, "Berhasil masuk ke jadwal $day!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = day, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }

                    NeoButton(
                        text = "BATAL",
                        onClick = {
                            showDayPickerDialog = false
                            selectedRecipeForDay = null
                        },
                        backgroundColor = NeoPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeItemCard(
    recipe: GeneratedRecipeMock,
    accentColor: Color,
    isFavorite: Boolean,
    currencyFormatter: NumberFormat,
    onFavoriteToggle: () -> Unit,
    onAddGrocery: () -> Unit,
    onExportPdf: () -> Unit,
    onAddToPlanner: () -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Title + favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Simpan",
                        tint = if (isFavorite) NeoPink else NeoBlack,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Price badge
            Row(
                modifier = Modifier
                    .background(accentColor, shape = RoundedCornerShape(8.dp))
                    .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ESTIMASI: " + currencyFormatter.format(recipe.estimatedCost),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeoBlack
                )
            }

            // Description
            Text(
                text = recipe.description,
                fontSize = 13.sp
            )

            // Ingredients
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "BAHAN-BAHAN:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                recipe.ingredients.forEach { (name, cost) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• $name", fontSize = 12.sp)
                        Text(text = currencyFormatter.format(cost), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Instructions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "CARA MASAK:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = recipe.instructions, fontSize = 12.sp)
            }

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeoButton(
                    text = "JADWAL",
                    onClick = onAddToPlanner,
                    backgroundColor = NeoCyan,
                    modifier = Modifier.weight(1f),
                    borderRadius = 8.dp
                )
                NeoButton(
                    text = "BELANJA",
                    onClick = onAddGrocery,
                    backgroundColor = NeoYellow,
                    modifier = Modifier.weight(1f),
                    borderRadius = 8.dp
                )
                NeoButton(
                    text = "PDF",
                    onClick = onExportPdf,
                    backgroundColor = NeoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.width(80.dp),
                    borderRadius = 8.dp
                )
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    favorite: FavoriteRecipe,
    currencyFormatter: NumberFormat,
    onDelete: () -> Unit,
    onAddGrocery: () -> Unit,
    onExportPdf: () -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = favorite.name.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = NeoPink,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )
            }

            Text(
                text = "Biaya total: " + currencyFormatter.format(favorite.estimatedCost),
                fontWeight = FontWeight.Black,
                color = NeoPrimary,
                fontSize = 13.sp
            )

            Text(
                text = favorite.description,
                fontSize = 13.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeoButton(
                    text = "BELANJAAN",
                    onClick = onAddGrocery,
                    backgroundColor = NeoGreen,
                    modifier = Modifier.weight(1f),
                    borderRadius = 8.dp
                )
                NeoButton(
                    text = "EKSPOR PDF",
                    onClick = onExportPdf,
                    backgroundColor = NeoBlack,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    borderRadius = 8.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
