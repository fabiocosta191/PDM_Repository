package ipca.example.userlistapp.ui.history

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.userlistapp.models.AuditLog
import javax.inject.Inject

data class HistoryState(
    val logs: List<AuditLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var uiState = mutableStateOf(HistoryState())
        private set

    fun fetchHistory(userId: String? = null) {
        uiState.value = uiState.value.copy(isLoading = true)

        // Pedimos sempre a lista ordenada por data
        val query = firestore.collection("audit_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query.get()
            .addOnSuccessListener { documents ->
                var logs = documents.map { doc ->
                    val data = doc.data
                    AuditLog(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        action = data["action"] as? String ?: "",
                        timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                        details = data["details"] as? String ?: ""
                    )
                }

                // --- CORREÇÃO: Filtragem feita aqui (Client-Side) ---
                // Isto evita o erro de "Index Required" do Firestore
                if (userId != null) {
                    logs = logs.filter { it.userId == userId }
                }
                // ----------------------------------------------------

                uiState.value = uiState.value.copy(isLoading = false, logs = logs)
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
    }
}