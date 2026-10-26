package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.data.model.VmItem
import com.example.ui.components.BrutalBadge
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTextField
import com.example.ui.components.VmCardItem
import com.example.ui.theme.NeoBackground
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPink
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun VmDashboardScreen(
    vmList: List<VmItem>,
    locations: List<LocationItem>,
    selectedRegion: String,
    searchQuery: String,
    isLoading: Boolean,
    isActionLoading: Boolean,
    activeActionVmUuid: String?,
    widgetPinnedUuid: String?,
    statusMessage: String?,
    errorMessage: String?,
    onSearchChange: (String) -> Unit,
    onRegionSelect: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartVm: (VmItem) -> Unit,
    onStopVm: (VmItem) -> Unit,
    onModifyVm: (VmItem) -> Unit,
    onPinWidget: (VmItem) -> Unit,
    onClearStatusMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearStatusMessage()
        }
    }

    val filteredVms = remember(vmList, searchQuery) {
        if (searchQuery.isBlank()) vmList
        else {
            val q = searchQuery.trim().lowercase()
            vmList.filter {
                it.name.lowercase().contains(q) ||
                        it.displayIp.lowercase().contains(q) ||
                        it.uuid.lowercase().contains(q)
            }
        }
    }

    val runningCount = vmList.count { it.isRunning }
    val stoppedCount = vmList.count { it.isStopped }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NeoBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NeoYellow)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Cloud App",
                            tint = NeoBlack,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IDCLOUD VM",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = NeoBlack
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BrutalButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        backgroundColor = NeoYellow,
                        shadowOffset = 2.dp,
                        borderWidth = 2.dp,
                        modifier = Modifier.testTag("refresh_dashboard_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeoBlack,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = NeoBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    BrutalButton(
                        onClick = onOpenSettings,
                        backgroundColor = Color.White,
                        shadowOffset = 2.dp,
                        borderWidth = 2.dp,
                        modifier = Modifier.testTag("settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = NeoBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Stats Quick Overview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total
                BrutalCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White,
                    shadowOffset = 2.dp,
                    borderWidth = 2.dp
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "TOTAL VM",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${vmList.size}",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = NeoBlack
                        )
                    }
                }

                // Running
                BrutalCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = NeoGreen,
                    shadowOffset = 2.dp,
                    borderWidth = 2.dp
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "RUNNING",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = NeoBlack
                        )
                        Text(
                            text = "$runningCount",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = NeoBlack
                        )
                    }
                }

                // Stopped
                BrutalCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = NeoPink,
                    shadowOffset = 2.dp,
                    borderWidth = 2.dp
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "STOPPED",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = NeoBlack
                        )
                        Text(
                            text = "$stoppedCount",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = NeoBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Region Scrollable Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REGION:",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NeoBlack
                )

                val effectiveLocations = if (locations.isNotEmpty()) locations else listOf(
                    LocationItem("jkt01", "jkt01"),
                    LocationItem("jkt02", "jkt02"),
                    LocationItem("jkt03", "jkt03"),
                    LocationItem("sgp01", "sgp01")
                )

                effectiveLocations.forEach { loc ->
                    val isSelected = loc.slug.equals(selectedRegion, ignoreCase = true)
                    BrutalButton(
                        onClick = { onRegionSelect(loc.slug) },
                        backgroundColor = if (isSelected) NeoYellow else Color.White,
                        borderWidth = 1.5.dp,
                        shadowOffset = if (isSelected) 2.dp else 1.dp
                    ) {
                        Text(
                            text = loc.slug.uppercase(),
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = NeoBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            BrutalTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = "Cari VM berdasarkan nama / IP...",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = NeoBlack
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("search_vm_input")
            )

            // Error banner if any
            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                BrutalCard(
                    backgroundColor = Color(0xFFFFEBEB),
                    borderColor = NeoRed,
                    shadowOffset = 2.dp,
                    borderWidth = 1.5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = NeoRed,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // VM List or Empty State
            if (filteredVms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BrutalCard(
                        backgroundColor = NeoSurface,
                        shadowOffset = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TIDAK ADA VIRTUAL MACHINE",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeoBlack
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (searchQuery.isNotBlank())
                                    "Tidak ada VM yang cocok dengan pencarian '$searchQuery'."
                                else
                                    "Tidak ditemukan VM pada region '${selectedRegion.uppercase()}'. Coba pilih region lain atau periksa console IDCloudHost.",
                                fontFamily = SpaceGroteskFamily,
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            BrutalButton(
                                onClick = onRefresh,
                                backgroundColor = NeoYellow
                            ) {
                                Text(
                                    text = "MUAT ULANG",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceGroteskFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVms, key = { it.uuid }) { vm ->
                        VmCardItem(
                            vm = vm,
                            isWidgetPinned = widgetPinnedUuid == vm.uuid,
                            isActionLoading = isActionLoading && activeActionVmUuid == vm.uuid,
                            onStartClick = { onStartVm(vm) },
                            onStopClick = { onStopVm(vm) },
                            onModifyClick = { onModifyVm(vm) },
                            onPinWidgetClick = { onPinWidget(vm) }
                        )
                    }
                }
            }
        }
    }
}
