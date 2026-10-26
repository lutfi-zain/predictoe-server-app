package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VmItem
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun ModifyVmDialog(
    vm: VmItem,
    onDismiss: () -> Unit,
    onConfirm: (newName: String, newVcpu: Int, newRamMb: Int) -> Unit
) {
    var name by remember { mutableStateOf(vm.name) }
    var vcpu by remember { mutableIntStateOf(vm.vcpu.coerceAtLeast(1)) }
    var ramGb by remember { mutableIntStateOf((vm.ram / 1024).coerceAtLeast(1)) }

    Dialog(onDismissRequest = onDismiss) {
        BrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("modify_vm_dialog"),
            backgroundColor = NeoSurface,
            shadowOffset = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "MODIFIKASI SPESIFIKASI",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = NeoBlack
                )
                Text(
                    text = "Ubah nama, core vCPU, dan memori RAM VM.",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                BrutalTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama Virtual Machine",
                    placeholder = "Misal: production-server",
                    modifier = Modifier.testTag("input_vm_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                BrutalStepper(
                    value = vcpu,
                    onValueChange = { vcpu = it },
                    label = "Alokasi vCPU",
                    unit = "Core",
                    min = 1,
                    max = 16,
                    step = 1,
                    modifier = Modifier.testTag("stepper_vcpu")
                )

                Spacer(modifier = Modifier.height(14.dp))

                BrutalStepper(
                    value = ramGb,
                    onValueChange = { ramGb = it },
                    label = "Alokasi Memori RAM",
                    unit = "GB",
                    min = 1,
                    max = 64,
                    step = 1,
                    modifier = Modifier.testTag("stepper_ram")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BrutalButton(
                        onClick = onDismiss,
                        backgroundColor = Color.White,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_modify")
                    ) {
                        Text(
                            text = "BATAL",
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 13.sp
                        )
                    }

                    BrutalButton(
                        onClick = {
                            onConfirm(name.trim(), vcpu, ramGb * 1024)
                        },
                        enabled = name.isNotBlank(),
                        backgroundColor = NeoGreen,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_modify")
                    ) {
                        Text(
                            text = "SIMPAN",
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
