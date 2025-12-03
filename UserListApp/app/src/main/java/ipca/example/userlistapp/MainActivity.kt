package ipca.example.userlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.userlistapp.ui.UserDetailView
import ipca.example.userlistapp.ui.UserListScreen
import ipca.example.userlistapp.ui.history.HistoryView // Certifica-te que criaste o ficheiro neste package
import ipca.example.userlistapp.ui.login.LoginView
import ipca.example.userlistapp.ui.profile.ProfileView // Certifica-te que criaste o ficheiro neste package
import ipca.example.userlistapp.ui.theme.UserListAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserListAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    // Verifica se o utilizador já está logado ao iniciar
                    LaunchedEffect(Unit) {
                        val user = Firebase.auth.currentUser
                        if (user != null) {
                            navController.navigate("user_list") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginView(
                                onLoginSuccess = {
                                    // Login normal -> vai para a lista
                                    navController.navigate("user_list") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onRegisterSuccess = {
                                    // Registo -> vai para o Perfil em modo Onboarding
                                    navController.navigate("profile?onboarding=true") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ROTA 2: LISTA DE UTILIZADORES
                        composable("user_list") {
                            UserListScreen(
                                modifier = Modifier.fillMaxSize(),
                                onUserClick = { userId ->
                                    navController.navigate("user_detail/$userId")
                                },
                                onLogout = {
                                    Firebase.auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onHistoryClick = {
                                    // Navega sem parâmetros para mostrar o histórico global
                                    navController.navigate("history")
                                }
                            )
                        }

// Atualizar a rota do Profile para aceitar argumento
                        composable(
                            route = "profile?onboarding={onboarding}",
                            arguments = listOf(
                                navArgument("onboarding") {
                                    defaultValue = false
                                    type = NavType.BoolType
                                }
                            )
                        ) { backStackEntry ->
                            val isOnboarding = backStackEntry.arguments?.getBoolean("onboarding") ?: false

                            ProfileView(
                                navController = navController,
                                isOnboarding = isOnboarding,
                                onLogout = {
                                    navController.navigate("login") { popUpTo(0) }
                                },
                                onSaveSuccess = {
                                    // Se estivermos em modo onboarding, ao guardar vamos para a lista
                                    if (isOnboarding) {
                                        navController.navigate("user_list") {
                                            // Remove o ecrã de perfil da pilha para não voltar atrás
                                            popUpTo("profile?onboarding=true") { inclusive = true }
                                        }
                                    } else {
                                        // Se for edição normal, pode mostrar um Toast ou voltar atrás
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        // ROTA 4: DETALHE DO UTILIZADOR
                        composable(
                            route = "user_detail/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")

                            UserDetailView(
                                userId = userId,
                                navController = navController,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // ROTA 5: HISTÓRICO (GLOBAL OU PESSOAL)
                        composable(
                            route = "history?userId={userId}",
                            arguments = listOf(
                                navArgument("userId") {
                                    nullable = true
                                    defaultValue = null
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            HistoryView(navController = navController, userId = userId)
                        }
                    }
                }
            }
        }
    }
}