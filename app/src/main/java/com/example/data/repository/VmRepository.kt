package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.ApiClient
import com.example.data.api.IdCloudHostApi
import com.example.data.model.LocationItem
import com.example.data.model.VmItem
import com.example.data.model.VmModifyRequest
import com.example.data.preferences.ApiKeyPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VmRepository(
    private val context: Context,
    private val api: IdCloudHostApi = ApiClient.apiService,
    val preferences: ApiKeyPreferences = ApiKeyPreferences(context)
) {
    private val tag = "VmRepository"

    suspend fun getLocations(): Result<List<LocationItem>> = withContext(Dispatchers.IO) {
        val apiKey = preferences.getApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(Exception("API Key belum disetel"))
        }

        try {
            val response = api.getLocations(apiKey)
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!)
            } else {
                Result.success(getDefaultLocations())
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to fetch locations dynamically, falling back to defaults", e)
            Result.success(getDefaultLocations())
        }
    }

    suspend fun getVmList(slug: String): Result<List<VmItem>> = withContext(Dispatchers.IO) {
        val apiKey = preferences.getApiKey()
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(Exception("Silakan masukkan API Key IDCloudHost terlebih dahulu."))
        }

        try {
            // First attempt with regional slug
            val response = api.getVmListBySlug(apiKey = apiKey, slug = slug)
            if (response.isSuccessful) {
                val list = response.body() ?: emptyList()
                return@withContext Result.success(list)
            }

            // Fallback attempt global list
            if (response.code() == 404) {
                val globalResponse = api.getVmListGlobal(apiKey = apiKey)
                if (globalResponse.isSuccessful) {
                    return@withContext Result.success(globalResponse.body() ?: emptyList())
                }
            }

            val errorMsg = when (response.code()) {
                401 -> "API Key tidak valid atau otorisasi ditolak (401)."
                403 -> "Akses ditolak (403). Periksa hak akses token API Anda."
                404 -> "Region / endpoint VM tidak ditemukan (404)."
                else -> "Gagal memuat VM: HTTP ${response.code()} ${response.message()}"
            }
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            Log.e(tag, "Network error fetching VMs", e)
            Result.failure(Exception("Gagal terhubung ke IDCloudHost: ${e.localizedMessage ?: "Periksa koneksi internet"}"))
        }
    }

    suspend fun startVm(slug: String, uuid: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = preferences.getApiKey()
            ?: return@withContext Result.failure(Exception("API Key tidak ditemukan"))

        try {
            val res = api.startVmBySlug(apiKey, slug, uuid)
            if (res.isSuccessful) {
                syncWidgetIfMatch(uuid, "RUNNING")
                return@withContext Result.success("VM berhasil dinyalakan")
            }

            // Fallback global endpoint
            val fallbackRes = api.startVmGlobal(apiKey, uuid)
            if (fallbackRes.isSuccessful) {
                syncWidgetIfMatch(uuid, "RUNNING")
                return@withContext Result.success("VM berhasil dinyalakan")
            }

            Result.failure(Exception("Gagal menyalakan VM: HTTP ${res.code()} ${res.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error menyalakan VM: ${e.localizedMessage}"))
        }
    }

    suspend fun stopVm(slug: String, uuid: String, force: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = preferences.getApiKey()
            ?: return@withContext Result.failure(Exception("API Key tidak ditemukan"))

        try {
            val res = api.stopVmBySlug(apiKey, slug, uuid, force)
            if (res.isSuccessful) {
                syncWidgetIfMatch(uuid, "STOPPED")
                return@withContext Result.success("VM berhasil dimatikan")
            }

            val fallbackRes = api.stopVmGlobal(apiKey, uuid, force)
            if (fallbackRes.isSuccessful) {
                syncWidgetIfMatch(uuid, "STOPPED")
                return@withContext Result.success("VM berhasil dimatikan")
            }

            Result.failure(Exception("Gagal mematikan VM: HTTP ${res.code()} ${res.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error mematikan VM: ${e.localizedMessage}"))
        }
    }

    suspend fun modifyVm(
        slug: String,
        uuid: String,
        name: String?,
        vcpu: Int?,
        ramMb: Int?
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = preferences.getApiKey()
            ?: return@withContext Result.failure(Exception("API Key tidak ditemukan"))

        val request = VmModifyRequest(
            uuid = uuid,
            name = name?.takeIf { it.isNotBlank() },
            vcpu = vcpu,
            ram = ramMb,
            memory = ramMb
        )

        try {
            val res = api.modifyVmBySlug(apiKey, slug, request)
            if (res.isSuccessful) {
                return@withContext Result.success("Spesifikasi VM berhasil diperbarui")
            }

            val fallbackRes = api.modifyVmGlobal(apiKey, request)
            if (fallbackRes.isSuccessful) {
                return@withContext Result.success("Spesifikasi VM berhasil diperbarui")
            }

            Result.failure(Exception("Gagal mengubah VM: HTTP ${res.code()} ${res.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error mengubah VM: ${e.localizedMessage}"))
        }
    }

    private fun syncWidgetIfMatch(uuid: String, newStatus: String) {
        if (preferences.getWidgetVmUuid() == uuid) {
            preferences.updateWidgetVmStatus(newStatus)
        }
    }

    private fun getDefaultLocations(): List<LocationItem> = listOf(
        LocationItem("jkt01", "Jakarta - Equinix (jkt01)", "Indonesia"),
        LocationItem("jkt02", "Jakarta - Cyber 1 (jkt02)", "Indonesia"),
        LocationItem("jkt03", "Jakarta - DCI (jkt03)", "Indonesia"),
        LocationItem("sgp01", "Singapore (sgp01)", "Singapore")
    )
}
