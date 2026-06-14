package com.superapp.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.superapp.ui.components.AnimatedCartHero
import com.superapp.ui.components.PrimaryCta
import com.superapp.ui.components.SecondaryCta
import com.superapp.ui.navigation.Dest
import com.superapp.ui.theme.*

@Composable
fun OnboardingScreen(nav: NavHostController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Brush.linearGradient(listOf(Purple700, Purple500, Coral))),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedCartHero(size = 240.dp)
        }

        Spacer(Modifier.height(36.dp))
        Text("Ahorra en cada compra.",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        Text(
            "Cotiza tu lista entre todas las cadenas, descubre la sucursal más conveniente y cuánto ahorras.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        PrimaryCta("Crear cuenta") { nav.navigate(Dest.REGISTER) }
        Spacer(Modifier.height(12.dp))
        SecondaryCta("Iniciar sesión") { nav.navigate(Dest.LOGIN) }
        Spacer(Modifier.height(24.dp))
    }
}
