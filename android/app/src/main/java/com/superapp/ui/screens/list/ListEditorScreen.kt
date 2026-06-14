package com.superapp.ui.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListEditorScreen(nav: NavHostController, listId: String, vm: ListsViewModel = hiltViewModel()) {
    val state by vm.editor.collectAsState()
    LaunchedEffect(listId) { vm.loadEditor(listId) }
    Scaffold(
        topBar = { TopAppBar(title = { Text(state.list?.name ?: "Lista") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { nav.navigate(Dest.search(listId)) },
                icon = { Icon(Icons.Outlined.Add, null) }, text = { Text("Agregar") }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.list?.items ?: emptyList()) { item ->
                    Surface(shape = MaterialTheme.shapes.large, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.raw_name, style = MaterialTheme.typography.titleMedium)
                                Text("Cantidad: ${item.quantity}${item.unit?.let { " $it" } ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { item.id?.let { vm.removeItem(listId, it) } }) {
                                Icon(Icons.Outlined.Delete, null)
                            }
                        }
                    }
                }
            }
            PrimaryCta("Cotizar lista") { nav.navigate(Dest.quote(listId)) }
        }
    }
}
