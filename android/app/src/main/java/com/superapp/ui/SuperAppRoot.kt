package com.superapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.superapp.ui.navigation.NavGraph
import com.superapp.ui.theme.SuperAppTheme

@Composable
fun SuperAppRoot() {
    SuperAppTheme {
        val nav = rememberNavController()
        NavGraph(nav)
    }
}
