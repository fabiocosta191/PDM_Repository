package ipca.example.userlistapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.userlistapp.models.User
import ipca.example.userlistapp.models.UserDao
import ipca.example.userlistapp.models.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

data class UserListState(
    val allUsers: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val roles: List<String> = listOf("All User", "user", "admin", "moderator"),
    val selectedRole: String = "All User"
)

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    var uiState = mutableStateOf(UserListState())
        private set

    private val client = OkHttpClient()

    fun fetchUsers(forceNetwork: Boolean = false) {
        if (uiState.value.isLoading) return

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            if (!forceNetwork) {
                val localUsers = userDao.getAll()
                if (localUsers.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            allUsers = localUsers,
                            filteredUsers = filterList(localUsers, uiState.value.selectedRole),
                            error = null
                        )
                    }
                    return@launch
                }
            }
            fetchFromNetwork()
        }
    }

    private fun fetchFromNetwork() {
        val request = Request.Builder()
            .url("https://dummyjson.com/users")
            .build()

        client.newCall(request).enqueue(object : Callback {

            // Se falhar a conexão (ex: sem internet)
            override fun onFailure(call: Call, e: IOException) {
                fallbackToLocalData("Sem conexão à internet.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Se o servidor responder com erro (ex: 404, 500)
                        fallbackToLocalData("Erro do servidor: ${response.code}")
                        return
                    }

                    try {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonObject = JSONObject(responseBody)
                            val userResponse = UserResponse.fromJson(jsonObject)

                            // Sucesso! Salva na BD e atualiza a UI
                            userDao.insertAll(*userResponse.users.toTypedArray())

                            viewModelScope.launch(Dispatchers.Main) {
                                uiState.value = uiState.value.copy(
                                    isLoading = false,
                                    allUsers = userResponse.users,
                                    filteredUsers = filterList(userResponse.users, uiState.value.selectedRole),
                                    error = null
                                )
                            }
                        } else {
                            fallbackToLocalData("Resposta vazia do servidor.")
                        }
                    } catch (e: Exception) {
                        fallbackToLocalData("Erro ao processar dados: ${e.message}")
                    }
                }
            }
        })
    }

    // --- NOVA FUNÇÃO DE SEGURANÇA ---
    // Tenta carregar dados locais se a rede falhar.
    // Só mostra erro se a base de dados também estiver vazia.
    private fun fallbackToLocalData(errorMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val localUsers = userDao.getAll()

            withContext(Dispatchers.Main) {
                if (localUsers.isNotEmpty()) {
                    // Temos dados antigos! Mostramos os dados em vez do erro.
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        allUsers = localUsers,
                        filteredUsers = filterList(localUsers, uiState.value.selectedRole),
                        error = null // Importante: Limpar o erro para a lista aparecer
                    )
                    // Opcional: Aqui podias adicionar uma flag para mostrar um "Toast" a dizer que está offline
                } else {
                    // Não há dados nenhuns (nem locais, nem rede). Mostramos o erro.
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

    fun filterByRole(role: String) {
        val filtered = filterList(uiState.value.allUsers, role)
        uiState.value = uiState.value.copy(
            selectedRole = role,
            filteredUsers = filtered
        )
    }

    private fun filterList(users: List<User>, role: String): List<User> {
        return if (role == "All User") users else users.filter { it.role == role }
    }
}