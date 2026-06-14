package com.superapp.ui.navigation

import java.net.URLEncoder

object Dest {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val PERMISSION = "permission"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val NEW_LIST = "newList"
    const val LIST = "list/{listId}"
    fun list(listId: String) = "list/$listId"
    const val SEARCH = "search/{listId}"
    fun search(listId: String) = "search/$listId"
    const val QUOTE = "quote/{listId}"
    fun quote(listId: String) = "quote/$listId"
    const val RESULTS = "results/{quoteId}"
    fun results(quoteId: String) = "results/$quoteId"
    const val DETAIL = "detail/{quoteId}/{resultId}"
    fun detail(quoteId: String, resultId: String) = "detail/$quoteId/$resultId"
    const val ROUTE = "route/{lat}/{lng}/{name}"
    fun route(lat: Double, lng: Double, name: String) =
        "route/$lat/$lng/${URLEncoder.encode(name, "UTF-8")}"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val ALERTS = "alerts"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
}
