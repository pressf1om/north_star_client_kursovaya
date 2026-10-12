package com.northstar.app.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.northstar.app.MainRoutes
import com.northstar.app.R
import com.northstar.app.presentation.components.BottomTab
import com.northstar.app.presentation.components.MainBottomBar
import com.northstar.app.presentation.people.AssignRoleScreen
import com.northstar.app.presentation.people.PeopleScreen
import com.northstar.app.presentation.session.CurrentUser
import com.northstar.app.presentation.session.UserStatus

@Composable
fun MainShell(
    user: CurrentUser,
    @Suppress("UNUSED_PARAMETER") onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startRoute(user)

    val tabs = tabsFor(user)
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    tabs = tabs,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startRoute(user),
            modifier = Modifier.padding(padding),
        ) {
            composable(MainRoutes.ADMIN_PEOPLE) {
                PeopleScreen(
                    onAssignRole = { userId ->
                        navController.navigate(MainRoutes.adminAssignRole(userId))
                    },
                )
            }
            composable(
                route = MainRoutes.ADMIN_ASSIGN_ROLE,
                arguments = listOf(navArgument("userId") { type = NavType.IntType }),
            ) {
                AssignRoleScreen(
                    onBack = { navController.popBackStack() },
                    onAssigned = {
                        navController.popBackStack()
                    },
                )
            }
            composable(MainRoutes.ADMIN_FLEET) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_fleet),
                    subtitle = stringResource(R.string.tab_placeholder),
                )
            }
            composable(MainRoutes.ADMIN_MORE) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_more),
                    subtitle = user.username,
                )
            }
            composable(MainRoutes.DISPATCH_FLEET) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_fleet),
                    subtitle = stringResource(R.string.tab_placeholder),
                )
            }
            composable(MainRoutes.DISPATCH_ARCHIVE) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_archive),
                    subtitle = stringResource(R.string.tab_placeholder),
                )
            }
            composable(MainRoutes.DISPATCH_MORE) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_more),
                    subtitle = user.username,
                )
            }
            composable(MainRoutes.DRIVER_TRIP) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_trip),
                    subtitle = user.carNumber ?: stringResource(R.string.tab_placeholder),
                )
            }
            composable(MainRoutes.DRIVER_ARCHIVE) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_archive),
                    subtitle = stringResource(R.string.tab_placeholder),
                )
            }
            composable(MainRoutes.DRIVER_MORE) {
                TabPlaceholderScreen(
                    title = stringResource(R.string.tab_more),
                    subtitle = user.username,
                )
            }
        }
    }
}

private fun startRoute(user: CurrentUser): String = when (user.status) {
    UserStatus.ADMIN -> MainRoutes.ADMIN_PEOPLE
    UserStatus.DISPATCHER -> MainRoutes.DISPATCH_FLEET
    UserStatus.DRIVER -> MainRoutes.DRIVER_TRIP
    else -> MainRoutes.ADMIN_PEOPLE
}

private fun tabsFor(user: CurrentUser): List<BottomTab> = when (user.status) {
    UserStatus.ADMIN -> listOf(
        BottomTab(MainRoutes.ADMIN_PEOPLE, "Люди"),
        BottomTab(MainRoutes.ADMIN_FLEET, "Парк"),
        BottomTab(MainRoutes.ADMIN_MORE, "Ещё"),
    )
    UserStatus.DISPATCHER -> listOf(
        BottomTab(MainRoutes.DISPATCH_FLEET, "Парк"),
        BottomTab(MainRoutes.DISPATCH_ARCHIVE, "Архив"),
        BottomTab(MainRoutes.DISPATCH_MORE, "Ещё"),
    )
    UserStatus.DRIVER -> listOf(
        BottomTab(MainRoutes.DRIVER_TRIP, "Рейс"),
        BottomTab(MainRoutes.DRIVER_ARCHIVE, "Архив"),
        BottomTab(MainRoutes.DRIVER_MORE, "Ещё"),
    )
    else -> listOf(
        BottomTab(MainRoutes.ADMIN_PEOPLE, "Люди"),
        BottomTab(MainRoutes.ADMIN_FLEET, "Парк"),
        BottomTab(MainRoutes.ADMIN_MORE, "Ещё"),
    )
}
