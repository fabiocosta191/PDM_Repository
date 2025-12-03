package ipca.example.userlistapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavController,
    onLogout: () -> Unit = {},
    isOnboarding: Boolean = false, // <--- NOVO PARÂMETRO
    onSaveSuccess: () -> Unit = {} // <--- NOVO PARÂMETRO
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.uiState

    LaunchedEffect(state.isAccountDeleted) {
        if (state.isAccountDeleted) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOnboarding) "Complete o seu Perfil" else "O meu Perfil") },
                navigationIcon = {
                    // Só mostra o botão de voltar se NÃO for onboarding
                    if (!isOnboarding) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = {},
                        label = { Text("Email") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Passamos o callback onSaveSuccess para o ViewModel
                            viewModel.saveProfile(onSuccess = onSaveSuccess)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isOnboarding) "Guardar e Continuar" else "Guardar Alterações")
                    }

                    // Se for onboarding, não mostramos as opções de histórico/apagar conta para simplificar
                    if (!isOnboarding) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                if (state.userId.isNotEmpty()) {
                                    navController.navigate("history?userId=${state.userId}")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver o meu Histórico")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (state.error != null) {
                            Text(
                                text = state.error!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.deleteAccount() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Apagar Conta")
                        }
                    }
                }
            }
        }
    }
}