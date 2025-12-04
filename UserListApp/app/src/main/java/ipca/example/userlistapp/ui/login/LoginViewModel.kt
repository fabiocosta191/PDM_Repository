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
    var resetPasswordMessage: String? = null,
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
        uiState.value = uiState.value.copy(email = email, error = null, resetPasswordMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(password = password, error = null)
    }

    private fun logAction(userId: String, email: String, action: String) {
        val log = AuditLog(
            userId = userId,
            email = email,
            action = action,
            timestamp = Timestamp.now()
        )
        firestore.collection("audit_logs").add(log)
    }

    // --- ATUALIZADO: Aceita email como parâmetro ---
    fun resetPassword(emailInput: String) {
        if (emailInput.isBlank()) {
            uiState.value = uiState.value.copy(error = "Escreva o seu email para recuperar a password")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null, resetPasswordMessage = null)

        auth.sendPasswordResetEmail(emailInput)
            .addOnCompleteListener { task ->
                uiState.value = uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    uiState.value = uiState.value.copy(
                        resetPasswordMessage = "Email enviado para $emailInput! Verifique a sua caixa de entrada."
                    )
                } else {
                    uiState.value = uiState.value.copy(
                        error = task.exception?.localizedMessage ?: "Erro ao enviar email"
                    )
                }
            }
    }

    // Login mantém-se igual (lê do estado principal)
    fun login(onSuccess: () -> Unit) {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            uiState.value = uiState.value.copy(error = "Preencha todos os campos")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null, resetPasswordMessage = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
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

    // --- ATUALIZADO: Aceita email e password como parâmetros ---
    fun register(emailInput: String, passwordInput: String, onSuccess: () -> Unit) {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            uiState.value = uiState.value.copy(error = "Preencha todos os campos de registo")
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null, resetPasswordMessage = null)

        auth.createUserWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "email" to emailInput,
                            "name" to emailInput.substringBefore("@")
                        )
                        firestore.collection("users").document(user.uid).set(userData)
                        logAction(user.uid, emailInput, "CRIACAO_CONTA")
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

    // Função helper para limpar mensagens ao fechar dialogs
    fun clearErrors() {
        uiState.value = uiState.value.copy(error = null, resetPasswordMessage = null)
    }
}