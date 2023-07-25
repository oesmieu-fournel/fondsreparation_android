package com.ecosystem.mobile.reparation.ui.odata

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ecosystem.mobile.reparation.ui.odata.screens.*
import com.sap.cloud.mobile.odata.EntityValue

const val SETTINGS_SCREEN_ROUTE = "settings"

@Composable
fun ODataNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = EntitySetsDest.route,
        modifier = modifier
    ) {
        @Composable
        fun NavBackStackEntry.findMasterEntry(): NavBackStackEntry {
            val parentId = this.destination.parent?.id
            return remember {
                navController.backQueue
                    .findLast {
                        it.destination.parent?.id == parentId &&
                                it.destination.route?.endsWith(NAV_ENTITY_LIST, false) == true
                    } ?: this
            }
        }

        composable(route = EntitySetsDest.route) {
            EntitySetScreen(
                EntitySetScreenInfo.values().toList(),
                navController::navigateToEntityList,
            ) { navController.navigate(SETTINGS_SCREEN_ROUTE) }
        }

        composable(route = SETTINGS_SCREEN_ROUTE) {
            SettingsScreen(navigateUp = navController::navigateUp)
        }

        //EntitySets
        EntitySetScreenInfo.values().forEach {
            val entitySet = it.entitySet
            getEntityScreens(entitySet)?.apply {
                navigation(
                    startDestination = EntityNavigationCommands(entitySet).entityListNav.route,
                    route = entitySet.name
                ) {
                    composable(route = EntityNavigationCommands(entitySet).entityListNav.route) {
                        entityListScreen(
                            navigateToEdit = navController::navigateToEntityUpdate,
                            navigateToDetails = navController::navigateToEntityDetail,
                            navigateToAdd = {
                                navController.navigateToEntityCreation(
                                    entitySet
                                )
                            },
                            navigateToHome = {
                                navController.popBackStack(
                                    EntitySetsDest.route,
                                    false
                                )
                            },
                            navigateUp = navController::navigateUp,
                            viewModel = viewModel(
                                factory = ODataEntityViewModelFactory(
                                    LocalContext.current.applicationContext as Application,
                                    entitySet,
                                    getOrderByProperty(entitySet)
                                )
                            )
                        )
                    }
                    composable(route = EntityNavigationCommands(entitySet).entityCreationNav.route) { navBackStackEntry ->
                        entityEditScreen(
                            navigateUp = navController::navigateUp,
                            viewModel = viewModel(navBackStackEntry.findMasterEntry())
                        )
                    }
                    composable(
                        route = EntityNavigationCommands(entitySet).entityDetailsNav.route,
                        arguments = EntityNavigationCommands(entitySet).entityDetailsNav.arguments
                    ) { navBackStackEntry ->
                        entityDetailScreen(
                            onNavigateProperty = { master, navProp ->
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "master",
                                    value = master
                                )
                                navController.navigateToNavigatePropertyList(navProp)
                            },
                            navigateToEdit = navController::navigateToEntityUpdate,
                            navigateUp = navController::navigateUp,
                            //shared viewModel in list screen
                            viewModel = viewModel(navBackStackEntry.findMasterEntry())
                        )
                    }
                    composable(
                        route = EntityNavigationCommands(entitySet).entityEditNav.route,
                        arguments = EntityNavigationCommands(entitySet).entityEditNav.arguments
                    ) { navBackStackEntry ->
                        //customer editor
                        entityEditScreen(
                            navigateUp = navController::navigateUp,
                            viewModel = viewModel(navBackStackEntry.findMasterEntry())
                        )
                    }
                    composable(
                        route = EntityNavigationCommands(entitySet).toEntitiesNav.route,
                        arguments = EntityNavigationCommands(entitySet).toEntitiesNav.arguments
                    ) { navBackStackEntry ->
                        val parent =
                            navController.previousBackStackEntry?.savedStateHandle?.get<EntityValue>(
                                "master"
                            )
                        val navProperty =
                            navBackStackEntry.arguments?.getString(navigationPropertyNameArg)
                        entityListScreen(
                            navigateToEdit = navController::navigateToEntityUpdate,
                            navigateToDetails = navController::navigateToEntityDetail,
                            navigateToAdd = {
                                navController.navigateToEntityCreation(
                                    entitySet
                                )
                            },
                            navigateToHome = {
                                navController.popBackStack(
                                    EntitySetsDest.route,
                                    false
                                )
                            },
                            navigateUp = navController::navigateUp,
                            viewModel = viewModel(
                                factory = ODataEntityViewModelFactory(
                                    LocalContext.current.applicationContext as Application,
                                    entitySet,
                                    getOrderByProperty(entitySet),
                                    parent,
                                    navProperty,
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

