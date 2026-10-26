package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.VmItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiKeyPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _apiKeyFlow = MutableStateFlow(getApiKey())
    val apiKeyFlow: StateFlow<String?> = _apiKeyFlow.asStateFlow()

    private val _regionFlow = MutableStateFlow(getRegion())
    val regionFlow: StateFlow<String> = _regionFlow.asStateFlow()

    fun getApiKey(): String? {
        val key = prefs.getString(KEY_API_KEY, null)
        return if (!key.isNullOrBlank()) key else null
    }

    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
        _apiKeyFlow.value = apiKey.trim()
    }

    fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
        _apiKeyFlow.value = null
    }

    fun getRegion(): String {
        return prefs.getString(KEY_REGION, DEFAULT_REGION) ?: DEFAULT_REGION
    }

    fun saveRegion(region: String) {
        prefs.edit().putString(KEY_REGION, region.trim()).apply()
        _regionFlow.value = region.trim()
    }

    // Widget Selected VM data
    fun getWidgetVmUuid(): String? = prefs.getString(KEY_WIDGET_VM_UUID, null)

    fun getWidgetVmName(): String = prefs.getString(KEY_WIDGET_VM_NAME, "Pilih VM di Aplikasi") ?: "Pilih VM di Aplikasi"

    fun getWidgetVmStatus(): String = prefs.getString(KEY_WIDGET_VM_STATUS, "STOPPED") ?: "STOPPED"

    fun getWidgetVmIp(): String = prefs.getString(KEY_WIDGET_VM_IP, "-") ?: "-"

    fun getWidgetVmSpec(): String = prefs.getString(KEY_WIDGET_VM_SPEC, "-") ?: "-"

    fun saveWidgetVm(vm: VmItem) {
        prefs.edit()
            .putString(KEY_WIDGET_VM_UUID, vm.uuid)
            .putString(KEY_WIDGET_VM_NAME, vm.name)
            .putString(KEY_WIDGET_VM_STATUS, vm.status.uppercase())
            .putString(KEY_WIDGET_VM_IP, vm.displayIp)
            .putString(KEY_WIDGET_VM_SPEC, "${vm.vcpu} vCPU • ${vm.ramFormatted}")
            .apply()
    }

    fun updateWidgetVmStatus(status: String) {
        prefs.edit().putString(KEY_WIDGET_VM_STATUS, status.uppercase()).apply()
    }

    companion object {
        private const val PREFS_NAME = "idcloudhost_vm_prefs"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_REGION = "region_slug"
        const val DEFAULT_REGION = "jkt01"

        private const val KEY_WIDGET_VM_UUID = "widget_vm_uuid"
        private const val KEY_WIDGET_VM_NAME = "widget_vm_name"
        private const val KEY_WIDGET_VM_STATUS = "widget_vm_status"
        private const val KEY_WIDGET_VM_IP = "widget_vm_ip"
        private const val KEY_WIDGET_VM_SPEC = "widget_vm_spec"
    }
}
