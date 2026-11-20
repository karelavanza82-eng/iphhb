package com.example.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.LauncherSettings
import com.example.icons.IosAppIcon
import com.example.launcher.InstalledAppInfo
import com.example.notifications.IosNotificationBadge
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosSansFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpotlightSearchOverlay(
    installedApps: List<InstalledAppInfo>,
    recentSearches: List<String>,
    badgeCounts: Map<String, Int>,
    settings: LauncherSettings,
    onLaunchApp: (InstalledAppInfo) -> Unit,
    onRecordSearch: (String) -> Unit,
    onClearRecents: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val filteredApps = remember(query, installedApps, settings.searchIncludePackageName) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            installedApps.take(12)
        } else {
            installedApps.filter { app ->
                app.label.lowercase().contains(q) ||
                    (settings.searchIncludePackageName && app.packageName.lowercase().contains(q))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("spotlight_search_overlay")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C2C2E).copy(alpha = 0.92f))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFAEAEB2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search apps or packages",
                            color = Color(0xFF8E8E93),
                            fontFamily = IosSansFontFamily,
                            fontSize = 16.sp
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        cursorBrush = SolidColor(IosBlue),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontFamily = IosSansFontFamily,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .testTag("spotlight_search_input")
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { query = "" },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("spotlight_clear_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF636366)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color(0xFF1C1C1E),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("spotlight_cancel_button")
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontFamily = IosSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (query.isBlank() && recentSearches.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches",
                    color = Color.White.copy(alpha = 0.72f),
                    fontFamily = IosSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                TextButton(
                    onClick = onClearRecents,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Clear", color = IosBlue, fontSize = 13.sp, fontFamily = IosSansFontFamily)
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentSearches.forEach { term ->
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable { query = term }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.75f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = term,
                            color = Color.White,
                            fontFamily = IosSansFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        Text(
            text = if (query.isBlank()) "Suggestions" else "Top Hits (${filteredApps.size})",
            color = Color.White.copy(alpha = 0.72f),
            fontFamily = IosSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching apps found for \"$query\"",
                    color = Color.White.copy(alpha = 0.75f),
                    fontFamily = IosSansFontFamily,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF232326).copy(alpha = 0.88f)),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filteredApps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                    val badgeCount = badgeCounts[app.packageName] ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (query.isNotBlank()) onRecordSearch(query)
                                onLaunchApp(app)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("search_result_${app.packageName}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            IosAppIcon(
                                packageName = app.packageName,
                                label = app.label,
                                iconSize = 44.dp,
                                cornerRadiusPercent = settings.iconCornerRadiusPercent,
                                darkThemeMode = settings.darkIconTheme,
                                forceIosIconPack = settings.forceIosIconPack
                            )
                            if (badgeCount > 0) {
                                IosNotificationBadge(
                                    count = badgeCount,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.label,
                                color = Color.White,
                                fontFamily = IosSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (settings.searchIncludePackageName) {
                                Text(
                                    text = app.packageName,
                                    color = Color.White.copy(alpha = 0.55f),
                                    fontFamily = IosSansFontFamily,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
