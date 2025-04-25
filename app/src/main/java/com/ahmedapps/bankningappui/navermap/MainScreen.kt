package com.ahmedapps.bankningappui.navermap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmedapps.bankningappui.navermap.components.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun NaverMapApp() {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        },
        topBar = { StatusBar() },
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
            )

            // 카테고리 바
            CategoryBar(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            // 날씨 정보
            WeatherInfo(
                modifier = Modifier
                    .align(Alignment.Start)
            )

            // 나침반
            Compass(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            // 위치 버튼
            LocationButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

        }
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "12:22",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifi4Bar,
                contentDescription = "Signal",
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "5G",
                modifier = Modifier.padding(start = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "82",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .background(Color.Black)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            modifier = Modifier.padding(start = 16.dp),
            tint = Color.Gray
        )

        Text(
            text = "장소, 버스, 지하철, 주소 검색",
            color = Color.Gray,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Search",
            modifier = Modifier.padding(end = 16.dp),
            tint = Color.Gray
        )

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4285F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Search",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CategoryBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryItem(
            icon = Icons.Default.Restaurant,
            text = "음식점"
        )

        CategoryItem(
            icon = Icons.Default.Coffee,
            text = "카페"
        )

        CategoryItem(
            icon = Icons.Default.Store,
            text = "편의점"
        )

        CategoryItem(
            icon = Icons.Default.FoodBank,
            text = "가볼만한곳"
        )

        CategoryItem(
            icon = Icons.Default.LocalGasStation,
            text = "주유"
        )
    }
}

@Composable
fun CategoryItem(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
        Text(
            text = text,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun WeatherInfo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700)),
            contentAlignment = Alignment.Center
        ) {
            // 날씨 아이콘
        }

        Text(
            text = "18°",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "미세",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun Compass(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // 나침반 아이콘
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "Compass",
            modifier = Modifier.size(24.dp),
            tint = Color.Red
        )
    }
}

@Composable
fun LocationButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(32.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color(0xFF4285F4),
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = "서구 아미동",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
