package com.ecosystem.mobile.reparation.dev.ui.odata

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sap.cloud.mobile.fiori.compose.theme.fioriHorizonAttributes

// Essentially a wrapper around a lambda function to give it a name and icon
// akin to Android menu XML entries.
// As an item on the action bar, the action will be displayed with an IconButton
// with the given icon, if not null. Otherwise, the string from the name resource is used.
// In overflow menu, item will always be displayed as text.
data class ActionItem(
    @StringRes
    val nameRes: Int,
    @DrawableRes
    val iconRes: Int? = null,
    val overflowMode: OverflowMode = OverflowMode.IF_NECESSARY,
    val enabled: Boolean = true,
    val doAction: () -> Unit,
) {
    // allow 'calling' the action like a function
    operator fun invoke() = doAction()
}

// Whether action items are allowed to overflow into a dropdown menu - or NOT SHOWN to hide
enum class OverflowMode {
    NEVER_OVERFLOW, IF_NECESSARY, ALWAYS_OVERFLOW, NOT_SHOWN
}

// Note: should be used in a RowScope
@Composable
fun ActionMenu(
    items: List<ActionItem>,
    isEnabled: Boolean = true,
    numIcons: Int = 3, // includes overflow menu icon; may be overridden by NEVER_OVERFLOW
    menuVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    if (items.isEmpty()) {
        return
    }
    // decide how many action items to show as icons
    val (appbarActions, overflowActions) = remember(items, numIcons) {
        separateIntoIconAndOverflow(items, numIcons)
    }

    for (item in appbarActions) {
        key(item.hashCode()) {
            val name = stringResource(item.nameRes)
            if (item.iconRes != null) {
                IconButton(onClick = item.doAction, enabled = isEnabled && item.enabled) {
                    Icon(painterResource(item.iconRes), name)
                }
            } else {
                TextButton(onClick = item.doAction, enabled = isEnabled && item.enabled) {
                    Text(
                        text = name,
                        color = MaterialTheme.fioriHorizonAttributes.SapFioriColorSectionDivider,
                    )
                }
            }
        }
    }

    if (overflowActions.isNotEmpty()) {
        IconButton(onClick = { menuVisible.value = true }) {
            Icon(Icons.Default.MoreVert, "More actions")
        }
        DropdownMenu(
            expanded = menuVisible.value,
            onDismissRequest = { menuVisible.value = false },
        ) {
            for (item in overflowActions) {
                key(item.hashCode()) {
                    DropdownMenuItem(
                        text = {
                            //Icon(item.icon, item.name) just have text in the overflow menu
                            Text(stringResource(item.nameRes))
                        },
                        onClick = {
                            menuVisible.value = false
                            item.doAction()
                        },
                        enabled = isEnabled && item.enabled
                    )
                }
            }
        }
    }
}

private fun separateIntoIconAndOverflow(
    items: List<ActionItem>,
    numIcons: Int
): Pair<List<ActionItem>, List<ActionItem>> {
    var (iconCount, overflowCount, preferIconCount) = Triple(0, 0, 0)
    for (item in items) {
        when (item.overflowMode) {
            OverflowMode.NEVER_OVERFLOW -> iconCount++
            OverflowMode.IF_NECESSARY -> preferIconCount++
            OverflowMode.ALWAYS_OVERFLOW -> overflowCount++
            OverflowMode.NOT_SHOWN -> {}
        }
    }

    val needsOverflow = iconCount + preferIconCount > numIcons || overflowCount > 0
    val actionIconSpace = numIcons - (if (needsOverflow) 1 else 0)

    val iconActions = ArrayList<ActionItem>()
    val overflowActions = ArrayList<ActionItem>()

    var iconsAvailableBeforeOverflow = actionIconSpace - iconCount
    for (item in items) {
        when (item.overflowMode) {
            OverflowMode.NEVER_OVERFLOW -> {
                iconActions.add(item)
            }
            OverflowMode.ALWAYS_OVERFLOW -> {
                overflowActions.add(item)
            }
            OverflowMode.IF_NECESSARY -> {
                if (iconsAvailableBeforeOverflow > 0) {
                    iconActions.add(item)
                    iconsAvailableBeforeOverflow--
                } else {
                    overflowActions.add(item)
                }
            }
            OverflowMode.NOT_SHOWN -> {
                // skip
            }
        }
    }
    return Pair(iconActions, overflowActions)
}
