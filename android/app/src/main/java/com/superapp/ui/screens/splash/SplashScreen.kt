package com.superapp.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.superapp.ui.components.AnimatedCartHero
import com.superapp.ui.navigation.Dest
import com.superapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavHostController, vm: SplashViewModel = hiltViewModel()) {
    val isLogged by vm.isLogged.collectAsState()
    var titleAlpha by remember { mutableStateOf(0f) }
    val a by animateFloatAsState(targetValue = titleAlpha, animationSpec = tween(700), label = "titleAlpha")

    LaunchedEffect(Unit) {
        titleAlpha = 1f
        delay(1600)
        vm.checkAuth()
    }
    LaunchedEffect(isLogged) {
        when (isLogged) {
            true -> nav.navigate(Dest.HOME) { popUpTo(Dest.SPLASH) { inclusive = true } }
            false -> nav.navigate(Dest.ONBOARDING) { popUpTo(Dest.SPLASH) { inclusive = true } }
            null -> Unit
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Purple900, Purple600, Coral.copy(alpha = 0.85f)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedCartHero(primary = Color.White, accent = Champagne, size = 240.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                "SUPERAPP",
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.alpha(a),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Compara. Ahorra.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.78f),
                modifier = Modifier.alpha(a),
            )
        }
    }
}
