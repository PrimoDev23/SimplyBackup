package com.simplyteam.simplybackup.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.PathsConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.connection.ConnectionConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.PathsConfigurationView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConnectionConfigurationNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    connectionConfigurationViewModel: ConnectionConfigurationViewModel,
    pathsConfigurationViewModel: PathsConfigurationViewModel
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.ConnectionConfiguration.Route
    ) {
        composable(
            Screen.ConnectionConfiguration.Route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(200))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, tween(200))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, tween(200))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, tween(200))
            }
        ) {
            ConnectionConfigurationView(
                paddingValues = paddingValues,
                navController = navController,
                viewModel = connectionConfigurationViewModel
            )
        }

        composable(
            Screen.PathsConfiguration.Route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(200))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, tween(200))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right, tween(200))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, tween(200))
            }
        ) {
            PathsConfigurationView(
                paddingValues = paddingValues,
                viewModel = pathsConfigurationViewModel
            )
        }
    }
}