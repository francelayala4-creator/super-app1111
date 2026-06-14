package com.superapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.superapp.data.api.dto.HistoryDto
import com.superapp.data.repository.HistoryRepository
import com.superapp.ui.components.EmptyBlock
import com.superapp.ui.components.LoadingBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(private val repo: HistoryRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<HistoryDto>?>(null)
    val items = _items.asStateFlow()
    fun load() { viewModelScope.launch { _items.value = runCatching { repo.history() }.getOrDefault(emptyList()) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(nav: NavHostController, vm: HistoryVM = hiltViewModel()) {
    val items by vm.items.collectAsState()
    LaunchedEffect(Unit) { vm.load() }
    Scaffold(topBar = { TopAppBar(title = { Text("Historial") }) }) { p ->
        when {
            items == null -> LoadingBlock()
            items!!.isEmpty() -> EmptyBlock("Sin historial", "Aquí verás tus cotizaciones anteriores.")
            else -> LazyColumn(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items!!) { h ->
                    Surface(shape = MaterialTheme.shapes.large, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Estrategia: ${h.strategy}", style = MaterialTheme.typography.titleMedium)
                            Text(h.created_at, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${h.results_count} resultados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
