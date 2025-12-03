package ipca.example.userlistapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

// Classe para a resposta completa da API (para a lista)
data class UserResponse(
    val users: List<User>,
    val total: Int,
    val skip: Int,
    val limit: Int
) {
    companion object {
        fun fromJson(json: JSONObject): UserResponse {
            val usersList = mutableListOf<User>()
            val usersArray = json.getJSONArray("users")
            for (i in 0 until usersArray.length()) {
                usersList.add(User.fromJson(usersArray.getJSONObject(i)))
            }
            return UserResponse(
                users = usersList,
                total = json.getInt("total"),
                skip = json.getInt("skip"),
                limit = json.getInt("limit")
            )
        }
    }
}

// --- NOVOS MODELOS DE DADOS ---

data class Hair(
    val color: String?,
    val type: String?
) {
    companion object {
        fun fromJson(json: JSONObject): Hair {
            return Hair(
                color = json.optString("color"),
                type = json.optString("type")
            )
        }
    }
}

data class Coordinates(
    val lat: Double?,
    val lng: Double?
) {
    companion object {
        fun fromJson(json: JSONObject): Coordinates {
            return Coordinates(
                lat = json.optDouble("lat"),
                lng = json.optDouble("lng")
            )
        }
    }
}

data class Bank(
    val cardExpire: String?,
    val cardNumber: String?,
    val cardType: String?,
    val currency: String?,
    val iban: String?
) {
    companion object {
        fun fromJson(json: JSONObject): Bank {
            return Bank(
                cardExpire = json.optString("cardExpire"),
                cardNumber = json.optString("cardNumber"),
                cardType = json.optString("cardType"),
                currency = json.optString("currency"),
                iban = json.optString("iban")
            )
        }
    }
}

data class Company(
    val department: String?,
    val name: String?,
    val title: String?,
    val address: Address?
) {
    companion object {
        fun fromJson(json: JSONObject): Company {
            val addressJson = json.optJSONObject("address")
            return Company(
                department = json.optString("department"),
                name = json.optString("name"),
                title = json.optString("title"),
                address = if (addressJson != null) Address.fromJson(addressJson) else null
            )
        }
    }
}

data class Crypto(
    val coin: String?,
    val wallet: String?,
    val network: String?
) {
    companion object {
        fun fromJson(json: JSONObject): Crypto {
            return Crypto(
                coin = json.optString("coin"),
                wallet = json.optString("wallet"),
                network = json.optString("network")
            )
        }
    }
}

// --- MODELOS ATUALIZADOS ---

// Classe para o objeto Address (endereço) - ATUALIZADA
data class Address(
    val address: String?,
    val city: String?,
    val postalCode: String?,
    val state: String?,
    val stateCode: String?,
    val country: String?,
    val coordinates: Coordinates?
) {
    companion object {
        fun fromJson(json: JSONObject): Address {
            val coordinatesJson = json.optJSONObject("coordinates")
            return Address(
                address = json.optString("address"),
                city = json.optString("city"),
                postalCode = json.optString("postalCode"),
                state = json.optString("state"),
                stateCode = json.optString("stateCode"),
                country = json.optString("country"),
                coordinates = if (coordinatesJson != null) Coordinates.fromJson(coordinatesJson) else null
            )
        }
    }
}

// Classe para o objeto User (utilizador) - ATUALIZADA
@Entity(tableName = "users") // <-- Alteração 1: Definir como Entidade Room
data class User(
    // Campos antigos
    @PrimaryKey // <-- Alteração 2: Definir a Chave Primária
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val image: String?,
    val address: Address?,

    // Novos campos
    val maidenName: String?,
    val age: Int?,
    val gender: String?,
    val username: String?,
    val birthDate: String?,
    val bloodGroup: String?,
    val height: Double?,
    val weight: Double?,
    val eyeColor: String?,
    val hair: Hair?,
    val ip: String?,
    val macAddress: String?,
    val university: String?,
    val bank: Bank?,
    val company: Company?,
    val ein: String?,
    val ssn: String?,
    val userAgent: String?,
    val crypto: Crypto?,
    val role: String?
) {
    companion object {
        fun fromJson(json: JSONObject): User {
            val addressJson = json.optJSONObject("address")
            val hairJson = json.optJSONObject("hair")
            val bankJson = json.optJSONObject("bank")
            val companyJson = json.optJSONObject("company")
            val cryptoJson = json.optJSONObject("crypto")

            return User(
                // Campos antigos
                id = json.getInt("id"),
                firstName = json.optString("firstName"),
                lastName = json.optString("lastName"),
                email = json.optString("email"),
                phone = json.optString("phone"),
                image = json.optString("image"),
                address = if (addressJson != null) Address.fromJson(addressJson) else null,

                // Novos campos
                maidenName = json.optString("maidenName"),
                age = json.optInt("age"),
                gender = json.optString("gender"),
                username = json.optString("username"),
                birthDate = json.optString("birthDate"),
                bloodGroup = json.optString("bloodGroup"),
                height = json.optDouble("height"),
                weight = json.optDouble("weight"),
                eyeColor = json.optString("eyeColor"),
                hair = if (hairJson != null) Hair.fromJson(hairJson) else null,
                ip = json.optString("ip"),
                macAddress = json.optString("macAddress"),
                university = json.optString("university"),
                bank = if (bankJson != null) Bank.fromJson(bankJson) else null,
                company = if (companyJson != null) Company.fromJson(companyJson) else null,
                ein = json.optString("ein"),
                ssn = json.optString("ssn"),
                userAgent = json.optString("userAgent"),
                crypto = if (cryptoJson != null) Crypto.fromJson(cryptoJson) else null,
                role = json.optString("role")
            )
        }
    }
}