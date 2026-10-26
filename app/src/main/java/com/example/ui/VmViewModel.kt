package com.example.ui

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LocationItem
import com.example.data.model.VmItem
import com.example.data.repository.VmRepository
import com.example.widget.IdchWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VmUiState(
    val isApiKeyConfigured: Boolean = false,
    val apiKey: String = "",
    val selectedRegion: String = "jkt01",
    val locations: List<LocationItem> = emptyList(),
    val vmList: List<VmItem> = emptyList(),
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val activeActionVmUuid: String? = null,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
    val selectedVmForModify: VmItem? = null,
    val isSettingsOpen: Boolean = false,
    val searchQuery: String = "",
    val widgetPinnedUuid: String? = null
)

class VmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VmRepository(application)
    private val prefs = repository.preferences

    private val _uiState = MutableStateFlow(VmUiState())
    val uiState: StateFlow<VmUiState> = _uiState.asStateFlow()

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        val key = prefs.getApiKey()
        val region = prefs.getRegion()
        val widgetUuid = prefs.getWidgetVmUuid()

        if (!key.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isApiKeyConfigured = true,
                    apiKey = key,
                    selectedRegion = region,
                    widgetPinnedUuid = widgetUuid
                )
            }
            fetchInitialData()
        } else {
            _uiState.update {
                it.copy(
                    isApiKeyConfigured = false,
                    selectedRegion = region,
                    widgetPinnedUuid = widgetUuid
                )
            }
        }
    }

    fun submitApiKey(newKey: String) {
        if (newKey.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            prefs.saveApiKey(newKey)

            // Test connection by fetching locations & VMs
            val locResult = repository.getLocations()
            val locations = locResult.getOrNull() ?: emptyList()
            val region = _uiState.value.selectedRegion

            val vmResult = repository.getVmList(region)
            if (vmResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isApiKeyConfigured = true,
                        apiKey = newKey,
                        locations = locations,
                        vmList = vmResult.getOrDefault(emptyList()),
                        isLoading = false,
                        errorMessage = null,
                        statusMessage = "Berhasil terhubung ke IDCloudHost!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = vmResult.exceptionOrNull()?.localizedMessage ?: "Gagal terhubung"
                    )
                }
            }
        }
    }

    fun updateApiKey(newKey: String) {
        if (newKey.isBlank()) return
        prefs.saveApiKey(newKey)
        _uiState.update { it.copy(apiKey = newKey) }
        refreshVms()
    }

    fun logout() {
        prefs.clearApiKey()
        _uiState.update {
            it.copy(
                isApiKeyConfigured = false,
                apiKey = "",
                vmList = emptyList(),
                isSettingsOpen = false
            )
        }
    }

    fun fetchInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load locations
            val locResult = repository.getLocations()
            val locations = locResult.getOrDefault(emptyList())

            val currentRegion = _uiState.value.selectedRegion
            val vmResult = repository.getVmList(currentRegion)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    locations = locations,
                    vmList = vmResult.getOrDefault(emptyList()),
                    errorMessage = vmResult.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun selectRegion(regionSlug: String) {
        prefs.saveRegion(regionSlug)
        _uiState.update { it.copy(selectedRegion = regionSlug) }
        refreshVms()
    }

    fun refreshVms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val region = _uiState.value.selectedRegion
            val result = repository.getVmList(region)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    vmList = result.getOrDefault(emptyList()),
                    errorMessage = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun startVm(vm: VmItem) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionLoading = true,
                    activeActionVmUuid = vm.uuid
                )
            }

            val region = _uiState.value.selectedRegion
            val result = repository.startVm(region, vm.uuid)

            if (result.isSuccess) {
                _uiState.update { state ->
                    val updatedList = state.vmList.map {
                        if (it.uuid == vm.uuid) it.copy(status = "RUNNING") else it
                    }
                    state.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        vmList = updatedList,
                        statusMessage = "VM '${vm.name}' berhasil dinyalakan"
                    )
                }
                broadcastWidgetUpdate()
            } else {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        statusMessage = result.exceptionOrNull()?.localizedMessage ?: "Gagal menyalakan VM"
                    )
                }
            }
        }
    }

    fun stopVm(vm: VmItem, force: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isActionLoading = true,
                    activeActionVmUuid = vm.uuid
                )
            }

            val region = _uiState.value.selectedRegion
            val result = repository.stopVm(region, vm.uuid, force)

            if (result.isSuccess) {
                _uiState.update { state ->
                    val updatedList = state.vmList.map {
                        if (it.uuid == vm.uuid) it.copy(status = "STOPPED") else it
                    }
                    state.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        vmList = updatedList,
                        statusMessage = "VM '${vm.name}' berhasil dimatikan"
                    )
                }
                broadcastWidgetUpdate()
            } else {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        statusMessage = result.exceptionOrNull()?.localizedMessage ?: "Gagal mematikan VM"
                    )
                }
            }
        }
    }

    fun openModifyDialog(vm: VmItem) {
        _uiState.update { it.copy(selectedVmForModify = vm) }
    }

    fun closeModifyDialog() {
        _uiState.update { it.copy(selectedVmForModify = null) }
    }

    fun submitModifyVm(newName: String, newVcpu: Int, newRamMb: Int) {
        val targetVm = _uiState.value.selectedVmForModify ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedVmForModify = null,
                    isActionLoading = true,
                    activeActionVmUuid = targetVm.uuid
                )
            }

            val region = _uiState.value.selectedRegion
            val result = repository.modifyVm(
                slug = region,
                uuid = targetVm.uuid,
                name = newName,
                vcpu = newVcpu,
                ramMb = newRamMb
            )

            if (result.isSuccess) {
                _uiState.update { state ->
                    val updatedList = state.vmList.map {
                        if (it.uuid == targetVm.uuid) {
                            it.copy(
                                name = newName,
                                vcpu = newVcpu,
                                ramRaw = newRamMb
                            )
                        } else it
                    }
                    state.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        vmList = updatedList,
                        statusMessage = "Spesifikasi '${newName}' berhasil diperbarui!"
                    )
                }
                refreshVms()
                broadcastWidgetUpdate()
            } else {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        activeActionVmUuid = null,
                        statusMessage = result.exceptionOrNull()?.localizedMessage ?: "Gagal mengubah VM"
                    )
                }
            }
        }
    }

    fun pinToWidget(vm: VmItem) {
        prefs.saveWidgetVm(vm)
        _uiState.update {
            it.copy(
                widgetPinnedUuid = vm.uuid,
                statusMessage = "'${vm.name}' dipasang ke Home Screen Widget!"
            )
        }
        broadcastWidgetUpdate()
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun broadcastWidgetUpdate() {
        val context = getApplication<Application>()
        val intent = Intent(context, IdchWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, IdchWidgetProvider::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
