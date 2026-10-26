package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrutalBadge
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTextField
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeoBackground
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPink
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun ApiKeyLoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onConnect: (apiKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKey by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeoBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Brand Card
            BrutalCard(
                backgroundColor = NeoYellow,
                shadowOffset = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeoBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Cloud Icon",
                            tint = NeoYellow,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "IDCLOUD VM",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        color = NeoBlack
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    BrutalBadge(
                        text = "NEOBRUTALISM CONTROLLER",
                        backgroundColor = NeoPink,
                        textColor = NeoBlack,
                        fontSize = 11
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Box Card
            BrutalCard(
                backgroundColor = Color.White,
                shadowOffset = 5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "AUTENTIKASI API KEY",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NeoBlack
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Masukkan API Token dari console.idcloudhost.com untuk mengakses dan mengontrol VM Anda secara persisten.",
                        fontFamily = SpaceGroteskFamily,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BrutalTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = "Personal API Key",
                        placeholder = "e.g. 5aea1842-895f-4bd3-8bd6-...",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle key visibility",
                                    tint = NeoBlack
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_api_key_input")
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        BrutalCard(
                            backgroundColor = Color(0xFFFFEBEB),
                            borderColor = NeoRed,
                            shadowOffset = 2.dp,
                            borderWidth = 1.5.dp,
                            modifier = Modifier.fillMaxWidth()
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

                    Spacer(modifier = Modifier.height(20.dp))

                    BrutalButton(
                        onClick = { onConnect(apiKey.trim()) },
                        enabled = apiKey.isNotBlank() && !isLoading,
                        backgroundColor = NeoGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_submit_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = NeoBlack,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MENGHUBUNGKAN...",
                                fontWeight = FontWeight.Bold,
                                fontFamily = SpaceGroteskFamily,
                                fontSize = 14.sp,
                                color = NeoBlack
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = NeoBlack,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "MASUK & SIMPAN KUNCI",
                                fontWeight = FontWeight.Bold,
                                fontFamily = SpaceGroteskFamily,
                                fontSize = 14.sp,
                                color = NeoBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tips Card
                    BrutalCard(
                        backgroundColor = Color(0xFFF6F8FA),
                        shadowOffset = 2.dp,
                        borderWidth = 1.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "CARA MENDAPATKAN API KEY:",
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = NeoBlack
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "1. Buka console.idcloudhost.com\n2. Masuk ke User Settings > API Token\n3. Buat token baru & tempelkan di sini.",
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = 10.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
