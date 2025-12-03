package ipca.example.userlistapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ipca.example.userlistapp.models.User
import ipca.example.userlistapp.ui.theme.UserListAppTheme

@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: UserListViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchUsers() // Carregamento inicial (normal)
    }

    UserListView(
        modifier = modifier,
        uiState = uiState,
        onUserClick = onUserClick,
        onLogout = onLogout,
        onRoleSelected = { role -> viewModel.filterByRole(role) },
        // Ação de Reload força a rede (true)
        onReload = { viewModel.fetchUsers(forceNetwork = true) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListView(
    modifier: Modifier = Modifier,
    uiState: UserListState,
    onUserClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onRoleSelected: (String) -> Unit = {},
    onReload: () -> Unit = {} // Novo parâmetro para o Reload
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Utilizadores") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // 1. Botão de Reload
                    IconButton(onClick = onReload) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recarregar"
                        )
                    }
                    // 2. Botão de Logout
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            val selectedTabIndex = uiState.roles.indexOf(uiState.selectedRole).coerceAtLeast(0)

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                edgePadding = 8.dp
            ) {
                uiState.roles.forEachIndexed { index, role ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onRoleSelected(role) },
                        text = { Text(text = role.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.error != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Erro desconhecido",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Botão extra de tentar novamente no centro do ecrã em caso de erro
                        androidx.compose.material3.Button(onClick = onReload) {
                            Text("Tentar Novamente")
                        }
                    }
                } else {
                    if (uiState.filteredUsers.isEmpty()) {
                        Text(
                            text = "Nenhum utilizador encontrado.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(uiState.filteredUsers) { _, user ->
                                UserViewCell(
                                    modifier = Modifier.clickable {
                                        onUserClick(user.id.toString())
                                    },
                                    user = user
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    UserListAppTheme {
        val fakeState = UserListState(
            isLoading = false,
            filteredUsers = listOf(
                User(
                    id = 1, firstName = "Fabio", lastName = "Teste", email = "fabio@example.com",
                    phone = "123", image = null, address = null, maidenName = null, age = 30,
                    gender = "male", username = "fabio", birthDate = null, bloodGroup = null,
                    height = null, weight = null, eyeColor = null, hair = null, ip = null,
                    macAddress = null, university = null, bank = null, company = null, ein = null,
                    ssn = null, userAgent = null, crypto = null, role = "admin"
                )
            )
        )
        UserListView(uiState = fakeState)
    }
}