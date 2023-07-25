package com.ecosystem.mobile.reparation.ui.odata

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.NavigationProperty

const val NAV_ENTITY_CREATION = "_entity_create"
const val NAV_ENTITY_UPDATE = "_entity_update"
const val NAV_ENTITY_LIST = "_entities_list"
const val NAV_ENTITY_DETAIL = "_entity_detail"
const val NAV_ENTITY_NAV_LIST = "_navigate"


interface ODataNavigationCommand {
    val route: String
    val arguments: List<NamedNavArgument>
}

const val navigationPropertyNameArg = "navigation_property_name"

object EntitySetsDest : ODataNavigationCommand {
    override val arguments: List<NamedNavArgument>
        get() = listOf()
    override val route: String = "entity_sets"
}


class EntityNavigationCommands(private val entitySet: EntitySet) {

    val entityListNav = object : ODataNavigationCommand {
        override val arguments: List<NamedNavArgument> = getArguments(ScreenType.List)
        override val route: String = getRoute(entitySet, ScreenType.List)
    }

    val entityDetailsNav = object : ODataNavigationCommand {
        override val arguments: List<NamedNavArgument> = getArguments(ScreenType.Details)
        override val route: String =
            getRoute(entitySet, ScreenType.Details)
    }

    val entityCreationNav = object : ODataNavigationCommand {
        override val arguments: List<NamedNavArgument> = getArguments(ScreenType.Create)
        override val route: String =
            getRoute(entitySet, ScreenType.Create)
    }

    val entityEditNav = object : ODataNavigationCommand {
        override val arguments: List<NamedNavArgument> =
            getArguments(ScreenType.Update)
        override val route: String =
            getRoute(entitySet, ScreenType.Update)
    }

    val toEntitiesNav = object : ODataNavigationCommand {
        override val arguments: List<NamedNavArgument> = getArguments(ScreenType.NavigatedList)
        override val route: String = getRoute(
            entitySet,
            ScreenType.NavigatedList
        )
    }

    fun getRoute(entitySet: EntitySet, type: ScreenType): String {
        return when (type) {
            ScreenType.List -> "${entitySet.entityType.localName}/$NAV_ENTITY_LIST"
            ScreenType.Details -> "${entitySet.entityType.localName}/$NAV_ENTITY_DETAIL"
            ScreenType.Update -> "${entitySet.entityType.localName}/$NAV_ENTITY_UPDATE"
            ScreenType.Create -> "${entitySet.entityType.localName}/$NAV_ENTITY_CREATION"
            ScreenType.NavigatedList -> "${entitySet.entityType.localName}/$NAV_ENTITY_NAV_LIST/{$navigationPropertyNameArg}/$NAV_ENTITY_LIST"
        }
    }

    fun getArguments(screenType: ScreenType): List<NamedNavArgument> {
        return when (screenType) {
            ScreenType.List -> listOf()
            ScreenType.Details -> listOf()
            ScreenType.Update -> listOf()
            ScreenType.Create -> listOf()
            ScreenType.NavigatedList -> listOf(
                navArgument(navigationPropertyNameArg) { type = NavType.StringType },
            )
        }
    }
}

fun NavHostController.navigateToEntityList(entitySet: EntitySet) {
    this.navigate("${entitySet.entityType.localName}/$NAV_ENTITY_LIST")
}

fun NavHostController.navigateToEntityDetail(entitySet: EntitySet) {
    this.navigate(
        "${entitySet.entityType.localName}/$NAV_ENTITY_DETAIL"
    )
}

fun NavHostController.navigateToEntityCreation(entitySet: EntitySet) {
    this.navigate("${entitySet.entityType.localName}/$NAV_ENTITY_CREATION")
}

fun NavHostController.navigateToEntityUpdate(entitySet: EntitySet) {
    this.navigate(
        "${entitySet.entityType.localName}/$NAV_ENTITY_UPDATE"
    )
}

fun NavHostController.navigateToNavigatePropertyList(
    navProp: NavigationProperty
) {
    this.navigate(
        "${navProp.relatedEntityType.localName}/$NAV_ENTITY_NAV_LIST/${navProp.name}/$NAV_ENTITY_LIST"
    )
}
