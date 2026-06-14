package com.superapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.superapp.data.repository.AuthRepository
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.components.SecondaryCta
import com.superapp.ui.navigation.Dest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    fun logout(onDone: () -> Unit) { viewModelScope.launch { auth.logout(); onDone() } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(nav: NavHostController, vm: ProfileVM = hiltViewModel()) {
    Scaffold(topBar = { TopAppBar(title = { Text("Perfil") }) }) { p ->
        Column(Modifier.padding(p).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryCta("Historial") { nav.navigate(Dest.HISTORY) }
            SecondaryCta("Favoritos") { nav.navigate(Dest.FAVORITES) }
            SecondaryCta("Alertas") { nav.navigate(Dest.ALERTS) }
            SecondaryCta("Ajustes") { nav.navigate(Dest.SETTINGS) }
            Spacer(Modifier.weight(1f))
            PrimaryCta("Cerrar sesión") { vm.logout { nav.navigate(Dest.SPLASH) { popUpTo(0) } } }
        }
    }
}
