package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeoBlack
import com.example.ui.theme.NeoBorder
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoSurface
import com.example.ui.theme.NeoYellow
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoSurface,
    borderColor: Color = NeoBorder,
    shadowColor: Color = NeoBlack,
    borderWidth: Dp = 2.5.dp,
    shadowOffset: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Drop shadow box (solid)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = shadowColor, shape = shape)
        )
        // Main content box
        Box(
            modifier = Modifier
                .background(color = backgroundColor, shape = shape)
                .border(width = borderWidth, color = borderColor, shape = shape)
                .clip(shape)
        ) {
            content()
        }
    }
}

@Composable
fun BrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoYellow,
    contentColor: Color = NeoBlack,
    borderColor: Color = NeoBorder,
    shadowColor: Color = NeoBlack,
    enabled: Boolean = true,
    borderWidth: Dp = 2.5.dp,
    shadowOffset: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowOffset else 0.dp,
        label = "btnOffset"
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow (visible only when unpressed or static)
        if (!isPressed || !enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(color = shadowColor, shape = shape)
            )
        }

        // Button Surface with tactile physical offset
        Box(
            modifier = Modifier
                .offset(x = currentOffset, y = currentOffset)
                .background(
                    color = if (enabled) backgroundColor else Color(0xFFD4D4D4),
                    shape = shape
                )
                .border(width = borderWidth, color = borderColor, shape = shape)
                .clip(shape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun BrutalBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoYellow,
    textColor: Color = NeoBlack,
    isMonospace: Boolean = false,
    fontSize: Int = 11
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = shape)
            .border(width = 1.5.dp, color = NeoBorder, shape = shape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = if (isMonospace) JetBrainsMonoFamily else SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            letterSpacing = if (isMonospace) 0.5.sp else 0.2.sp
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val isRunning = status.equals("running", ignoreCase = true) ||
            status.equals("active", ignoreCase = true)
    val isStopped = status.equals("stopped", ignoreCase = true) ||
            status.equals("shutoff", ignoreCase = true)

    val (bg, label) = when {
        isRunning -> NeoGreen to "RUNNING"
        isStopped -> NeoRed to "STOPPED"
        else -> NeoYellow to status.uppercase()
    }

    BrutalBadge(
        text = "● $label",
        backgroundColor = bg,
        textColor = NeoBlack,
        modifier = modifier
    )
}
