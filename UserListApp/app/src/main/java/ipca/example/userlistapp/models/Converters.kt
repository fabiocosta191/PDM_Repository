package ipca.example.userlistapp.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // Conversor para Address
    @TypeConverter
    fun fromAddress(address: Address?): String? {
        return gson.toJson(address)
    }

    @TypeConverter
    fun toAddress(addressString: String?): Address? {
        return gson.fromJson(addressString, Address::class.java)
    }

    // Conversor para Hair
    @TypeConverter
    fun fromHair(hair: Hair?): String? {
        return gson.toJson(hair)
    }

    @TypeConverter
    fun toHair(hairString: String?): Hair? {
        return gson.fromJson(hairString, Hair::class.java)
    }

    // Conversor para Bank
    @TypeConverter
    fun fromBank(bank: Bank?): String? {
        return gson.toJson(bank)
    }

    @TypeConverter
    fun toBank(bankString: String?): Bank? {
        return gson.fromJson(bankString, Bank::class.java)
    }

    // Conversor para Company
    @TypeConverter
    fun fromCompany(company: Company?): String? {
        return gson.toJson(company)
    }

    @TypeConverter
    fun toCompany(companyString: String?): Company? {
        return gson.fromJson(companyString, Company::class.java)
    }

    // Conversor para Crypto
    @TypeConverter
    fun fromCrypto(crypto: Crypto?): String? {
        return gson.toJson(crypto)
    }

    @TypeConverter
    fun toCrypto(cryptoString: String?): Crypto? {
        return gson.fromJson(cryptoString, Crypto::class.java)
    }
}