package com.superapp.ui.screens.permission

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.components.SecondaryCta
import com.superapp.ui.navigation.Dest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(nav: NavHostController) {
    val perm = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val granted = perm.status is PermissionStatus.Granted
    Column(Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        Spacer(Modifier.height(40.dp))
        Text("Para comparar mejor, necesitamos tu ubicación", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Usamos tu posición sólo para calcular distancias y ETA hacia las cadenas.",
            style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        PrimaryCta(if (granted) "Continuar" else "Permitir ubicación") {
            if (granted) nav.navigate(Dest.HOME) { popUpTo(Dest.PERMISSION) { inclusive = true } }
            else perm.launchPermissionRequest()
        }
        Spacer(Modifier.height(12.dp))
        SecondaryCta("Continuar sin ubicación") { nav.navigate(Dest.HOME) }
        Spacer(Modifier.height(12.dp))
    }
}
