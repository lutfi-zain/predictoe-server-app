package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoBorder
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoTextMuted
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun BrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(10.dp)

    Column(modifier = modifier) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label.uppercase(),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = NeoBlack,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            // Shadow box
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(color = NeoBlack, shape = shape)
            )

            // Input field box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NeoSurface, shape = shape)
                    .border(width = 2.dp, color = NeoBorder, shape = shape)
                    .clip(shape)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = NeoTextMuted,
                                fontSize = 14.sp,
                                fontFamily = SpaceGroteskFamily
                            )
                        }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            textStyle = TextStyle(
                                color = NeoBlack,
                                fontSize = 14.sp,
                                fontFamily = SpaceGroteskFamily,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(NeoBlack),
                            visualTransformation = visualTransformation,
                            keyboardOptions = keyboardOptions,
                            keyboardActions = keyboardActions,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        trailingIcon()
                    }
                }
            }
        }
    }
}

@Composable
fun BrutalStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    unit: String,
    min: Int = 1,
    max: Int = 16,
    step: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = NeoBlack,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BrutalButton(
                onClick = { if (value - step >= min) onValueChange(value - step) },
                enabled = value > min,
                backgroundColor = Color.White,
                shadowOffset = 2.dp,
                borderWidth = 2.dp,
                modifier = Modifier.width(46.dp)
            ) {
                Text(
                    text = "-",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SpaceGroteskFamily
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            BrutalCard(
                modifier = Modifier.weight(1f),
                backgroundColor = NeoYellow,
                shadowOffset = 2.dp,
                borderWidth = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value $unit",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeoBlack
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            BrutalButton(
                onClick = { if (value + step <= max) onValueChange(value + step) },
                enabled = value < max,
                backgroundColor = Color.White,
                shadowOffset = 2.dp,
                borderWidth = 2.dp,
                modifier = Modifier.width(46.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SpaceGroteskFamily
                )
            }
        }
    }
}
