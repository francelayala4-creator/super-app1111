package com.superapp.ui.screens.quote

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.LoadingBlock
import com.superapp.ui.navigation.Dest

@Composable
fun QuoteLoadingScreen(nav: NavHostController, listId: String, vm: QuoteViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    LaunchedEffect(listId) { vm.run(listId) }
    LaunchedEffect(state.quoteResponse) {
        val q = state.quoteResponse
        if (q != null) {
            nav.navigate(Dest.results(q.quote_id)) { popUpTo(Dest.QUOTE) { inclusive = true } }
        }
    }
    if (state.error != null) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("No pudimos cotizar", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { vm.run(listId) }) { Text("Reintentar") }
        }
    } else {
        LoadingBlock("Comparando entre cadenas…")
    }
}
