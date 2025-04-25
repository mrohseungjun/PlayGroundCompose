package com.ahmedapps.bankningappui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MeditationScreen() {
    val navItems = listOf(
        NavItem("홈", Icons.Default.Home),
        NavItem("명상", Icons.Default.Home),
        NavItem("수면", Icons.Default.Home),
        NavItem("심리", Icons.Default.Home),
        NavItem("음악", Icons.Default.Home),
    )
    var selectedNav by remember { mutableStateOf(1) } // "명상" 선택

    Scaffold(
        topBar = { MeditationTopBar() },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedNav == index,
                        onClick = { selectedNav = index }
                    )
                }
            }
        },
        containerColor = Color(0xFF0E1637)
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SubscriptionBanner()
            Spacer(modifier = Modifier.height(16.dp))
            MeditationList(sampleMeditations)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationTopBar() {
    var expanded by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("전체") }

    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF0E1637),       // 배경색 지정
            titleContentColor = Color.White,           // 타이틀 컬러
            actionIconContentColor = Color.White       // 아이콘 컬러
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("명상", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { /* graduation cap action */ }) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                }
                IconButton(onClick = { /* favorite action */ }) {
                    Icon(Icons.Default.FavoriteBorder, tint = Color.White, contentDescription = null)
                }
                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(filter, color = Color.White)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("전체", "신규", "인기").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    filter = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun SubscriptionBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, tint = Color.White, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("구독하기", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun MeditationList(items: List<MeditationItem>) {
    Column {
        items.forEach { item ->
            MeditationCard(item)
        }
    }
}

@Composable
fun MeditationCard(item: MeditationItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = item.backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // 어두운 오버레이
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // 내용
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 뱃지
            Box(
                modifier = Modifier
                    .background(
                        color = when(item.badge) {
                            BadgeType.NEW -> Color(0xFFF05C5C)
                            BadgeType.POPULAR -> Color(0xFFFFB547)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Text(
                    text = when(item.badge) {
                        BadgeType.NEW -> "신규"
                        BadgeType.POPULAR -> "인기"
                    },
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(8.dp))
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "${item.count}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// 데이터 모델 & 샘플
data class MeditationItem(
    val title: String,
    @DrawableRes val backgroundRes: Int,
    val badge: BadgeType,
    val count: Int
)

enum class BadgeType { NEW, POPULAR }

val sampleMeditations = listOf(
    MeditationItem("미라클모닝 아침 루틴 명상", R.drawable.ic_launcher_foreground, BadgeType.NEW, 6),
    MeditationItem("힘을 북돋는 토닥사운드", R.drawable.ic_launcher_foreground, BadgeType.NEW, 2),
    MeditationItem("봄날의 걷기 명상", R.drawable.ic_launcher_foreground, BadgeType.POPULAR, 4),
    MeditationItem("혼자인 나를 위한 시간", R.drawable.ic_launcher_foreground, BadgeType.POPULAR, 7),
)

data class NavItem(val label: String, val icon: ImageVector)