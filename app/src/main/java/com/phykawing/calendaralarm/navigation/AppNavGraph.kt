package com.phykawing.calendaralarm.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.phykawing.calendaralarm.ui.eventdetail.EventDetailScreen
import com.phykawing.calendaralarm.ui.events.EventListScreen
import com.phykawing.calendaralarm.ui.settings.SettingsScreen

object Routes {
    const val EVENT_LIST = "event_list"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val SETTINGS = "settings"

    fun eventDetail(eventId: Long) = "event_detail/$eventId"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.EVENT_LIST
    ) {
        composable(Routes.EVENT_LIST) {
            EventListScreen(
                onEventClick = { eventId ->
                    navController.navigate(Routes.eventDetail(eventId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) {
            EventDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
