import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// 데이터 클래스들
data class WeatherData(
    val temperature: Int,
    val humidity: Int,
    val windSpeed: Double,
    val rainChance: Int,
    val airQuality: Int,
    val airQualityStatus: String
)

data class HikingTrail(
    val id: Int,
    val name: String,
    val distance: Double,
    val duration: Int,
    val difficulty: String,
    val crowdness: String,
    val rating: Double
)

// 미세먼지 상태에 따른 색상 반환 함수
@Composable
fun getAirQualityColor(status: String): Color {
    return when (status) {
        "좋음" -> Color(0xFF4CAF50)
        "보통" -> Color(0xFFFFB300)
        "나쁨" -> Color(0xFFFF9800)
        "매우 나쁨" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

// 앱의 메인 컴포저블
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun HikingApp() {
    // 앱 상태값
    var selectedTrail by remember { mutableStateOf<HikingTrail?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // 코루틴 스코프
    val scope = rememberCoroutineScope()

    // 바텀 시트 상태
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    // 예시 데이터
    val weatherData = remember {
        WeatherData(
            temperature = 23,
            humidity = 45,
            windSpeed = 3.2,
            rainChance = 10,
            airQuality = 55,
            airQualityStatus = "보통"
        )
    }

    val hikingTrails = remember {
        listOf(
            HikingTrail(
                id = 1,
                name = "한강 공원 산책로",
                distance = 2.5,
                duration = 30,
                difficulty = "쉬움",
                crowdness = "보통",
                rating = 4.5
            ),
            HikingTrail(
                id = 2,
                name = "남산 둘레길",
                distance = 4.2,
                duration = 60,
                difficulty = "중간",
                crowdness = "한산",
                rating = 4.8
            ),
            HikingTrail(
                id = 3,
                name = "북한산 숲길",
                distance = 3.7,
                duration = 45,
                difficulty = "중간",
                crowdness = "혼잡",
                rating = 4.2
            )
        )
    }

    // 현재 바텀 시트 상태에 따른 컨텐츠 표시 여부
    val isHalfExpanded = bottomSheetState.currentValue == SheetValue.PartiallyExpanded
    val isFullyExpanded = bottomSheetState.currentValue == SheetValue.Expanded

    Box(modifier = Modifier.fillMaxSize()) {
        // 지도 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp) // TopBar 높이
                .background(Color(0xFFE3F2FD))
                .clickable {
                    // 지도 클릭 시 바텀 시트 축소 및 검색창 닫기
                    scope.launch {
                        bottomSheetState.partialExpand()
                    }
                    isSearchActive = false
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "지도 영역 - 클릭하면 전체 지도가 보입니다",
                color = Color.Gray
            )
        }

        // 상단 바
        TopBar(
            weatherData = weatherData,
            isSearchActive = isSearchActive,
            searchQuery = searchQuery,
            onSearchToggle = { isSearchActive = !isSearchActive },
            onSearchQueryChange = { searchQuery = it },
            onClearSearch = { searchQuery = "" }
        )

        // 검색 결과
        if (isSearchActive && searchQuery.isNotEmpty()) {
            SearchResults(
                query = searchQuery,
                trails = hikingTrails.filter { it.name.contains(searchQuery) },
                onTrailSelected = { trail ->
                    selectedTrail = trail
                    scope.launch {
                        bottomSheetState.expand()
                    }
                    isSearchActive = false
                }
            )
        }

        BottomSheet(
            sheetState = bottomSheetState,
            sheetContent = {
                BottomSheetContent(
                    isHalfExpanded = isHalfExpanded || isFullyExpanded,
                    isFullyExpanded = isFullyExpanded,
                    weatherData = weatherData,
                    hikingTrails = hikingTrails,
                    selectedTrail = selectedTrail,
                    onExpandSheet = {
                        scope.launch {
                            if (bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                bottomSheetState.expand()
                            } else {
                                bottomSheetState.partialExpand()
                            }
                        }
                    },
                    onCollapseSheet = {
                        scope.launch {
                            bottomSheetState.partialExpand()
                        }
                    },
                    onTrailSelect = { trail ->
                        selectedTrail = trail
                        scope.launch {
                            bottomSheetState.expand()
                        }
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    sheetContent: @Composable () -> Unit
) {
    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                sheetContent()
            }
        },
        sheetPeekHeight = 160.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShadowElevation = 8.dp,
        contentColor = MaterialTheme.colorScheme.onBackground,
        // 빈 컨텐츠 - 이미 Box에 내용이 있으므로 여기선 필요없음
        content = {}
    )
}

@Composable
fun TopBar(
    weatherData: WeatherData,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        if (isSearchActive) {
            // 검색 모드 UI
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "산책로, 장소 검색...",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = onClearSearch,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "지우기",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onSearchToggle,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "취소",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            // 기본 상단 바 UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "위치",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "서울시 용산구",
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 미세먼지 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "미세먼지",
                            tint = getAirQualityColor(weatherData.airQualityStatus),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "미세먼지 ${weatherData.airQualityStatus}",
                            color = getAirQualityColor(weatherData.airQualityStatus),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 온도 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "온도",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${weatherData.temperature}°C",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 검색 버튼
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .clickable(onClick = onSearchToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResults(
    query: String,
    trails: List<HikingTrail>,
    onTrailSelected: (HikingTrail) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp), // TopBar 아래에 위치
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 256.dp)
        ) {
            item {
                Text(
                    text = "\"$query\"에 대한 검색 결과",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(trails) { trail ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrailSelected(trail) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = trail.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${trail.distance}km • ${trail.duration}분 소요",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                if (trails.indexOf(trail) < trails.size - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    isHalfExpanded: Boolean,
    isFullyExpanded: Boolean,
    weatherData: WeatherData,
    hikingTrails: List<HikingTrail>,
    selectedTrail: HikingTrail?,
    onExpandSheet: () -> Unit,
    onCollapseSheet: () -> Unit,
    onTrailSelect: (HikingTrail) -> Unit
) {
    // 드래그 핸들 표시
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 1단계: 기본 정보 (항상 표시)
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "오늘의 대기 상태",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "미세먼지 ${weatherData.airQualityStatus} (${weatherData.airQuality}㎍/㎥)",
                            style = MaterialTheme.typography.bodyLarge,
                            color = getAirQualityColor(weatherData.airQualityStatus),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${weatherData.temperature}°C",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "습도",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${weatherData.humidity}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "바람",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${weatherData.windSpeed}m/s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onExpandSheet,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "근처 산책로 보기",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // 2단계: 산책로 목록 (HalfExpanded 또는 FullyExpanded 상태일 때)
        if (isHalfExpanded) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "추천 산책로",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(hikingTrails) { trail ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { onTrailSelect(trail) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9F9F9)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = trail.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${trail.distance}km",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${trail.duration}분",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = trail.crowdness,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = trail.rating.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3단계: 산책로 상세 정보 (FullyExpanded 상태이고 선택된 산책로가 있을 때)
        if (isFullyExpanded && selectedTrail != null) {
            item {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .clickable(onClick = onCollapseSheet),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "뒤로가기",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "목록으로 돌아가기",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = selectedTrail.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = selectedTrail.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Text(
                            text = " • ",
                            color = Color.Gray
                        )

                        Text(
                            text = selectedTrail.difficulty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Text(
                            text = " • ",
                            color = Color.Gray
                        )

                        Text(
                            text = "${selectedTrail.distance}km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "코스 정보",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "고도 변화 그래프",
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "현재 상태",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "혼잡도",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = selectedTrail.crowdness,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        Column {
                            Text(
                                text = "미세먼지 노출",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = weatherData.airQualityStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = getAirQualityColor(weatherData.airQualityStatus)
                            )
                        }

                        Column {
                            Text(
                                text = "주변 시설",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "화장실, 벤치",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { /* 안내 시작 로직 */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "안내 시작하기",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}