package com.superapp.ui.screens.route

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.superapp.ui.components.OsmMap
import com.superapp.ui.components.OsmMarker
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.components.SecondaryCta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(nav: NavHostController, lat: Double, lng: Double, name: String) {
    val ctx = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Ruta a $name") }) }) { p ->
        Column(Modifier.padding(p)) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                OsmMap(
                    centerLat = lat, centerLng = lng, zoom = 14.0,
                    markers = listOf(OsmMarker(lat = lat, lng = lng, title = name))
                )
            }
            Column(Modifier.padding(16.dp)) {
                PrimaryCta("Iniciar viaje") { showPicker = true }
            }
        }
        if (showPicker) {
            ModalBottomSheet(onDismissRequest = { showPicker = false }) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Abrir con", style = MaterialTheme.typography.titleLarge)
                    SecondaryCta("Google Maps") {
                        startIntent(ctx, Uri.parse("google.navigation:q=$lat,$lng&mode=d"), "com.google.android.apps.maps"); showPicker = false
                    }
                    SecondaryCta("Waze") {
                        startIntent(ctx, Uri.parse("waze://?ll=$lat,$lng&navigate=yes"), "com.waze"); showPicker = false
                    }
                    SecondaryCta("Mapas (genérico)") {
                        startIntent(ctx, Uri.parse("geo:$lat,$lng?q=$lat,$lng($name)"), null); showPicker = false
                    }
                }
            }
        }
    }
}

private fun startIntent(ctx: android.content.Context, uri: Uri, pkg: String?) {
    val i = Intent(Intent.ACTION_VIEW, uri)
    if (pkg != null) i.setPackage(pkg)
    try { ctx.startActivity(i) } catch (e: Exception) {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
