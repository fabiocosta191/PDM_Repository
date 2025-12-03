package ipca.example.userlistapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import ipca.example.userlistapp.models.*
import ipca.example.userlistapp.ui.theme.UserListAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailView(
    userId: String?,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(userId) {
        if (userId != null && uiState.user == null) { // Carrega apenas se ainda não tiver user
            viewModel.fetchUser(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalhes do Utilizador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    // --- BOTÃO RELOAD NA BARRA DE TOPO ---
                    IconButton(onClick = {
                        if (userId != null) viewModel.fetchUser(userId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recarregar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.error!!,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // --- BOTÃO DE TENTAR NOVAMENTE NO CENTRO ---
                        Button(onClick = {
                            if (userId != null) viewModel.fetchUser(userId)
                        }) {
                            Text("Tentar Novamente")
                        }
                    }
                }

                uiState.user != null -> {
                    UserDetailContent(user = uiState.user!!)
                }

                else -> {
                    Text(
                        "ID do utilizador inválido.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun UserDetailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = "Foto de ${user.firstName}",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${user.firstName} ${user.lastName} (${user.maidenName})",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "@${user.username}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(title = "Informação Pessoal")
        InfoRow(label = "Email", value = user.email)
        InfoRow(label = "Telefone", value = user.phone)
        InfoRow(label = "Idade", value = user.age?.toString())
        InfoRow(label = "Género", value = user.gender)
        InfoRow(label = "Data Nasc.", value = user.birthDate)
        InfoRow(label = "Universidade", value = user.university)

        SectionTitle(title = "Informação Física")
        InfoRow(label = "Altura", value = user.height?.toString()?.plus(" cm"))
        InfoRow(label = "Peso", value = user.weight?.toString()?.plus(" kg"))
        InfoRow(label = "Grupo Sanguíneo", value = user.bloodGroup)
        InfoRow(label = "Cor dos Olhos", value = user.eyeColor)
        InfoRow(label = "Cabelo", value = "${user.hair?.color}, ${user.hair?.type}")

        SectionTitle(title = "Endereço")
        InfoRow(label = "Morada", value = user.address?.address)
        InfoRow(label = "Cidade", value = user.address?.city)
        InfoRow(label = "Estado", value = "${user.address?.state} (${user.address?.stateCode})")
        InfoRow(label = "País", value = user.address?.country)
        InfoRow(label = "Cód. Postal", value = user.address?.postalCode)
        InfoRow(label = "Coords.", value = "(${user.address?.coordinates?.lat}, ${user.address?.coordinates?.lng})")

        SectionTitle(title = "Empresa")
        InfoRow(label = "Nome", value = user.company?.name)
        InfoRow(label = "Cargo", value = user.company?.title)
        InfoRow(label = "Departamento", value = user.company?.department)
        InfoRow(label = "Morada (Empresa)", value = user.company?.address?.address)

        SectionTitle(title = "Informação Bancária")
        InfoRow(label = "Tipo Cartão", value = user.bank?.cardType)
        InfoRow(label = "Nº Cartão", value = user.bank?.cardNumber)
        InfoRow(label = "Validade", value = user.bank?.cardExpire)
        InfoRow(label = "Moeda", value = user.bank?.currency)
        InfoRow(label = "IBAN", value = user.bank?.iban)

        SectionTitle(title = "Outras Informações")
        InfoRow(label = "IP", value = user.ip)
        InfoRow(label = "MAC Address", value = user.macAddress)
        InfoRow(label = "EIN", value = user.ein)
        InfoRow(label = "SSN", value = user.ssn)
        InfoRow(label = "Role", value = user.role)
        InfoRow(label = "User Agent", value = user.userAgent)
    }
}

@Composable
fun SectionTitle(title: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    HorizontalDivider()
}

@Composable
fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank() || value.contains("null")) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }
}

@Preview(name = "Detalhes Carregados", showBackground = true)
@Composable
fun UserDetailContentPreview() {
    UserListAppTheme {
        UserDetailView(userId = "1", navController = rememberNavController())
    }
}