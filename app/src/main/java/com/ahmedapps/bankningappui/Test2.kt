package com.ahmedapps.bankningappui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirconControlScreen() {
    var targetTemp by remember { mutableStateOf(26) }
    val minTemp = 16
    val maxTemp = 30

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스탠드 에어컨", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { /* 전원 토글 */ }) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power")
                    }
                }
            )
        },
        containerColor = Color.White
    ) { p ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(p)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // 모드 선택
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("냉방", style = MaterialTheme.typography.bodyLarge)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }

            Spacer(Modifier.height(32.dp))

            // 온도 다이얼
            TemperatureDial(
                temperature = targetTemp,
                onValueChanged = { targetTemp = it },
                min = minTemp,
                max = maxTemp,
                modifier = Modifier
                    .size(280.dp)
                    .pointerInput(Unit) {
                        // 터치 드래그로 제어하고 싶다면 여기서 GestureDetector 처리
                    }
            )

            Spacer(Modifier.height(16.dp))
            Text("희망 온도", style = MaterialTheme.typography.bodyMedium)
            Text("$targetTemp°C", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))
            Row {
                IconButton(onClick = { if (targetTemp > minTemp) targetTemp-- }) {
                    Icon(Icons.Default.RemoveCircle, contentDescription = "감소", tint = Color.Gray, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.width(32.dp))
                IconButton(onClick = { if (targetTemp < maxTemp) targetTemp++ }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "증가", tint = Color.Gray, modifier = Modifier.size(48.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // 상태 정보
            Column(modifier = Modifier.fillMaxWidth()) {
                StatusRow("실내 온도", "25°C")
                StatusRow("실내 습도", "67%")
                StatusRow("종합청정도", "좋음", dotColor = Color(0xFF3276FF))
            }
        }
    }
}

@Composable
fun StatusRow(label: String, value: String, dotColor: Color? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        if (dotColor != null) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(dotColor, shape = CircleShape)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TemperatureDial(
    temperature: Int,
    onValueChanged: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    // 다이얼의 시작 각도와 총 sweep 각도
    val startAngle = 135f
    val totalAngle = 270f
    val sweepPerDegree = totalAngle / (max - min)

    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f * 0.8f
            val strokeWidth = 20f
            val center = this.center

            // 전체 도트형 원 (회색)
            drawArc(
                color = Color(0xFFE0E0E0),
                startAngle = startAngle,
                sweepAngle = totalAngle,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f),
                    cap = StrokeCap.Round
                ),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // 현재 온도까지 파란색 arc
            val sweep = (temperature - min) * sweepPerDegree
            drawArc(
                brush = Brush.sweepGradient(listOf(Color(0xFF3276FF), Color(0xFF5EC2FF))),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f),
                    cap = StrokeCap.Round
                ),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // 노브 (현재 위치에 흰색 원)
            val knobAngle = Math.toRadians((startAngle + sweep).toDouble())
            val knobX = center.x + radius * cos(knobAngle).toFloat()
            val knobY = center.y + radius * sin(knobAngle).toFloat()
            drawCircle(
                color = Color.White,
                radius = strokeWidth / 2,
                center = Offset(knobX, knobY),
                style = Fill
            )
        }
    }
}
