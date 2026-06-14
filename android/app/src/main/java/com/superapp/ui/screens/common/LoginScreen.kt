package com.superapp.ui.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Column(Modifier.fillMaxSize().padding(24.dp).statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("Bienvenido", style = MaterialTheme.typography.displayLarge)
        OutlinedTextField(value = state.email, onValueChange = vm::setEmail, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = state.password, onValueChange = vm::setPassword, label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.weight(1f))
        PrimaryCta("Entrar", enabled = !state.loading) {
            vm.login { nav.navigate(Dest.HOME) { popUpTo(Dest.SPLASH) { inclusive = true } } }
        }
        TextButton(onClick = { nav.navigate(Dest.REGISTER) }) { Text("Crear cuenta") }
    }
}
