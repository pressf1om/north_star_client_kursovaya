package com.northstar.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.northstar.app.presentation.auth.RegisterScreen
import com.northstar.app.presentation.auth.SignInScreen
import com.northstar.app.presentation.auth.WaitingScreen
import com.northstar.app.presentation.main.MainShell
import com.northstar.app.presentation.session.SessionStatus
import com.northstar.app.presentation.session.SessionViewModel
import com.northstar.app.presentation.theme.ThemeViewModel
import com.northstar.app.ui.theme.NorthStarTheme

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    NorthStarTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val sessionStatus by sessionViewModel.status.collectAsStateWithLifecycle()
            val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()

            when (sessionStatus) {
                SessionStatus.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                SessionStatus.SignedOut -> {
                    val authNavController = rememberNavController()
                    NavHost(
                        navController = authNavController,
                        startDestination = AuthRoutes.SIGN_IN,
                    ) {
                        composable(AuthRoutes.SIGN_IN) {
                            SignInScreen(
                                onNavigateToRegister = {
                                    authNavController.navigate(AuthRoutes.REGISTER)
                                },
                                onSignedIn = sessionViewModel::onSignedIn,
                            )
                        }
                        composable(AuthRoutes.REGISTER) {
                            RegisterScreen(
                                onNavigateBack = { authNavController.popBackStack() },
                                onRegistered = sessionViewModel::onSignedIn,
                            )
                        }
                    }
                }

                SessionStatus.SignedIn -> {
                    val user = currentUser
                    if (user == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (user.isPending) {
                        WaitingScreen(onSignOut = sessionViewModel::signOut)
                    } else {
                        MainShell(
                            user = user,
                            onSignOut = sessionViewModel::signOut,
                            isDarkTheme = isDarkTheme,
                            onThemeClick = themeViewModel::toggleTheme,
                            onDarkThemeChange = themeViewModel::setDarkTheme,
                        )
                    }
                }
            }
        }
    }
}
