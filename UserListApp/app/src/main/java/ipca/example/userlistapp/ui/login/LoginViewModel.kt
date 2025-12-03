package ipca.example.userlistapp.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.userlistapp.models.AuditLog
import javax.inject.Inject

data class LoginState(
    var email: String = "",
    var password: String = "",
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    fun onEmailChange(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    // Função auxiliar para gravar o histórico
    private fun logAction(userId: String, email: String, action: String) {
        val log = AuditLog(
            userId = userId,
            email = email,
            action = action,
            timestamp = Timestamp.now()
        )
        // Guardar na coleção "audit_logs"
        firestore.collection("audit_logs").add(log)
    }

    fun login(onSuccess: () -> Unit) {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            uiState.value = uiState.value.copy(error = "Preencha todos os campos")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Regista o Login no histórico
                        logAction(user.uid, user.email ?: email, "LOGIN")
                    }
                    uiState.value = uiState.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.localizedMessage ?: "Erro no login"
                    )
                }
            }
    }

    fun register(onSuccess: () -> Unit) {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            uiState.value = uiState.value.copy(error = "Preencha todos os campos")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // 1. Cria o documento do utilizador na coleção "users"
                        val userData = hashMapOf(
                            "email" to email,
                            "name" to email.substringBefore("@")
                        )
                        firestore.collection("users").document(user.uid).set(userData)

                        // 2. Regista a Criação de Conta no histórico
                        logAction(user.uid, email, "CRIACAO_CONTA")
                    }

                    uiState.value = uiState.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.localizedMessage ?: "Erro ao criar conta"
                    )
                }
            }
    }
}