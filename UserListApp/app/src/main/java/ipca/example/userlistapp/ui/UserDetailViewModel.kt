package ipca.example.userlistapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ipca.example.userlistapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

// Define o estado da UI para os detalhes do utilizador
data class UserDetailState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserDetailViewModel : ViewModel() {

    var uiState = mutableStateOf(UserDetailState())
        private set

    private val client = OkHttpClient()

    fun fetchUser(userId: String) {
        // Limpamos o erro ao iniciar um novo carregamento
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val request = Request.Builder()
            .url("https://dummyjson.com/users/$userId")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Voltar à Main thread para atualizar a UI
                viewModelScope.launch(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = "Falha ao carregar dados: ${e.message}"
                    )
                }
            }

            override fun onResponse(call: Call, response: Response) {
                viewModelScope.launch(Dispatchers.Main) {
                    response.use {
                        if (!response.isSuccessful) {
                            uiState.value = uiState.value.copy(
                                isLoading = false,
                                error = "Erro no servidor: ${response.code}"
                            )
                            return@launch
                        }

                        try {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val jsonObject = JSONObject(responseBody)
                                val user = User.fromJson(jsonObject)
                                uiState.value = uiState.value.copy(
                                    isLoading = false,
                                    user = user,
                                    error = null
                                )
                            } else {
                                uiState.value = uiState.value.copy(
                                    isLoading = false,
                                    error = "Resposta vazia do servidor."
                                )
                            }
                        } catch (e: Exception) {
                            uiState.value = uiState.value.copy(
                                isLoading = false,
                                error = "Erro ao processar dados: ${e.message}"
                            )
                        }
                    }
                }
            }
        })
    }
}