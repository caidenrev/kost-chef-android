package com.example.chef_ai_revan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chef_ai_revan.ui.screens.*
import com.example.chef_ai_revan.viewmodel.BudgetViewModel

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Menu")
    object Planner : Screen("planner", "Jadwal")
    object Budget : Screen("budget", "Dompet")
    object Grocery : Screen("grocery", "Belanja")
}

@Composable
fun SetupNavGraph(navController: NavHostController, budgetViewModel: BudgetViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = budgetViewModel)
        }
        composable(Screen.Planner.route) {
            PlannerScreen(viewModel = budgetViewModel)
        }
        composable(Screen.Budget.route) {
            BudgetScreen(viewModel = budgetViewModel)
        }
        composable(Screen.Grocery.route) {
            GroceryScreen(viewModel = budgetViewModel)
        }
    }
}
