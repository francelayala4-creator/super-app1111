package com.superapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.superapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavHostController, vm: HomeViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.load() }
    val centerLat = state.userLat ?: 20.5888
    val centerLng = state.userLng ?: -100.3899

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Inicio") })
                NavigationBarItem(selected = false, onClick = { nav.navigate(Dest.FAVORITES) }, icon = { Icon(Icons.Outlined.FavoriteBorder, null) }, label = { Text("Favoritos") })
                NavigationBarItem(selected = false, onClick = { nav.navigate(Dest.ALERTS) }, icon = { Icon(Icons.Outlined.NotificationsNone, null) }, label = { Text("Alertas") })
                NavigationBarItem(selected = false, onClick = { nav.navigate(Dest.SETTINGS) }, icon = { Icon(Icons.Outlined.Settings, null) }, label = { Text("Ajustes") })
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { nav.navigate(Dest.NEW_LIST) },
                icon = { Icon(Icons.Outlined.Add, null) },
                text = { Text("Nueva lista") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
            AnimatedVisibility(
                visible = appeared,
                enter = slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(500)) + fadeIn(tween(500))
            ) {
                Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Surface(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge, shadowElevation = 12.dp) {
                        Column(
                            Modifier.background(Brush.linearGradient(listOf(Purple700, Purple500, Coral))).padding(24.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Buen día", style = MaterialTheme.typography.titleLarge, color = Color.White.copy(alpha = 0.9f))
                                    Spacer(Modifier.height(4.dp))
                                    Text("Comparemos tu lista", style = MaterialTheme.typography.displayLarge, color = Color.White)
                                }
                                IconButton(onClick = { nav.navigate(Dest.PROFILE) }) {
                                    Icon(Icons.Outlined.AccountCircle, null, tint = Color.White)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Hasta 15% de ahorro comparando 8 cadenas en Querétaro.",
                                style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.92f))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = appeared,
                enter = slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(600, delayMillis = 120)) + fadeIn(tween(600, delayMillis = 120))
            ) {
                Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Surface(Modifier.fillMaxWidth().height(220.dp), shape = MaterialTheme.shapes.extraLarge, shadowElevation = 8.dp) {
                        OsmMap(
                            centerLat = centerLat, centerLng = centerLng, zoom = 12.5,
                            markers = state.stores.map { OsmMarker(it.latitude, it.longitude, it.name, it.address) }
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconPill(Icons.Outlined.Storefront, "${state.stores.size} sucursales",
                    MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                IconPill(Icons.Outlined.ChecklistRtl, "${state.lists.size} listas",
                    MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
            }

            Text("Tus listas", style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = appeared,
                enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(700, delayMillis = 240)) + fadeIn(tween(700, delayMillis = 240))
            ) {
                if (state.lists.isEmpty()) {
                    PremiumCard(Modifier.padding(horizontal = 16.dp)) {
                        Text("Aún no tienes listas", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text("Crea tu primera lista para empezar a comparar precios.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        PrimaryCta("Crear lista") { nav.navigate(Dest.NEW_LIST) }
                    }
                } else {
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.lists) { l ->
                            Surface(Modifier.width(260.dp), shape = MaterialTheme.shapes.extraLarge, shadowElevation = 8.dp) {
                                Column(Modifier.background(Brush.linearGradient(listOf(Purple100, Cream50))).padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.ShoppingBasket, null, tint = Purple700)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Lista de compra", style = MaterialTheme.typography.labelMedium, color = Purple700)
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    Text(l.name, style = MaterialTheme.typography.headlineMedium, maxLines = 1, color = Purple900)
                                    Spacer(Modifier.height(4.dp))
                                    Text("${l.items.size} productos", color = InkSoft, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AssistChip(onClick = { nav.navigate(Dest.list(l.id)) }, label = { Text("Editar") })
                                        Button(
                                            onClick = { nav.navigate(Dest.quote(l.id)) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Purple600, contentColor = Color.White),
                                            shape = MaterialTheme.shapes.medium,
                                        ) { Text("Cotizar") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun IconPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, bg: Color, fg: Color) {
    Surface(shape = MaterialTheme.shapes.large, color = bg) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, color = fg)
        }
    }
}
