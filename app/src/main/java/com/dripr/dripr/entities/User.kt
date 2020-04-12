package com.dripr.dripr.entities

import android.content.Context
import android.os.Parcelable
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var profilePicture: String,
    var tokens: List<Token?> = arrayListOf()
) : Parcelable {

    companion object {
        fun getFromDevice(context: Context): User {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val id = preferences.getString("data.source.prefs.USER_ID", "") ?: ""
            val firstName= preferences.getString("data.source.prefs.USER_FIRSTNAME", "") ?: ""
            val lastName = preferences.getString("data.source.prefs.USER_LASTNAME", "") ?: ""
            val email= preferences.getString("data.source.prefs.USER_EMAIL", "") ?: ""
            val profilePicture= preferences.getString("data.source.prefs.USER_PROFILEPICTURE", "") ?: ""

            val tokensJson= preferences.getString("data.source.prefs.USER_TOKENS", "[]") ?: "[]"
            val tokenArr = Gson().fromJson(tokensJson,  Array<Token?>::class.java)
            var tokens = emptyList<Token?>()
            if(tokenArr.isNotEmpty()) tokens =  tokenArr.toList()

            return User(id, firstName, lastName, email, profilePicture, tokens)
        }

        fun saveToDevice(context: Context, user: User): User {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit().putString("data.source.prefs.USER_ID", user.id).apply()
            preferences.edit().putString("data.source.prefs.USER_FIRSTNAME", user.firstName).apply()
            preferences.edit().putString("data.source.prefs.USER_LASTNAME", user.lastName).apply()
            preferences.edit().putString("data.source.prefs.USER_EMAIL", user.email).apply()
            preferences.edit().putString("data.source.prefs.USER_PROFILEPICTURE", user.profilePicture).apply()
            preferences.edit().putString("data.source.prefs.USER_TOKENS", Gson().toJson(user.tokens)).apply()
            return user
        }
    }
}