package com.superapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Ajustes") }) }) { p ->
        Column(Modifier.padding(p).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ListItem(headlineContent = { Text("Tema") }, supportingContent = { Text("Sigue el sistema") })
            ListItem(headlineContent = { Text("Versión") }, supportingContent = { Text("1.0.0") })
            ListItem(headlineContent = { Text("Acerca de SUPERAPP") },
                supportingContent = { Text("Compara precios entre supermercados y elige el mejor.") })
        }
    }
}
