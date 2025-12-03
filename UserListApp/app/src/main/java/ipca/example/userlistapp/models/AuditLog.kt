package ipca.example.userlistapp.models

import com.google.firebase.Timestamp

data class AuditLog(
    val id: String = "",
    val userId: String = "",
    val email: String = "", // Útil para identificar visualmente sem fazer outra query
    val action: String = "", // Ex: "LOGIN", "REGISTO", "EDITAR", "APAGAR"
    val timestamp: Timestamp = Timestamp.now(),
    val details: String = ""
)