package ipca.example.userlistapp.ui.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.userlistapp.models.AuditLog // Importar o modelo AuditLog
import javax.inject.Inject

data class ProfileState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAccountDeleted: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var uiState = mutableStateOf(ProfileState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user == null) {
            uiState.value = uiState.value.copy(error = "Utilizador não autenticado")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, email = user.email ?: "",userId = user.uid)

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val phone = document.getString("phone") ?: ""
                    uiState.value = uiState.value.copy(
                        name = name,
                        phone = phone,
                        isLoading = false
                    )
                } else {
                    uiState.value = uiState.value.copy(isLoading = false)
                }
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
    }

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onPhoneChange(newValue: String) {
        uiState.value = uiState.value.copy(phone = newValue)
    }

    // Função auxiliar atualizada para usar AuditLog
    private fun logAction(userId: String, email: String, action: String, details: String = "") {
        val log = AuditLog(
            userId = userId,
            email = email,
            action = action,
            details = details,
            timestamp = Timestamp.now()
        )
        firestore.collection("audit_logs").add(log)
    }

    // Adicionar o parâmetro onSuccess
    fun saveProfile(onSuccess: () -> Unit = {}) {
        val user = auth.currentUser ?: return
        uiState.value = uiState.value.copy(isLoading = true)

        val userData = hashMapOf(
            "name" to uiState.value.name,
            "phone" to uiState.value.phone,
            "email" to uiState.value.email
        )

        firestore.collection("users").document(user.uid).set(userData)
            .addOnSuccessListener {
                logAction(user.uid, uiState.value.email, "EDICAO_DADOS", "Nome: ${uiState.value.name}")
                uiState.value = uiState.value.copy(isLoading = false, error = null)
                Log.d("ProfileViewModel", "Dados guardados com sucesso")

                // Chamar o callback de sucesso
                onSuccess()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
    }

    fun deleteAccount() {
        val user = auth.currentUser ?: return

        // CORREÇÃO: Definir as variáveis antes de usar
        val userId = user.uid
        val userEmail = user.email ?: ""

        uiState.value = uiState.value.copy(isLoading = true)

        // Registar log ANTES de apagar
        logAction(userId, userEmail, "REMOCAO_CONTA")

        // 1. Apagar dados do Firestore
        firestore.collection("users").document(userId).delete()
            .addOnSuccessListener {
                // 2. Apagar utilizador do Authentication
                user.delete()
                    .addOnSuccessListener {
                        uiState.value = uiState.value.copy(isLoading = false, isAccountDeleted = true)
                    }
                    .addOnFailureListener { e ->
                        uiState.value = uiState.value.copy(isLoading = false, error = "Erro ao apagar conta: ${e.localizedMessage}. Tente fazer login novamente.")
                    }
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = "Erro ao apagar dados: ${e.localizedMessage}")
            }
    }
}