package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VmItem
import com.example.ui.components.BrutalBadge
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeoBackground
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPink
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoTextMuted
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.SpaceGroteskFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

@Composable
fun VmDetailScreen(
    vm: VmItem,
    region: String,
    isWidgetPinned: Boolean,
    isActionLoading: Boolean,
    onBack: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onModifyClick: () -> Unit,
    onPinWidgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pingLatencyMs by remember { mutableStateOf<Long?>(null) }
    var isPinging by remember { mutableStateOf(false) }
    var pingStatus by remember { mutableStateOf<String?>(null) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label disalin ke clipboard", Toast.LENGTH_SHORT).show()
    }

    fun testConnectivity(ip: String) {
        if (ip.isBlank() || ip.contains("No IP")) {
            pingStatus = "Alamat IP tidak valid"
            return
        }
        isPinging = true
        pingStatus = "Menguji koneksi ke $ip..."
        pingLatencyMs = null

        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val startTime = System.currentTimeMillis()
                    val socket = Socket()
                    // Try port 22 (SSH) or 80 with 3s timeout
                    val address = InetSocketAddress(ip, 22)
                    socket.connect(address, 3000)
                    val elapsed = System.currentTimeMillis() - startTime
                    socket.close()
                    elapsed to true
                } catch (e: Exception) {
                    // Try port 80
                    try {
                        val startTime = System.currentTimeMillis()
                        val socket = Socket()
                        val address = InetSocketAddress(ip, 80)
                        socket.connect(address, 3000)
                        val elapsed = System.currentTimeMillis() - startTime
                        socket.close()
                        elapsed to true
                    } catch (e2: Exception) {
                        -1L to false
                    }
                }
            }
            isPinging = false
            if (result.second && result.first > 0) {
                pingLatencyMs = result.first
                pingStatus = "Terhubung (${result.first} ms)"
            } else {
                pingLatencyMs = null
                pingStatus = "Port 22/80 tidak merespons (VM mungkin sedang boot atau firewall tertutup)"
            }
        }
    }

    Scaffold(
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalButton(
                    onClick = onBack,
                    backgroundColor = Color.White,
                    borderWidth = 2.dp,
                    shadowOffset = 2.dp,
                    modifier = Modifier.testTag("detail_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = NeoBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vm.name,
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = NeoBlack,
                        maxLines = 1
                    )
                    Text(
                        text = "REGION: ${region.uppercase()}",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 11.sp,
                        color = NeoTextMuted
                    )
                }

                StatusBadge(status = vm.status)
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Control & Action Bar
                BrutalCard(
                    backgroundColor = NeoSurface,
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "KONTROL DAYA & WIDGET",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeoBlack
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (vm.isRunning) {
                                BrutalButton(
                                    onClick = onStopClick,
                                    enabled = !isActionLoading,
                                    backgroundColor = NeoRed,
                                    borderWidth = 2.dp,
                                    shadowOffset = 2.dp,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = null,
                                        tint = NeoBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "STOP",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SpaceGroteskFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                BrutalButton(
                                    onClick = onStartClick,
                                    enabled = !isActionLoading,
                                    backgroundColor = NeoGreen,
                                    borderWidth = 2.dp,
                                    shadowOffset = 2.dp,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = NeoBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "START",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SpaceGroteskFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            BrutalButton(
                                onClick = onModifyClick,
                                enabled = !isActionLoading,
                                backgroundColor = NeoYellow,
                                borderWidth = 2.dp,
                                shadowOffset = 2.dp,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = NeoBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RESIZE",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceGroteskFamily,
                                    fontSize = 12.sp
                                )
                            }

                            BrutalButton(
                                onClick = onPinWidgetClick,
                                enabled = !isActionLoading,
                                backgroundColor = if (isWidgetPinned) NeoPink else Color.White,
                                borderWidth = 2.dp,
                                shadowOffset = 2.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = "Pin to Home Widget",
                                    tint = NeoBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Hardware Specs Card
                BrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = NeoBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SPESIFIKASI KOMPUTASI",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeoBlack
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BrutalCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = NeoYellow,
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "vCPU CORES",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = NeoBlack
                                    )
                                    Text(
                                        text = "${vm.vcpu} Core",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = NeoBlack
                                    )
                                }
                            }

                            BrutalCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = Color(0xFFD6E4FF),
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "MEMORI RAM",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = NeoBlack
                                    )
                                    Text(
                                        text = vm.ramFormatted,
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = NeoBlack
                                    )
                                }
                            }

                            BrutalCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = Color(0xFFFFE8D6),
                                shadowOffset = 2.dp,
                                borderWidth = 1.5.dp
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "STORAGE",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = NeoBlack
                                    )
                                    Text(
                                        text = "${vm.diskSize} GB",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = NeoBlack
                                    )
                                }
                            }
                        }

                        // Storage Disks List
                        if (!vm.storage.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "DAFTAR DISK TERPASANG:",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = NeoBlack
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            vm.storage.forEach { disk ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = null,
                                            tint = NeoBlack,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${disk.name ?: "disk"} (${disk.type ?: "block"})",
                                            fontFamily = JetBrainsMonoFamily,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    BrutalBadge(
                                        text = "${disk.size ?: 0} GB ${if (disk.primary == true) "• PRIMARY" else ""}",
                                        backgroundColor = if (disk.primary == true) NeoYellow else Color(0xFFE5E5E5),
                                        fontSize = 10
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Network & Connectivity Card
                BrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NetworkCheck,
                                    contentDescription = null,
                                    tint = NeoBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "JARINGAN & ALAMAT IP",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoBlack
                                )
                            }

                            BrutalButton(
                                onClick = { testConnectivity(vm.displayIp) },
                                enabled = !isPinging,
                                backgroundColor = NeoYellow,
                                borderWidth = 1.5.dp,
                                shadowOffset = 1.5.dp
                            ) {
                                if (isPinging) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = NeoBlack
                                    )
                                } else {
                                    Text(
                                        text = "TEST PING",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        if (!pingStatus.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pingStatus!!,
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = 11.sp,
                                color = if (pingLatencyMs != null) Color(0xFF008844) else NeoPink,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Public IP
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PUBLIC IP (IPv4)",
                                    fontFamily = SpaceGroteskFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = vm.displayIp,
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { copyToClipboard("Public IP", vm.displayIp) }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy IP",
                                    tint = NeoBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Private IP
                        if (!vm.privateIpv4.isNullOrBlank() || !vm.privateIp.isNullOrBlank()) {
                            val privIp = vm.privateIpv4 ?: vm.privateIp ?: "-"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "PRIVATE IP (VPC)",
                                        fontFamily = SpaceGroteskFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = privIp,
                                        fontFamily = JetBrainsMonoFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = { copyToClipboard("Private IP", privIp) }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Private IP",
                                        tint = NeoBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. System UUID & Metadata Card
                BrutalCard(
                    backgroundColor = Color.White,
                    shadowOffset = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "METADATA & IDENTITAS VM",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeoBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FULL UUID",
                                    fontFamily = SpaceGroteskFamily,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = vm.uuid,
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { copyToClipboard("VM UUID", vm.uuid) }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy UUID",
                                    tint = NeoBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (!vm.osName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sistem Operasi", fontFamily = SpaceGroteskFamily, fontSize = 11.sp, color = Color.Gray)
                                Text("${vm.osName} ${vm.osVersion ?: ""}".trim(), fontFamily = SpaceGroteskFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (!vm.createdAt.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dibuat Pada", fontFamily = SpaceGroteskFamily, fontSize = 11.sp, color = Color.Gray)
                                Text(vm.createdAt, fontFamily = JetBrainsMonoFamily, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
