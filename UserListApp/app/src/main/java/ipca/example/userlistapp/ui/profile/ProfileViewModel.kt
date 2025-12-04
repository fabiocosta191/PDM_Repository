package ipca.example.userlistapp.ui.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.userlistapp.models.AuditLog
import javax.inject.Inject

data class ProfileState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAccountDeleted: Boolean = false,
    val passwordChangeSuccess: Boolean = false // <--- NOVO CAMPO
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

    // ... (loadUserProfile mantém-se igual) ...
    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user == null) {
            uiState.value = uiState.value.copy(error = "Utilizador não autenticado")
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            email = user.email ?: "",
            userId = user.uid
        )

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

    // ... (onNameChange, onPhoneChange mantêm-se iguais) ...
    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onPhoneChange(newValue: String) {
        uiState.value = uiState.value.copy(phone = newValue)
    }

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

    // ... (saveProfile e deleteAccount mantêm-se iguais) ...
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
                onSuccess()
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
    }

    fun deleteAccount() {
        val user = auth.currentUser ?: return
        val userId = user.uid
        val userEmail = user.email ?: ""

        uiState.value = uiState.value.copy(isLoading = true)
        logAction(userId, userEmail, "REMOCAO_CONTA")

        firestore.collection("users").document(userId).delete()
            .addOnSuccessListener {
                user.delete()
                    .addOnSuccessListener {
                        uiState.value = uiState.value.copy(isLoading = false, isAccountDeleted = true)
                    }
                    .addOnFailureListener { e ->
                        uiState.value = uiState.value.copy(isLoading = false, error = "Erro auth: ${e.localizedMessage}")
                    }
            }
            .addOnFailureListener { e ->
                uiState.value = uiState.value.copy(isLoading = false, error = "Erro dados: ${e.localizedMessage}")
            }
    }

    // --- NOVA FUNÇÃO: ALTERAR PASSWORD ---
    fun changePassword(newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            // Validar password simples
            if (newPassword.length < 6) {
                uiState.value = uiState.value.copy(error = "A password deve ter pelo menos 6 caracteres")
                return
            }

            uiState.value = uiState.value.copy(isLoading = true, error = null, passwordChangeSuccess = false)

            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    uiState.value = uiState.value.copy(isLoading = false)
                    if (task.isSuccessful) {
                        uiState.value = uiState.value.copy(
                            passwordChangeSuccess = true,
                            error = null
                        )
                        logAction(user.uid, user.email ?: "", "ALTERACAO_PASSWORD")
                    } else {
                        // Se falhar (ex: requer login recente), mostra o erro
                        uiState.value = uiState.value.copy(
                            error = task.exception?.localizedMessage ?: "Erro ao alterar password"
                        )
                    }
                }
        }
    }

    // Função para resetar o status de sucesso (usado pela View)
    fun resetPasswordChangeStatus() {
        uiState.value = uiState.value.copy(passwordChangeSuccess = false)
    }
}