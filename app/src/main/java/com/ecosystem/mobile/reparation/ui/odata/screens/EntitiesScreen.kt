package com.ecosystem.mobile.reparation.ui.odata.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.ecosystem.mobile.reparation.ui.odata.*
import com.ecosystem.mobile.reparation.ui.odata.data.EntityMediaResource
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.fiori.compose.common.FioriIcon
import com.sap.cloud.mobile.fiori.compose.common.FioriImage
import com.sap.cloud.mobile.fiori.compose.objectcell.model.*
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCell
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCellDefaults
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCellUiState
import com.sap.cloud.mobile.fiori.compose.theme.fioriHorizonAttributes
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.onboarding.compose.screens.LoadingItem
import com.ecosystem.mobile.reparation.ui.AlertDialogComponent

/* generic entity list screen
//TODO: pull down screen to refresh
//https://github.com/aakarshrestha/compose-swipe-to-refresh
//https://google.github.io/accompanist/swiperefresh/
@Composable
fun EntitiesScreen(
    modifier: Modifier = Modifier,
    navigateToDetails: (EntitySet) -> Unit, // navigate to details screen
    navigateToEdit: (EntitySet) -> Unit, // navigate to edit screen
    navigateToAdd: () -> Unit, // navigate to creation screen
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit,
    viewModel: ODataViewModel
) {
    val items = viewModel.pagingDataState.value.collectAsLazyPagingItems()
    val selectedEntities by viewModel.selectItems.collectAsState()
    val context = LocalContext.current

    var deleteConfirm by remember {
        mutableStateOf(false)
    }

    val onDelete = remember {
        { deleteConfirm = true }
    }

    val onAddAction = remember {
        {
            viewModel.setDefaultMasterEntity()
            navigateToAdd()
        }
    }

    val onUpdateAction = remember {
        {
            require(selectedEntities.isNotEmpty())
            val selectedItem = selectedEntities[0]
            viewModel.setMasterEntity(selectedItem)
            viewModel.resetSelection()
            navigateToEdit(viewModel.entitySet)
        }
    }

    if (deleteConfirm) {
        AlertDialogComponent(title = context.getString(R.string.delete_dialog_title),
            text = context.getString(R.string.delete_one_item),
            onNegativeButtonClick = { deleteConfirm = false },
            positiveButtonText = context.getString(R.string.delete),
            onPositiveButtonClick = {
                deleteConfirm = false
                viewModel.deleteSelected()
            })
    }

    val actionItems = when (selectedEntities.size) {
        0 -> listOf(
            ActionItem(
                nameRes = R.string.menu_home,
                iconRes = R.drawable.ic_home_24dp,
                overflowMode = OverflowMode.IF_NECESSARY,
                doAction = navigateToHome
            ), ActionItem(
                nameRes = R.string.menu_refresh,
                iconRes = R.drawable.ic_menu_refresh,
                overflowMode = OverflowMode.IF_NECESSARY,
                doAction = viewModel::refreshEntities
            )
        )
        1 -> listOf(
            ActionItem(
                nameRes = R.string.menu_update,
                iconRes = R.drawable.ic_edit_24dp,
                overflowMode = OverflowMode.IF_NECESSARY,
                doAction = onUpdateAction
            ),
            ActionItem(
                nameRes = R.string.menu_delete,
                iconRes = R.drawable.ic_delete_24dp,
                overflowMode = OverflowMode.IF_NECESSARY,
                doAction = onDelete
            ),
        )

        else -> listOf(
            ActionItem(
                nameRes = R.string.menu_delete,
                iconRes = R.drawable.ic_delete_24dp,
                overflowMode = OverflowMode.IF_NECESSARY,
                doAction = onDelete
            ),
        )
    }

    OperationScreen(
        screenSettings = OperationScreenSettings(
            title = screenTitle(getEntitySetScreenInfo(viewModel.entitySet), ScreenType.List),
            navigateUp = navigateUp,
            actionItems = actionItems,
            floatingActionClick = if (viewModel.parent == null) onAddAction else null, //hide floating action in navigation property list
            floatingActionIcon = Icons.Filled.Add
        ),
        modifier = modifier,
        viewModel = viewModel
    ) {
        val listState: LazyListState = rememberLazyListState()
        if (items.loadState.refresh == LoadState.Loading) {
            LoadingItem()
        } else {
            LazyColumn(state = listState) {
                items(items = items) {  entity ->
                    if (entity == null) {
                        return@items
                    }
                    val selected = selectedEntities.contains(entity)
                    val avatar = FioriAvatarConstruct(
                        hasBadge = false,
                        type = FioriAvatarType.SINGLE,
                        avatarList = listOf(
                            if (!selected) {
                                if (EntityMediaResource.hasMediaResources(entity.entitySet)) {
                                        FioriAvatarData(
                                            image = FioriImage(EntityMediaResource.getMediaResourceUrl(
                                                    entity,
                                                    SAPServiceManager.serviceRoot
                                                )!!
                                            ),
                                            shape = FioriAvatarShape.ROUNDEDCORNER
                                        )
                                } else FioriAvatarData(
                                    text = viewModel.getAvatarText(entity).uppercase(),
                                    textColor = MaterialTheme.fioriHorizonAttributes.SapFioriColorHeaderCaption
                                )
                            } else FioriAvatarData(
                                image = FioriImage(resId = R.drawable.ic_check_circle_black_24dp), //else R.drawable.ic_uncheck_circle_black_24dp,
//                                color = MaterialTheme.colors.onPrimary,
                                size = 40.dp,
                            )
                        ),
                        size = 40.dp,
                        shape = FioriAvatarShape.CIRCLE,
//                        backgroundColor = MaterialTheme.colors.primaryVariant
                    )
                    val stateIcon = getEntityStateIcon(entity)
                    val objectCellData = FioriObjectCellData(
                        title = viewModel.getEntityTitle(entity),
                        iconStack = listOf(
                            IconInFioriObjectCell(
                                FioriIcon(
                                    resId = stateIcon.icon,
                                    contentDescription = stringResource(id = stateIcon.desc),
                                    tint = Color.Unspecified
                                )
                            )
                        ),
                        subtitle = "Subtitle goes here",
                        caption = "caption display",
                        avatar = avatar,
                    )

                    FioriObjectCell(
                        uiState = FioriObjectCellUiState(
                            objectCellData, isRead = true, displayProcess = false
                        ),
                        colors = FioriObjectCellDefaults.colors(),
                        textStyles = FioriObjectCellDefaults.textStyles(),
                        styles = FioriObjectCellDefaults.styles(),
                        onClick = {
                            viewModel.setMasterEntity(entity)
                            navigateToDetails(viewModel.entitySet)
                        },
                        onLongPress = {
                            viewModel.onEntitySelection(entity)
                        }
                    )
                }
            }
        }
    }
} */

data class StateIcon(@DrawableRes val icon: Int, @StringRes val desc: Int)

fun getEntityStateIcon(it: EntityValue): StateIcon {
    return when {
        it.inErrorState -> StateIcon(
            R.drawable.ic_error_state, R.string.error_state
        )

        it.isUpdated -> StateIcon(
            R.drawable.ic_updated_state, R.string.updated_state
        )
        it.isLocal -> StateIcon(
            R.drawable.ic_local_state, R.string.local_state
        )
        else ->
            StateIcon(
                R.drawable.ic_download_state, R.string.download_state
            )
    }

}

sealed interface ResultOf<out Bitmap> {
    object Loading : ResultOf<Nothing>
    data class Success(val bitmap: Bitmap) : ResultOf<Bitmap>
    object Failure : ResultOf<Nothing>
}


