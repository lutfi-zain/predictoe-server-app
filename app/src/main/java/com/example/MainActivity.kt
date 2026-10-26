package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.VmViewModel
import com.example.ui.components.ModifyVmDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.ApiKeyLoginScreen
import com.example.ui.screens.VmDashboardScreen
import com.example.ui.screens.VmDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeoBackground

class MainActivity : ComponentActivity() {

    private val viewModel: VmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    color = NeoBackground,
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    VmApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun VmApp(viewModel: VmViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (!state.isApiKeyConfigured) {
        ApiKeyLoginScreen(
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            onConnect = { key -> viewModel.submitApiKey(key) }
        )
    } else if (state.selectedDetailVm != null) {
        val detailVm = state.selectedDetailVm!!
        VmDetailScreen(
            vm = detailVm,
            region = state.selectedRegion,
            isWidgetPinned = state.widgetPinnedUuid == detailVm.uuid,
            isActionLoading = state.isActionLoading && state.activeActionVmUuid == detailVm.uuid,
            onBack = { viewModel.closeVmDetail() },
            onStartClick = { viewModel.startVm(detailVm) },
            onStopClick = { viewModel.stopVm(detailVm) },
            onModifyClick = { viewModel.openModifyDialog(detailVm) },
            onPinWidgetClick = { viewModel.pinToWidget(detailVm) }
        )
    } else {
        VmDashboardScreen(
            vmList = state.vmList,
            locations = state.locations,
            selectedRegion = state.selectedRegion,
            searchQuery = state.searchQuery,
            isLoading = state.isLoading,
            isActionLoading = state.isActionLoading,
            activeActionVmUuid = state.activeActionVmUuid,
            widgetPinnedUuid = state.widgetPinnedUuid,
            statusMessage = state.statusMessage,
            errorMessage = state.errorMessage,
            onSearchChange = { viewModel.setSearchQuery(it) },
            onRegionSelect = { viewModel.selectRegion(it) },
            onRefresh = { viewModel.refreshVms() },
            onOpenSettings = { viewModel.openSettings() },
            onStartVm = { vm -> viewModel.startVm(vm) },
            onStopVm = { vm -> viewModel.stopVm(vm) },
            onModifyVm = { vm -> viewModel.openModifyDialog(vm) },
            onPinWidget = { vm -> viewModel.pinToWidget(vm) },
            onVmClick = { vm -> viewModel.openVmDetail(vm) },
            onClearStatusMessage = { viewModel.clearStatusMessage() }
        )
    }

    // Modals
    state.selectedVmForModify?.let { vm ->
        ModifyVmDialog(
            vm = vm,
            onDismiss = { viewModel.closeModifyDialog() },
            onConfirm = { newName, newVcpu, newRamMb ->
                viewModel.submitModifyVm(newName, newVcpu, newRamMb)
            }
        )
    }

    if (state.isSettingsOpen) {
        SettingsDialog(
            currentApiKey = state.apiKey,
            currentRegion = state.selectedRegion,
            locations = state.locations,
            onDismiss = { viewModel.closeSettings() },
            onSaveApiKey = { newKey -> viewModel.updateApiKey(newKey) },
            onSelectRegion = { region -> viewModel.selectRegion(region) },
            onLogout = { viewModel.logout() }
        )
    }
}
