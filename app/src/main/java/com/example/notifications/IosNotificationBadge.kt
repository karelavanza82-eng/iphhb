package com.example.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosBadgeRed
import com.example.ui.theme.IosSansFontFamily

@Composable
fun IosNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = count > 0,
        modifier = modifier,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        val text = if (count > 99) "99+" else count.toString()
        val horizontalPad = if (text.length == 1) 0.dp else 6.dp
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 21.dp)
                .height(21.dp)
                .shadow(elevation = 3.dp, shape = CircleShape, clip = false)
                .background(color = IosBadgeRed, shape = CircleShape)
                .padding(horizontal = horizontalPad),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontFamily = IosSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 13.sp,
                maxLines = 1
            )
        }
    }
}
