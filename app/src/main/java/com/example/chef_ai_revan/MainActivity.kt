package com.example.chef_ai_revan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chef_ai_revan.ui.components.ApiKeySettingsDialog
import com.example.chef_ai_revan.ui.components.ApiKeyWelcomeDialog
import com.example.chef_ai_revan.ui.components.MasakinLogo
import com.example.chef_ai_revan.ui.components.MasakinSplashScreen
import com.example.chef_ai_revan.ui.components.NeoToastHost
import com.example.chef_ai_revan.ui.components.neoShadow
import com.example.chef_ai_revan.ui.navigation.Screen
import com.example.chef_ai_revan.ui.navigation.SetupNavGraph
import com.example.chef_ai_revan.ui.theme.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel

class MainActivity : ComponentActivity() {

    private val isAppReady = AtomicBoolean(false)

    private val budgetViewModel: BudgetViewModel by viewModels {
        BudgetViewModel.Factory((application as ChefAiApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppReady.get() }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                // Tutup overlay splash sistem (icon bulat) agar logo Compose tampil full
                isAppReady.set(true)
                delay(900)
                showSplash = false
            }

            if (showSplash) {
                MasakinSplashScreen()
            } else {
            ChefairevanTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MasakinTopBar(viewModel = budgetViewModel)
                    },
                    bottomBar = {
                        NeoBottomNavigation(navController)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                // Draw subtle background color (biru sangat muda)
                                drawRect(color = Color(0xFFEBF3FC))
                                
                                // Draw beautiful Neo-Brutalist polka-dot grid
                                val dotColor = Color(0xFF2E86C1).copy(alpha = 0.15f) // Sweet spot: tidak terlalu tebal
                                val dotRadius = 2.8.dp.toPx() // Ukuran pas: 2.8.dp
                                val dotSpacing = 20.dp.toPx()
                                
                                var x = 10.dp.toPx()
                                while (x < size.width) {
                                    var y = 10.dp.toPx()
                                    while (y < size.height) {
                                        drawCircle(
                                            color = dotColor,
                                            radius = dotRadius,
                                            center = Offset(x, y)
                                        )
                                        y += dotSpacing
                                    }
                                    x += dotSpacing
                                }
                            }
                            .padding(innerPadding)
                    ) {
                        SetupNavGraph(
                            navController = navController,
                            budgetViewModel = budgetViewModel
                        )
                    }
                        NeoToastHost(
                            modifier = Modifier.padding(
                                top = innerPadding.calculateTopPadding() + 8.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        )
                        val showApiWelcome by budgetViewModel.showApiKeyWelcomeDialog.collectAsStateWithLifecycle()
                        if (showApiWelcome) {
                            ApiKeyWelcomeDialog(
                                onOpenSettings = { budgetViewModel.dismissApiKeyWelcome(openSettings = true) },
                                onDismiss = { budgetViewModel.dismissApiKeyWelcome(openSettings = false) }
                            )
                        }
                        val showApiSettings by budgetViewModel.showApiSettingsDialog.collectAsStateWithLifecycle()
                        if (showApiSettings) {
                            ApiKeySettingsDialog(
                                viewModel = budgetViewModel,
                                onDismiss = { budgetViewModel.showApiSettingsDialog.value = false }
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun MasakinTopBar(viewModel: BudgetViewModel) {
    val isWarning by viewModel.isWeeklyBudgetWarning.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeoWhite)
            .drawBehind {
                drawLine(
                    color = NeoBlack,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 3.dp.toPx()
                )
            }
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MasakinLogo(height = 52.dp)
            if (isWarning) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(NeoPrimary, shape = CircleShape)
                )
            }
        }
        IconButton(
            onClick = { viewModel.showApiSettingsDialog.value = true },
            modifier = Modifier
                .size(44.dp)
                .neoShadow(offsetX = 3.dp, offsetY = 3.dp, borderRadius = 22.dp)
                .background(NeoYellow, shape = CircleShape)
                .border(2.dp, NeoBlack, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Pengaturan API Key",
                tint = NeoBlack,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NeoBottomNavigation(navController: NavHostController) {
    val items = listOf(
        NavigationItem(Screen.Dashboard.route, "MENU", Icons.Filled.Home),
        NavigationItem(Screen.Planner.route, "JADWAL", Icons.Filled.DateRange),
        NavigationItem(Screen.Budget.route, "DOMPET", Icons.Filled.Wallet),
        NavigationItem(Screen.Grocery.route, "BELANJA", Icons.Filled.List)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeoWhite)
            .drawBehind {
                drawLine(
                    color = NeoBlack,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3.dp.toPx()
                )
            }
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(if (isSelected) NeoYellow else NeoWhite)
                    .drawBehind {
                        drawLine(
                            color = NeoBlack,
                            start = Offset(size.width, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .clickable {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = NeoBlack
                    )
                    if (isSelected) {
                        Text(
                            text = item.label,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = NeoBlack
                        )
                    }
                }
            }
        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: ImageVector)
