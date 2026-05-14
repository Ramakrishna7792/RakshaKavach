package com.safety.rakshakavach.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safety.rakshakavach.ui.screens.*
import com.safety.rakshakavach.viewmodel.MainViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onLoginSuccess = { 
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onRegisterClick = { 
                    viewModel.resetVerificationState()
                    navController.navigate("register") 
                },
                onForgotPasswordClick = { 
                    viewModel.resetVerificationState()
                    navController.navigate("forgot_password") 
                },
                viewModel = viewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { workerId -> 
                    navController.navigate("registration_success/$workerId")
                },
                onBackToLogin = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable(
            route = "registration_success/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
            RegistrationSuccessScreen(workerId = workerId) { 
                navController.navigate("auth") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onVerifySuccess = { workerId -> 
                    navController.navigate("reset_password/$workerId")
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable(
            route = "reset_password/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
            ResetPasswordScreen(
                workerId = workerId,
                onResetSuccess = { 
                    navController.navigate("auth") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("home") { HomeScreen(navController, viewModel = viewModel) }
        composable("incidents") { 
            IncidentLogScreen(
                onBack = { navController.popBackStack() }, 
                onNavigateToReports = { navController.navigate("recent_reports") },
                viewModel = viewModel
            ) 
        }
        composable("recent_reports") {
            RecentReportsScreen(onBack = { navController.popBackStack() }, viewModel = viewModel)
        }
        composable("task_selection") { 
            TaskSelectionScreen(
                onTaskSelected = { task -> navController.navigate("checklist/$task") },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable("profile") { ProfileScreen(onBack = { navController.popBackStack() }, viewModel = viewModel) }
        composable("quiz") { QuizScreen(onBack = { navController.popBackStack() }, viewModel = viewModel) }
        composable("leaderboard") { LeaderboardScreen(onBack = { navController.popBackStack() }, viewModel = viewModel) }
        composable("risk_meter") { RiskMeterScreen(onBack = { navController.popBackStack() }, viewModel = viewModel) }
        composable("checklist/{taskType}") { backStackEntry ->
            val taskType = backStackEntry.arguments?.getString("taskType") ?: "Welding"
            ChecklistScreen(
                taskType = taskType, 
                onBack = { navController.popBackStack() },
                onStartWork = { 
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
