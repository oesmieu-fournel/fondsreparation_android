package com.ecosystem.mobile.reparation.ui.odata.screens.attachment

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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.ecosystem.mobile.reparation.ui.odata.*
import com.ecosystem.mobile.reparation.ui.odata.data.EntityMediaResource
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.fiori.compose.avatar.model.*
import com.sap.cloud.mobile.fiori.compose.common.FioriIcon
import com.sap.cloud.mobile.fiori.compose.common.FioriImage
import com.sap.cloud.mobile.fiori.compose.objectcell.model.*
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCell
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCellDefaults
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCellUiState
import com.sap.cloud.mobile.fiori.compose.theme.fioriHorizonAttributes
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.onboarding.compose.screens.LoadingItem
import com.ecosystem.mobile.reparation.ui.AlertDialogComponent
import com.ecosystem.mobile.reparation.ui.odata.screens.*

//TODO: pull down screen to refresh
//https://github.com/aakarshrestha/compose-swipe-to-refresh
//https://google.github.io/accompanist/swiperefresh/
val AttachmentEntitiesScreen:
    @Composable
        (
        navigateToDetails: (EntitySet) -> Unit, // navigate to details screen
        navigateToEdit: (EntitySet) -> Unit, // navigate to edit screen
        navigateToAdd: () -> Unit, // navigate to creation screen
        navigateToHome: () -> Unit,
        navigateUp: () -> Unit,
        viewModel: ODataViewModel
    ) -> Unit =
{ navigateToDetails, navigateToEdit, navigateToAdd, navigateToHome, navigateUp, viewModel ->
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
        modifier = Modifier,
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
                                            FioriImage(
                                                url = EntityMediaResource.getMediaResourceUrl(
                                                    entity,
                                                    SAPServiceManager.serviceRoot
                                                )!!
                                            ),
                                            shape = FioriAvatarShape.ROUNDEDCORNER
                                        )
                                } else FioriAvatarData(
                                    text = viewModel.getAvatarText(entity).uppercase(),
                                    textColor = MaterialTheme.fioriHorizonAttributes.SapFioriColorBaseText
                                )
                            } else FioriAvatarData(
                                FioriImage(resId = R.drawable.ic_check_circle_black_24dp),
                                color = MaterialTheme.fioriHorizonAttributes.SapFioriColorHeaderCaption,
                                size = 40.dp,
                            )
                        ),
                        size = 40.dp,
                        shape = FioriAvatarShape.CIRCLE,
//                      backgroundColor = MaterialTheme.fioriHorizonAttributes.SapFioriColorS6
                    )
                    val stateIcon = getEntityStateIcon(entity)
                    val objectCellData = FioriObjectCellData(
                        headline = viewModel.getEntityTitle(entity),
                        iconStack = listOf(
                            IconStackElement(viewModel.getAvatarText(entity).uppercase()),
                            IconStackElement(
                                FioriIcon(
                                    resId = com.sap.cloud.mobile.fiori.compose.R.drawable.avatar_badge,
                                    contentDescription = stringResource(id = stateIcon.desc),
                                    tint = MaterialTheme.fioriHorizonAttributes.SapFioriColorSectionDivider
                                )
                            )
                        ),
                        subheadline = "Subtitle goes here",
                        footnote = "caption display",
                        avatar = avatar,
                    )

                    FioriObjectCell(
                        uiState = FioriObjectCellUiState(
                            objectCellData, isRead = true, displayProcess = false
                        ),
                        colors = FioriObjectCellDefaults.colors(),
                        textStyles = FioriObjectCellDefaults.textStyles(),
                        styles = FioriObjectCellDefaults.styles(iconStackSize = 10.dp),
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
}
