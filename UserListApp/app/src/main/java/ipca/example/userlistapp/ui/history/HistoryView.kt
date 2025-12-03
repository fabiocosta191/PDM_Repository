package ipca.example.userlistapp.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryView(
    navController: NavController,
    userId: String? = null // Se null, mostra tudo
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val state by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchHistory(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userId == null) "Histórico Global" else "Histórico do Utilizador") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                items(state.logs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val dateStr = dateFormat.format(log.timestamp.toDate())

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(text = log.action, style = MaterialTheme.typography.titleMedium)
                                Text(text = dateStr, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(text = "User: ${log.email}", style = MaterialTheme.typography.bodyMedium)
                            if (log.details.isNotEmpty()) {
                                Text(text = "Detalhes: ${log.details}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}