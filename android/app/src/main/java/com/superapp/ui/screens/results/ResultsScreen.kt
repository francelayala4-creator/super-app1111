package com.superapp.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.*
import com.superapp.ui.navigation.Dest
import com.superapp.ui.screens.quote.QuoteViewModel
import com.superapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(nav: NavHostController, quoteId: String, vm: QuoteViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    LaunchedEffect(quoteId) { vm.loadById(quoteId) }
    var strategyTab by remember { mutableStateOf(2) }
    val quote = state.quoteResponse
    val results = quote?.results ?: emptyList()
    val sorted = when (strategyTab) {
        0 -> results.sortedBy { it.total }
        1 -> results.sortedBy { it.distance_km ?: Double.MAX_VALUE }
        else -> results.sortedByDescending { it.score }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resultados", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { p ->
        Column(Modifier.padding(p)) {
            ScrollableTabRow(
                selectedTabIndex = strategyTab,
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {},
            ) {
                listOf("💰 Más barato", "📍 Más cercano", "⚖️ Mejor balance").forEachIndexed { i, label ->
                    Tab(selected = strategyTab == i, onClick = { strategyTab = i },
                        text = { Text(label, style = MaterialTheme.typography.titleMedium) })
                }
            }

            // Mapa OSM con markers reales en cada sucursal
            Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Surface(Modifier.fillMaxWidth().height(200.dp),
                    shape = MaterialTheme.shapes.extraLarge, shadowElevation = 6.dp) {
                    OsmMap(
                        centerLat = sorted.firstOrNull()?.store_lat ?: 20.5888,
                        centerLng = sorted.firstOrNull()?.store_lng ?: -100.3899,
                        zoom = 12.0,
                        markers = sorted.mapNotNull { r ->
                            val lat = r.store_lat ?: return@mapNotNull null
                            val lng = r.store_lng ?: return@mapNotNull null
                            OsmMarker(lat = lat, lng = lng,
                                title = "${r.chain_name} · $${"%.0f".format(r.total)}",
                                subtitle = r.store_name)
                        }
                    )
                }
            }

            when {
                state.loading && results.isEmpty() -> LoadingBlock("Cargando resultados…")
                state.error != null -> ErrorBlock(state.error!!) { vm.loadById(quoteId) }
                results.isEmpty() -> EmptyBlock("Sin resultados", "No encontramos cadenas para esta lista.")
                else -> LazyColumn(
                    Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(sorted) { r ->
                        val accent = chainColorByName(r.chain_name)
                        ChainResultCard(r, accent) {
                            nav.navigate(Dest.detail(quoteId, r.id))
                        }
                    }
                }
            }
        }
    }
}

private fun chainColorByName(name: String): Color {
    val n = name.lowercase()
    return when {
        "walmart" in n -> ChainWalmart
        "soriana" in n -> ChainSoriana
        "chedraui" in n -> ChainChedraui
        "h-e-b" in n || "heb" in n -> ChainHeb
        "comer" in n -> ChainComer
        "costco" in n -> ChainCostco
        "sam" in n -> ChainSams
        "aurrera" in n || "bodega" in n -> ChainAurrera
        else -> Purple500
    }
}

@Composable
private fun ChainResultCard(
    r: com.superapp.data.api.dto.QuoteResultDto,
    accent: Color,
    onClick: () -> Unit,
) {
    Surface(
        Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.75f)))
                ).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(48.dp).background(Color.White.copy(alpha = 0.25f), MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(r.chain_name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium, color = Color.White)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.chain_name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text(r.store_name ?: "Sucursal cercana",
                        style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
                Box(Modifier.background(Color.White, MaterialTheme.shapes.medium).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("#${r.rank}", style = MaterialTheme.typography.titleLarge, color = accent)
                }
            }
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(Modifier.weight(1f)) {
                        Text("Total", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$" + "%.2f".format(r.total),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    if ((r.savings_vs_max ?: 0.0) > 0) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Ahorras", style = MaterialTheme.typography.bodyMedium, color = SaveTeal)
                            Text("$" + "%.0f".format(r.savings_vs_max),
                                style = MaterialTheme.typography.headlineMedium, color = SaveTeal)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("${r.items_found}/${r.items_found + r.items_missing} ✓",
                        bg = MaterialTheme.colorScheme.secondaryContainer,
                        fg = MaterialTheme.colorScheme.onSecondaryContainer)
                    r.distance_km?.let {
                        Pill("📍 ${"%.1f".format(it)} km",
                            bg = MaterialTheme.colorScheme.primaryContainer,
                            fg = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    r.eta_minutes?.let {
                        Pill("⏱️ ${"%.0f".format(it)} min",
                            bg = MaterialTheme.colorScheme.tertiaryContainer,
                            fg = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }
    }
}
