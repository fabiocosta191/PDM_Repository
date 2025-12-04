package ipca.example.userlistapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // Importante
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.uiState

    // Estados para controlar visibilidade da password e diálogos
    var isPasswordVisible by remember { mutableStateOf(false) } // Login
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // --- DIÁLOGO DE RECUPERAÇÃO (MANTÉM-SE IGUAL) ---
    if (showResetDialog) {
        var resetEmail by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showResetDialog = false; viewModel.clearErrors() },
            title = { Text("Recuperar Password") },
            text = {
                Column {
                    Text("Insira o seu email:")
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.resetPasswordMessage != null) {
                        Text(text = state.resetPasswordMessage!!, color = Color(0xFF006400))
                    }
                    if (state.error != null) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.resetPassword(resetEmail) }) { Text("Enviar") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false; viewModel.clearErrors() }) { Text("Fechar") }
            }
        )
    }

    // --- DIÁLOGO DE CRIAÇÃO DE CONTA (COM VISIBILIDADE) ---
    if (showRegisterDialog) {
        var regEmail by remember { mutableStateOf("") }
        var regPassword by remember { mutableStateOf("") }
        var isRegPasswordVisible by remember { mutableStateOf(false) } // Estado local para este diálogo

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false; viewModel.clearErrors() },
            title = { Text("Criar Nova Conta") },
            text = {
                Column {
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Password") },
                        // Lógica de Visibilidade
                        visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                Icon(
                                    imageVector = if (isRegPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.error != null) {
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.register(regEmail, regPassword) {
                        showRegisterDialog = false
                        onRegisterSuccess()
                    }
                }) { Text("Criar Conta") }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false; viewModel.clearErrors() }) { Text("Cancelar") }
            }
        )
    }

    // --- ECRÃ PRINCIPAL DE LOGIN (COM VISIBILIDADE) ---
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "User List App",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            TextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                // Lógica de Visibilidade
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = { showResetDialog = true; viewModel.clearErrors() }) {
                    Text("Esqueci-me da password")
                }
            }

            if (state.error != null && !showRegisterDialog && !showResetDialog) {
                Text(
                    text = state.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { viewModel.login(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Ainda não tem conta?")

            OutlinedButton(
                onClick = { showRegisterDialog = true; viewModel.clearErrors() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Conta Nova")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    LoginView()
}