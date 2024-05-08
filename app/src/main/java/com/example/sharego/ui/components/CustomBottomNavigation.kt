package com.example.sharego.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomBottomNavigation() {
    val items = listOf(
        BottomNavItem.Search,
        BottomNavItem.Publish,
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    BottomNavigation {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = false,
                onClick = {}
            )
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: ImageVector) {
    object Search : BottomNavItem("Buscar", Icons.Filled.Search)
    object Publish : BottomNavItem("Publicar", Icons.Filled.Share)
    object Home : BottomNavItem("Inicio", Icons.Filled.Home)
    object Profile : BottomNavItem("Perfil", Icons.Filled.Person)
}