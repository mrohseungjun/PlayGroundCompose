package com.ahmedapps.bankningappui.navermap.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val label: String,
    val icon: ImageVector
){
    object Home : NavItem("주변", Icons.Default.LocationOn)
    object Star : NavItem("저장", Icons.Default.Star)
    object Bus : NavItem("대중교통", Icons.Default.DirectionsBus)
    object Car : NavItem("내비게이션", Icons.Default.DirectionsCar)
    object Person : NavItem("My", Icons.Default.Person)
}

@Composable
fun BottomNavBar() {
    val navItems = listOf(
        NavItem.Home,
        NavItem.Star,
        NavItem.Bus,
        NavItem.Car,
        NavItem.Person
    )
    NavigationBar(
        containerColor = Color.White,
    ){
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = index == 0,
                onClick = {  }
            )
        }
    }
}