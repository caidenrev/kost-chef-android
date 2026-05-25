package com.example.chef_ai_revan.ui.screens

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
import kotlinx.coroutines.delay

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

            // Loading card atau hasil resep
            if (isGenerating) {
                item {
                    LoadingRecipeCard(progress = generationProgress)
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
                                NeoToastState.show("Dihapus dari Wishlist!", NeoToastType.DELETE)
                            } else {
                                viewModel.addRecipeToFavorites(recipe)
                                NeoToastState.show("Disimpan ke Wishlist!", NeoToastType.SUCCESS)
                            }
                        },
                        onAddGrocery = {
                            recipe.ingredients.forEach { (name, cost) ->
                                viewModel.addGroceryItem(name, cost)
                            }
                            NeoToastState.show("Bahan dimasukkan ke daftar belanja!", NeoToastType.SUCCESS)
                        },
                        onExportPdf = {
                            val ingredientsStrList = recipe.ingredients.map { "${it.first} (${currencyFormatter.format(it.second)})" }
                            PdfExporter.exportRecipePdf(
                                context = context,
                                title = recipe.name,
                                cost = currencyFormatter.format(recipe.estimatedCost),
                                description = recipe.description + "\n\nCara Memasak:\n" + recipe.steps.mapIndexed { i, s -> "${i+1}. $s" }.joinToString("\n"),
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
                            NeoToastState.show("Dihapus dari Wishlist!", NeoToastType.DELETE)
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
                            NeoToastState.show("Bahan dimasukkan ke daftar belanja!", NeoToastType.SUCCESS)
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
                                            recipe = selectedRecipeForDay!!
                                        )
                                        showDayPickerDialog = false
                                        selectedRecipeForDay = null
                                        NeoToastState.show("Berhasil masuk ke jadwal $day!", NeoToastType.SUCCESS)
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
    var isExpanded by remember { mutableStateOf(false) }

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // ── Header: nama + favorit ──────────────────────────────────────
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
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // ── Harga + deskripsi singkat ───────────────────────────────────
            Row(
                modifier = Modifier
                    .background(accentColor, shape = RoundedCornerShape(8.dp))
                    .border(2.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "ESTIMASI: " + currencyFormatter.format(recipe.estimatedCost),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeoBlack
                )
            }

            Text(
                text = recipe.description,
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            // ── Tombol lihat/tutup detail ───────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isExpanded) NeoBlack else Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(1.5.dp, NeoBlack, shape = RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (isExpanded) Color.White else NeoBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isExpanded) "TUTUP DETAIL" else "LIHAT BAHAN & CARA MASAK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isExpanded) Color.White else NeoBlack
                    )
                }
                Text(
                    text = "${recipe.ingredients.size} bahan  •  ${recipe.steps.size} langkah",
                    fontSize = 11.sp,
                    color = if (isExpanded) Color.White.copy(alpha = 0.7f) else Color.Gray
                )
            }

            // ── Detail (collapsed/expanded) ─────────────────────────────────
            if (isExpanded) {
                HorizontalDivider(color = NeoBlack.copy(alpha = 0.1f))

                // Bahan-bahan
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = NeoBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "BAHAN-BAHAN", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    recipe.ingredients.forEach { (name, cost) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
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

                // Cara memasak
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = NeoBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "CARA MEMASAK", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    recipe.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(NeoBlack, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = step.trimStart { it.isDigit() || it == '.' || it == ' ' },
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                HorizontalDivider(color = NeoBlack.copy(alpha = 0.1f))
            }

            // ── Action buttons ──────────────────────────────────────────────
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
                    modifier = Modifier.width(72.dp),
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
                    text = "BELANJA",
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

@Composable
fun LoadingRecipeCard(progress: String) {
    val friendlyMessages = listOf(
        "Chef Revan sedang berpikir...",
        "Meracik resep terbaik untukmu...",
        "Memilih bumbu yang pas...",
        "Menghitung budget dengan cermat...",
        "Hampir selesai, sabar ya!"
    )
    var messageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1800)
            messageIndex = (messageIndex + 1) % friendlyMessages.size
        }
    }

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoPurple
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = NeoYellow,
                strokeWidth = 4.dp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeoYellow,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = friendlyMessages[messageIndex],
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
            if (progress.startsWith("ERROR")) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeoPink.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = progress,
                        color = Color.White,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
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
