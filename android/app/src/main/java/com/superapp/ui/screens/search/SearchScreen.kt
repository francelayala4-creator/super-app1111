package com.superapp.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.screens.list.ListsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    nav: NavHostController,
    listId: String,
    vm: SearchViewModel = hiltViewModel(),
    listsVm: ListsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Agregar productos") }) }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; vm.search(it) },
                label = { Text("Buscar producto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.results) { product ->
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                listsVm.addItem(listId, product.name, productId = product.id)
                                nav.popBackStack()
                            }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                listOfNotNull(product.brand, product.size?.let { "$it${product.unit ?: ""}" }).joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("¿No encuentras el producto?", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Nombre libre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            PrimaryCta("Agregar a la lista", enabled = customName.isNotBlank()) {
                if (customName.isNotBlank()) {
                    listsVm.addItem(listId, customName)
                    nav.popBackStack()
                }
            }
        }
    }
}
