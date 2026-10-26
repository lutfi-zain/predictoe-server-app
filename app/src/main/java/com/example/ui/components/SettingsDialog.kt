package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.LocationItem
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPink
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun SettingsDialog(
    currentApiKey: String,
    currentRegion: String,
    locations: List<LocationItem>,
    onDismiss: () -> Unit,
    onSaveApiKey: (String) -> Unit,
    onSelectRegion: (String) -> Unit,
    onLogout: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        BrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("settings_dialog"),
            backgroundColor = NeoSurface,
            shadowOffset = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "PENGATURAN API & REGION",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = NeoBlack
                )
                Text(
                    text = "Kelola API Key IDCloudHost dan preferensi region.",
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                BrutalTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = "IDCloudHost API Key",
                    placeholder = "Masukkan API Key Anda...",
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = NeoBlack
                            )
                        }
                    },
                    modifier = Modifier.testTag("input_settings_api_key")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PILIH DATA CENTER REGION",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NeoBlack,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    locations.forEach { loc ->
                        val isSelected = loc.slug.equals(currentRegion, ignoreCase = true)
                        BrutalCard(
                            backgroundColor = if (isSelected) NeoYellow else Color.White,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectRegion(loc.slug) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = loc.name,
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = NeoBlack
                                )
                                if (isSelected) {
                                    BrutalBadge(
                                        text = "AKTIF",
                                        backgroundColor = NeoGreen,
                                        fontSize = 10
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BrutalButton(
                        onClick = onLogout,
                        backgroundColor = NeoPink,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_logout")
                    ) {
                        Text(
                            text = "HAPUS KUNCI",
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = NeoBlack
                        )
                    }

                    BrutalButton(
                        onClick = {
                            if (apiKeyInput.isNotBlank()) {
                                onSaveApiKey(apiKeyInput.trim())
                            }
                            onDismiss()
                        },
                        backgroundColor = NeoGreen,
                        borderWidth = 2.dp,
                        shadowOffset = 2.dp,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("btn_save_settings")
                    ) {
                        Text(
                            text = "SIMPAN PENGATURAN",
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 11.sp,
                            color = NeoBlack
                        )
                    }
                }
            }
        }
    }
}
