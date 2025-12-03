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
import ipca.example.userlistapp.ui.UserListScreen // <--- IMPORTANTE: Importar o Screen e não apenas a View
import ipca.example.userlistapp.ui.login.LoginView
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
                            LoginView(onLoginSuccess = {
                                navController.navigate("user_list") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }

                        // --- AQUI ESTÁ A MUDANÇA PRINCIPAL ---
                        composable("user_list") {
                            // Usamos UserListScreen em vez de UserListView.
                            // A Screen encarrega-se de chamar o ViewModel.
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
                                }
                            )
                        }
                        // -------------------------------------

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
                    }
                }
            }
        }
    }
}