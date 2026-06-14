package com.superapp.ui.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewListScreen(nav: NavHostController, vm: ListsViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("Compra semanal") }
    val state by vm.create.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Nueva lista") }) }) { p ->
        Column(Modifier.padding(p).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.weight(1f))
            PrimaryCta("Crear lista", enabled = !state.loading) {
                vm.createNew(name) { id ->
                    nav.navigate(Dest.list(id)) { popUpTo(Dest.NEW_LIST) { inclusive = true } }
                }
            }
            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
