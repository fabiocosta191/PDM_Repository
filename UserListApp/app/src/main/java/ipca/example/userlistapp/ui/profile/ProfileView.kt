package ipca.example.userlistapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importar remember e mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavController,
    onLogout: () -> Unit = {},
    isOnboarding: Boolean = false,
    onSaveSuccess: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.uiState
    val context = LocalContext.current

    // Estado para controlar o Diálogo de Password
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPasswordInput by remember { mutableStateOf("") }
    var isNewPasswordVisible by remember { mutableStateOf(false) } // <--- Novo Estado

    // Efeito para mostrar mensagem de sucesso
    LaunchedEffect(state.passwordChangeSuccess) {
        if (state.passwordChangeSuccess) {
            Toast.makeText(context, "Password alterada com sucesso!", Toast.LENGTH_SHORT).show()
            showPasswordDialog = false // Fecha o diálogo
            newPasswordInput = "" // Limpa o campo
            viewModel.resetPasswordChangeStatus()
        }
    }

    LaunchedEffect(state.isAccountDeleted) {
        if (state.isAccountDeleted) {
            onLogout()
        }
    }

    // --- DIÁLOGO PARA ALTERAR PASSWORD ---
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Alterar Password") },
            text = {
                Column {
                    Text("Insira a nova password:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("Nova Password") },
                        // Lógica de Visibilidade
                        visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                                Icon(
                                    imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password"
                                )
                            }
                        },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.changePassword(newPasswordInput) }
                ) {
                    Text("Alterar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    // -------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOnboarding) "Complete o seu Perfil" else "O meu Perfil") },
                navigationIcon = {
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
                        onClick = { viewModel.saveProfile(onSuccess = onSaveSuccess) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isOnboarding) "Guardar e Continuar" else "Guardar Alterações")
                    }

                    if (!isOnboarding) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // --- BOTÃO DE ALTERAR PASSWORD ---
                        OutlinedButton(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Alterar Password")
                        }
                        // ---------------------------------

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