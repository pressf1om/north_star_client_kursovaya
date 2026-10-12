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
import com.northstar.app.presentation.admin.AdminMoreScreen
import com.northstar.app.presentation.admin.NewRouteScreen
import com.northstar.app.presentation.admin.RoutesListScreen
import com.northstar.app.presentation.archive.ArchiveScreen
import com.northstar.app.presentation.dispatch.DispatchMessagesScreen
import com.northstar.app.presentation.dispatch.DispatchMoreScreen
import com.northstar.app.presentation.driver.DriverHelpScreen
import com.northstar.app.presentation.driver.DriverMoreScreen
import com.northstar.app.presentation.driver.DriverNotesScreen
import com.northstar.app.presentation.driver.DriverTripScreen
import com.northstar.app.presentation.fleet.FleetScreen
import com.northstar.app.presentation.fleet.NewApplicationScreen
import com.northstar.app.presentation.people.AssignRoleScreen
import com.northstar.app.presentation.people.PeopleScreen
import com.northstar.app.presentation.session.CurrentUser
import com.northstar.app.presentation.session.UserStatus

@Composable
fun MainShell(
    user: CurrentUser,
    onSignOut: () -> Unit,
    isDarkTheme: Boolean,
    onThemeClick: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
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
                    onThemeClick = onThemeClick,
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
                FleetScreen(
                    canAddCar = user.status == UserStatus.ADMIN,
                    onNewApplication = { navController.navigate(MainRoutes.NEW_APPLICATION) },
                    onThemeClick = onThemeClick,
                )
            }
            composable(MainRoutes.NEW_APPLICATION) {
                NewApplicationScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() },
                )
            }
            composable(MainRoutes.ADMIN_MORE) {
                AdminMoreScreen(
                    user = user,
                    onRoutesClick = { navController.navigate(MainRoutes.ADMIN_ROUTES) },
                    onThemeClick = onThemeClick,
                )
            }
            composable(MainRoutes.ADMIN_ROUTES) {
                RoutesListScreen(
                    onBack = { navController.popBackStack() },
                    onAddRoute = { navController.navigate(MainRoutes.ADMIN_NEW_ROUTE) },
                )
            }
            composable(MainRoutes.ADMIN_NEW_ROUTE) {
                NewRouteScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() },
                )
            }
            composable(MainRoutes.DISPATCH_FLEET) {
                FleetScreen(
                    canAddCar = false,
                    onNewApplication = { navController.navigate(MainRoutes.NEW_APPLICATION) },
                    onThemeClick = onThemeClick,
                )
            }
            composable(MainRoutes.DISPATCH_ARCHIVE) {
                ArchiveScreen(carNumber = null, onThemeClick = onThemeClick)
            }
            composable(MainRoutes.DISPATCH_MORE) {
                DispatchMoreScreen(
                    user = user,
                    onMessagesClick = { navController.navigate(MainRoutes.DISPATCH_MESSAGES) },
                    onThemeClick = onThemeClick,
                )
            }
            composable(MainRoutes.DISPATCH_MESSAGES) {
                DispatchMessagesScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(MainRoutes.DRIVER_TRIP) {
                DriverTripScreen(carNumber = user.carNumber)
            }
            composable(MainRoutes.DRIVER_ARCHIVE) {
                ArchiveScreen(carNumber = user.carNumber, onThemeClick = onThemeClick)
            }
            composable(MainRoutes.DRIVER_MORE) {
                DriverMoreScreen(
                    user = user,
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onHelpClick = { navController.navigate(MainRoutes.DRIVER_HELP) },
                    onNotesClick = { navController.navigate(MainRoutes.DRIVER_NOTES) },
                    onSignOut = onSignOut,
                    onThemeClick = onThemeClick,
                )
            }
            composable(MainRoutes.DRIVER_NOTES) {
                DriverNotesScreen(
                    carNumber = user.carNumber,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(MainRoutes.DRIVER_HELP) {
                DriverHelpScreen(
                    carNumber = user.carNumber,
                    onBack = { navController.popBackStack() },
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
