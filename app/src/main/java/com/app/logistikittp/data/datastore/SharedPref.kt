package com.app.logistikittp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class SharedPref(private val context: Context) {

    private val userId = stringPreferencesKey("userId")
    private val emailInstitusi = stringPreferencesKey("emailInstitusi")
    private val nama = stringPreferencesKey("nama")
    private val posisi = stringPreferencesKey("posisi")
    private val nimNik = stringPreferencesKey("nimNik")
    private val programStudi = stringPreferencesKey("programStudi")
    private val password = stringPreferencesKey("password")
    private val role = stringPreferencesKey("role")
    private val profilePicture = stringPreferencesKey("profilePicture")

    suspend fun saveUserData(
        userId: String?,
        emailInstitusi: String?,
        nama: String?,
        posisi: String?,
        nimNik: String?,
        programStudi: String?,
        password: String?,
        role: String?,
        profilePicture: String?
    ) {
        context.dataStore.edit { userData ->
            userData[SharedKeys.USER_ID] = userId ?: ""
            userData[SharedKeys.EMAIL_INSTITUSI] = emailInstitusi ?: ""
            userData[SharedKeys.NAMA] = nama ?: ""
            userData[SharedKeys.POSISI] = posisi ?: ""
            userData[SharedKeys.NIM_NIK] = nimNik ?: ""
            userData[SharedKeys.PROGRAM_STUDI] = programStudi ?: ""
            userData[SharedKeys.PASSWORD] = password ?: ""
            userData[SharedKeys.ROLE] = role ?: ""
            userData[SharedKeys.PROFILE_PICTURE] = profilePicture ?: ""
        }
    }

    val getUserId: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.USER_ID] ?: "Undefined" }

    val getEmailInstitusi: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.EMAIL_INSTITUSI] ?: "Undefined" }

    val getNama: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.NAMA] ?: "Undefined" }

    val getPosisi: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.POSISI] ?: "Undefined" }

    val getNimNik: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.NIM_NIK] ?: "Undefined" }

    val getProgramStudi: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.PROGRAM_STUDI] ?: "Undefined" }

    val getPassword: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.PASSWORD] ?: "Undefined" }

    val getRole: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.ROLE] ?: "Undefined" }

    val getProfilePicture: Flow<String> = context.dataStore.data
        .map { userData -> userData[SharedKeys.PROFILE_PICTURE] ?: "Undefined" }

    val getAllUserData: Flow<String> = context.dataStore.data
        .map { userData ->
            "User ID: ${userData[SharedKeys.USER_ID] ?: "Undefined"}\n" +
                    "Email Institusi: ${userData[SharedKeys.EMAIL_INSTITUSI] ?: "Undefined"}\n" +
                    "Nama: ${userData[SharedKeys.NAMA] ?: "Undefined"}\n" +
                    "Posisi: ${userData[SharedKeys.POSISI] ?: "Undefined"}\n" +
                    "Nim/Nik: ${userData[SharedKeys.NIM_NIK] ?: "Undefined"}\n" +
                    "Program Studi: ${userData[SharedKeys.PROGRAM_STUDI] ?: "Undefined"}\n" +
                    "Password: ${userData[SharedKeys.PASSWORD] ?: "Undefined"}\n" +
                    "Role: ${userData[SharedKeys.ROLE] ?: "Undefined"}\n" +
                    "Profile Picture: ${userData[SharedKeys.PROFILE_PICTURE] ?: "Undefined"}"
        }

    suspend fun removeSession() {
        context.dataStore.edit { userData ->
            userData.remove(SharedKeys.USER_ID)
            userData.remove(SharedKeys.EMAIL_INSTITUSI)
            userData.remove(SharedKeys.NAMA)
            userData.remove(SharedKeys.POSISI)
            userData.remove(SharedKeys.NIM_NIK)
            userData.remove(SharedKeys.PROGRAM_STUDI)
            userData.remove(SharedKeys.PASSWORD)
            userData.remove(SharedKeys.ROLE)
            userData.remove(SharedKeys.PROFILE_PICTURE)
        }
    }

    object SharedKeys {
        val USER_ID = stringPreferencesKey("userId")
        val EMAIL_INSTITUSI = stringPreferencesKey("emailInstitusi")
        val NAMA = stringPreferencesKey("nama")
        val POSISI = stringPreferencesKey("posisi")
        val NIM_NIK = stringPreferencesKey("nimNik")
        val PROGRAM_STUDI = stringPreferencesKey("programStudi")
        val PASSWORD = stringPreferencesKey("password")
        val ROLE = stringPreferencesKey("role")
        val PROFILE_PICTURE = stringPreferencesKey("profilePicture")
    }
}
