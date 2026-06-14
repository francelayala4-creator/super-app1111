package com.superapp.ui.theme

import androidx.compose.ui.graphics.Color

// Brand: aurora purple — profundo, no neón
val Purple950 = Color(0xFF0F0822)
val Purple900 = Color(0xFF1B0F3D)
val Purple800 = Color(0xFF2A1859)
val Purple700 = Color(0xFF3D2380)
val Purple600 = Color(0xFF5B2EAA)
val Purple500 = Color(0xFF7A47D6)
val Purple400 = Color(0xFFA078E8)
val Purple300 = Color(0xFFC6ABF5)
val Purple200 = Color(0xFFE0CFFA)
val Purple100 = Color(0xFFF1E8FF)
val Purple50  = Color(0xFFFAF6FF)

// Acentos
val Champagne = Color(0xFFF0CC7A)   // total, savings highlight
val SaveTeal  = Color(0xFF0FB394)   // ahorro accent
val Coral     = Color(0xFFFF7A66)   // CTAs secundarios
val Amber     = Color(0xFFFFB347)   // promo / alert

// Surfaces (cream tinted para light, deep para dark)
val Cream100  = Color(0xFFFCF7F2)   // fondo light, cálido
val Cream200  = Color(0xFFF5EFE6)
val Cream50   = Color(0xFFFFFEFC)

val Night900  = Color(0xFF0A0517)
val Night800  = Color(0xFF120A2A)
val Night700  = Color(0xFF1E133D)

// Texto
val InkDark   = Color(0xFF1A0F3D)
val InkSoft   = Color(0xFF5B4E78)
val InkOnDark = Color(0xFFEFEAFA)

// Colores por cadena (para badges)
val ChainWalmart  = Color(0xFF0071CE)
val ChainSoriana  = Color(0xFFE60012)
val ChainChedraui = Color(0xFFE20613)
val ChainHeb      = Color(0xFFE2231A)
val ChainComer    = Color(0xFF0A2F70)
val ChainCostco   = Color(0xFF005DAA)
val ChainSams     = Color(0xFF0070BB)
val ChainAurrera  = Color(0xFFE6332A)

fun chainColor(slug: String?): Color = when (slug) {
    "walmart" -> ChainWalmart
    "soriana" -> ChainSoriana
    "chedraui" -> ChainChedraui
    "heb" -> ChainHeb
    "laComer" -> ChainComer
    "costco" -> ChainCostco
    "sams" -> ChainSams
    "bodegaAurrera" -> ChainAurrera
    else -> Purple500
}
