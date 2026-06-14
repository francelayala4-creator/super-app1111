package com.superapp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.superapp.ui.screens.alerts.AlertsScreen
import com.superapp.ui.screens.common.LoginScreen
import com.superapp.ui.screens.common.RegisterScreen
import com.superapp.ui.screens.detail.ChainDetailScreen
import com.superapp.ui.screens.favorites.FavoritesScreen
import com.superapp.ui.screens.history.HistoryScreen
import com.superapp.ui.screens.home.HomeScreen
import com.superapp.ui.screens.list.ListEditorScreen
import com.superapp.ui.screens.list.NewListScreen
import com.superapp.ui.screens.onboarding.OnboardingScreen
import com.superapp.ui.screens.permission.PermissionScreen
import com.superapp.ui.screens.profile.ProfileScreen
import com.superapp.ui.screens.quote.QuoteLoadingScreen
import com.superapp.ui.screens.results.ResultsScreen
import com.superapp.ui.screens.route.RouteScreen
import com.superapp.ui.screens.search.SearchScreen
import com.superapp.ui.screens.settings.SettingsScreen
import com.superapp.ui.screens.splash.SplashScreen

private val FAST = tween<Float>(280)
private val MED  = tween<androidx.compose.ui.unit.IntOffset>(420)

@Composable
fun NavGraph(nav: NavHostController) {
    NavHost(
        navController = nav,
        startDestination = Dest.SPLASH,
        enterTransition = { slideIntoContainer(SlideDirection.Start, MED) + fadeIn(FAST) },
        exitTransition  = { slideOutOfContainer(SlideDirection.Start, MED) + fadeOut(FAST) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, MED) + fadeIn(FAST) },
        popExitTransition  = { slideOutOfContainer(SlideDirection.End, MED) + fadeOut(FAST) },
    ) {
        composable(Dest.SPLASH,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition  = { fadeOut(tween(400)) }
        ) { SplashScreen(nav) }
        composable(Dest.ONBOARDING) { OnboardingScreen(nav) }
        composable(Dest.PERMISSION) { PermissionScreen(nav) }
        composable(Dest.LOGIN) { LoginScreen(nav) }
        composable(Dest.REGISTER) { RegisterScreen(nav) }
        composable(Dest.HOME,
            enterTransition = { fadeIn(tween(500)) + scaleIn(initialScale = 0.94f, animationSpec = tween(500)) }
        ) { HomeScreen(nav) }
        composable(Dest.NEW_LIST) { NewListScreen(nav) }
        composable(Dest.LIST, arguments = listOf(navArgument("listId") { type = NavType.StringType })) {
            ListEditorScreen(nav, it.arguments?.getString("listId") ?: "")
        }
        composable(Dest.SEARCH, arguments = listOf(navArgument("listId") { type = NavType.StringType })) {
            SearchScreen(nav, it.arguments?.getString("listId") ?: "")
        }
        composable(Dest.QUOTE, arguments = listOf(navArgument("listId") { type = NavType.StringType })) {
            QuoteLoadingScreen(nav, it.arguments?.getString("listId") ?: "")
        }
        composable(Dest.RESULTS,
            arguments = listOf(navArgument("quoteId") { type = NavType.StringType }),
            enterTransition = { slideIntoContainer(SlideDirection.Up, MED) + fadeIn(FAST) },
            popExitTransition = { slideOutOfContainer(SlideDirection.Down, MED) + fadeOut(FAST) }
        ) {
            ResultsScreen(nav, it.arguments?.getString("quoteId") ?: "")
        }
        composable(Dest.DETAIL, arguments = listOf(
            navArgument("quoteId") { type = NavType.StringType },
            navArgument("resultId") { type = NavType.StringType },
        )) {
            ChainDetailScreen(
                nav,
                quoteId = it.arguments?.getString("quoteId") ?: "",
                resultId = it.arguments?.getString("resultId") ?: "",
            )
        }
        composable(Dest.ROUTE, arguments = listOf(
            navArgument("lat") { type = NavType.StringType },
            navArgument("lng") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType },
        )) {
            RouteScreen(
                nav,
                it.arguments?.getString("lat")?.toDouble() ?: 0.0,
                it.arguments?.getString("lng")?.toDouble() ?: 0.0,
                java.net.URLDecoder.decode(it.arguments?.getString("name") ?: "", "UTF-8")
            )
        }
        composable(Dest.HISTORY) { HistoryScreen(nav) }
        composable(Dest.FAVORITES) { FavoritesScreen(nav) }
        composable(Dest.ALERTS) { AlertsScreen(nav) }
        composable(Dest.PROFILE) { ProfileScreen(nav) }
        composable(Dest.SETTINGS) { SettingsScreen(nav) }
    }
}
