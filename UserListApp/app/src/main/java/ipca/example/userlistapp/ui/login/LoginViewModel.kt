package ipca.example.userlistapp.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class LoginState(
    var email: String = "",
    var password: String = "",
    var error: String? = null,
    var isLoading: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    fun onEmailChange(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(password = password)
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
}