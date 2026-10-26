package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VmItem
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoBlue
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPink
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoTextMuted
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun VmCardItem(
    vm: VmItem,
    isWidgetPinned: Boolean,
    isActionLoading: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onModifyClick: () -> Unit,
    onPinWidgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BrutalCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vm_card_${vm.uuid}"),
        backgroundColor = NeoSurface,
        shadowOffset = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Name and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vm.name,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = NeoBlack
                    )
                    Text(
                        text = "UUID: ${vm.uuid.take(12)}...",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 10.sp,
                        color = NeoTextMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = vm.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IP and Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalBadge(
                    text = vm.displayIp,
                    backgroundColor = Color(0xFFF1F1F1),
                    isMonospace = true,
                    fontSize = 11
                )
                BrutalBadge(
                    text = "${vm.vcpu} vCPU",
                    backgroundColor = NeoYellow,
                    fontSize = 11
                )
                BrutalBadge(
                    text = vm.ramFormatted,
                    backgroundColor = Color(0xFFD6E4FF),
                    fontSize = 11
                )
                if (vm.diskSize > 0) {
                    BrutalBadge(
                        text = "${vm.diskSize} GB",
                        backgroundColor = Color(0xFFFFE8D6),
                        fontSize = 11
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Power toggle button (Start or Stop)
                if (vm.isRunning) {
                    BrutalButton(
                        onClick = onStopClick,
                        enabled = !isActionLoading,
                        backgroundColor = NeoRed,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stop_btn_${vm.uuid}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop VM",
                            tint = NeoBlack,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "STOP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = SpaceGroteskFamily,
                            color = NeoBlack
                        )
                    }
                } else {
                    BrutalButton(
                        onClick = onStartClick,
                        enabled = !isActionLoading,
                        backgroundColor = NeoGreen,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("start_btn_${vm.uuid}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start VM",
                            tint = NeoBlack,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "START",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = SpaceGroteskFamily,
                            color = NeoBlack
                        )
                    }
                }

                // Modify button
                BrutalButton(
                    onClick = onModifyClick,
                    enabled = !isActionLoading,
                    backgroundColor = NeoYellow,
                    borderWidth = 2.dp,
                    shadowOffset = 2.dp,
                    modifier = Modifier.testTag("modify_btn_${vm.uuid}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Modify VM",
                        tint = NeoBlack,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "MODIFY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = SpaceGroteskFamily,
                        color = NeoBlack
                    )
                }

                // Pin to Widget button
                BrutalButton(
                    onClick = onPinWidgetClick,
                    enabled = !isActionLoading,
                    backgroundColor = if (isWidgetPinned) NeoPink else Color.White,
                    borderWidth = 2.dp,
                    shadowOffset = 2.dp,
                    modifier = Modifier.testTag("pin_widget_btn_${vm.uuid}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Pin to Home Widget",
                        tint = NeoBlack
                    )
                }
            }
        }
    }
}
