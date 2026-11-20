package com.example.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosRed
import com.example.ui.theme.IosSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerSheet(
    hostManager: AppWidgetHostManager,
    onAddIosWidget: (kind: IosWidgetKind, spanCols: Int, spanRows: Int) -> Unit,
    onAddAndroidWidget: (provider: AppWidgetProviderInfo, spanCols: Int, spanRows: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val systemProviders = remember { hostManager.getInstalledWidgetProviders() }
    var selectedSizeIdx by remember { mutableIntStateOf(0) } // 0 = 2x2 Small, 1 = 4x2 Medium

    val iosWidgetCards: List<Triple<IosWidgetKind, String, Pair<ImageVector, Color>>> = remember {
        listOf(
            Triple(IosWidgetKind.WEATHER, "Current temperature & forecast", Icons.Default.WbSunny to Color(0xFF2D659E)),
            Triple(IosWidgetKind.CALENDAR, "Day, date & today's events", Icons.Default.CalendarToday to IosRed),
            Triple(IosWidgetKind.CLOCK, "Analog live clock dial", Icons.Default.Schedule to Color(0xFF1C1C1E)),
            Triple(IosWidgetKind.BATTERY, "Device battery level & status", Icons.Default.BatteryChargingFull to IosGreen),
            Triple(IosWidgetKind.PHOTO, "Favorite photo showcase", Icons.Default.Photo to IosOrange),
            Triple(IosWidgetKind.MUSIC, "Quick Music player card", Icons.Default.MusicNote to Color(0xFFFA2D48)),
            Triple(IosWidgetKind.CUSTOM, "Pinned quick note card", Icons.Default.EditNote to Color(0xFFFFC300))
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1C1C1E),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.84f)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add Widget",
                    color = Color.White,
                    fontFamily = IosSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                listOf("Small (2x2)", "Medium (4x2)").forEachIndexed { idx, label ->
                    SegmentedButton(
                        selected = selectedSizeIdx == idx,
                        onClick = { selectedSizeIdx = idx },
                        shape = SegmentedButtonDefaults.itemShape(index = idx, count = 2),
                        modifier = Modifier.testTag("widget_size_$idx")
                    ) {
                        Text(label, fontFamily = IosSansFontFamily, fontSize = 13.sp)
                    }
                }
            }

            val spanCols = if (selectedSizeIdx == 0) 2 else 4
            val spanRows = 2

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "iOS Widgets",
                        color = Color.White.copy(alpha = 0.65f),
                        fontFamily = IosSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(iosWidgetCards) { (kind, subtitle, iconPair) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2C2C2E))
                            .clickable {
                                onAddIosWidget(kind, spanCols, spanRows)
                                onDismiss()
                            }
                            .padding(14.dp)
                            .testTag("add_ios_widget_${kind.name}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(iconPair.second),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconPair.first, contentDescription = null, tint = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = kind.title,
                                color = Color.White,
                                fontFamily = IosSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = subtitle,
                                color = Color.White.copy(alpha = 0.65f),
                                fontFamily = IosSansFontFamily,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add ${kind.title}",
                            tint = IosBlue
                        )
                    }
                }

                if (systemProviders.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Android App Widgets (AppWidgetHost)",
                            color = Color.White.copy(alpha = 0.65f),
                            fontFamily = IosSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(systemProviders) { provider ->
                        val label = remember(provider) { provider.loadLabel(context.packageManager) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2C2C2E))
                                .clickable {
                                    onAddAndroidWidget(provider, spanCols, spanRows)
                                    onDismiss()
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF3A3A3C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Widgets, contentDescription = null, tint = IosBlue)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    fontFamily = IosSansFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = provider.provider.packageName,
                                    color = Color.White.copy(alpha = 0.55f),
                                    fontFamily = IosSansFontFamily,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Add widget",
                                tint = IosBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
