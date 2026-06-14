package com.superapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
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
fun ChainDetailScreen(
    nav: NavHostController,
    quoteId: String,
    resultId: String,
    vm: QuoteViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    LaunchedEffect(quoteId) { vm.loadById(quoteId) }
    val result = state.quoteResponse?.results?.firstOrNull { it.id == resultId }
    val accent = chainColorByName(result?.chain_name ?: "")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(result?.chain_name ?: "Detalle",
                    style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { p ->
        when {
            state.loading && result == null -> LoadingBlock("Cargando detalle…")
            result == null -> EmptyBlock("Sin detalle", "Vuelve a la pantalla anterior.")
            else -> Column(Modifier.padding(p)) {
                // Hero header con identidad de la cadena
                Surface(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = 10.dp,
                ) {
                    Column(Modifier
                        .background(Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.7f))))
                        .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(56.dp)
                                .background(Color.White.copy(alpha = 0.25f), MaterialTheme.shapes.large),
                                contentAlignment = Alignment.Center) {
                                Text(result.chain_name.take(1).uppercase(),
                                    style = MaterialTheme.typography.displayLarge.copy(),
                                    color = Color.White)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(result.chain_name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                Text(result.store_name ?: "Sucursal cercana",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row {
                            Column(Modifier.weight(1f)) {
                                Text("Total", color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodyMedium)
                                Text("$" + "%.2f".format(result.total),
                                    style = MaterialTheme.typography.displayLarge, color = Color.White)
                            }
                            if ((result.savings_vs_max ?: 0.0) > 0) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Ahorras", color = Color.White.copy(alpha = 0.85f),
                                        style = MaterialTheme.typography.bodyMedium)
                                    Text("$" + "%.0f".format(result.savings_vs_max),
                                        style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Pill("${result.items_found}/${result.items_found + result.items_missing} ✓",
                                bg = Color.White.copy(alpha = 0.25f), fg = Color.White)
                            result.distance_km?.let {
                                Pill("📍 ${"%.1f".format(it)} km",
                                    bg = Color.White.copy(alpha = 0.25f), fg = Color.White)
                            }
                            result.eta_minutes?.let {
                                Pill("⏱️ ${"%.0f".format(it)} min",
                                    bg = Color.White.copy(alpha = 0.25f), fg = Color.White)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Map preview de la sucursal
                if (result.store_lat != null && result.store_lng != null) {
                    Surface(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(160.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        shadowElevation = 6.dp,
                    ) {
                        OsmMap(
                            centerLat = result.store_lat, centerLng = result.store_lng, zoom = 15.0,
                            markers = listOf(OsmMarker(result.store_lat, result.store_lng,
                                title = result.store_name, subtitle = result.store_address))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.padding(horizontal = 16.dp)) {
                        PrimaryCta("Cómo llegar 🚗") {
                            nav.navigate(Dest.route(result.store_lat, result.store_lng,
                                "${result.chain_name} · ${result.store_name ?: ""}"))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text("Productos en este súper",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(result.items) { item ->
                        Surface(
                            Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            shadowElevation = 4.dp,
                        ) {
                            Row(
                                Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.raw_name, style = MaterialTheme.typography.titleMedium)
                                    val statusText = when {
                                        !item.matched -> "❌ No encontrado"
                                        item.match_confidence < 0.86 -> "≈ Coincidencia probable (${"%.0f".format(item.match_confidence * 100)}%)"
                                        else -> "✓ Coincidencia confirmada"
                                    }
                                    val statusColor = when {
                                        !item.matched -> MaterialTheme.colorScheme.error
                                        item.match_confidence < 0.86 -> Amber
                                        else -> SaveTeal
                                    }
                                    Text(statusText, color = statusColor,
                                        style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        item.subtotal?.let { "$" + "%.2f".format(it) } ?: "—",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    item.unit_price?.let {
                                        Text("$${"%.2f".format(it)} c/u",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
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
