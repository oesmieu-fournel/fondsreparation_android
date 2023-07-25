package com.ecosystem.mobile.reparation.ui.odata.screens.partner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.ui.odata.*
import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.ecosystem.mobile.reparation.ui.odata.data.EntityMediaResource
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.fiori.compose.keyvaluecell.model.FioriKeyValueCellContent
import com.sap.cloud.mobile.fiori.compose.keyvaluecell.ui.FioriKeyValueCell
import com.sap.cloud.mobile.fiori.compose.objectheader.model.*
import com.sap.cloud.mobile.fiori.compose.objectheader.ui.FioriObjectHeader
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.NavigationProperty
import com.ecosystem.mobile.reparation.ui.AlertDialogComponent
import com.ecosystem.mobile.reparation.ui.odata.screens.OperationScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.OperationScreenSettings
import com.ecosystem.mobile.reparation.ui.odata.screens.defaultObjectHeaderData
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Partner

val PartnerEntityDetailScreen: @Composable (
    onNavigateProperty: (EntityValue, NavigationProperty) -> Unit,
    navigateToEdit: (EntitySet) -> Unit,
    navigateUp: () -> Unit,
    viewModel: ODataViewModel
) -> Unit = { onNavigateProperty, navigateToEdit, navigateUp, viewModel ->
    val masterEntity by viewModel.masterEntity.collectAsState()
    val context = LocalContext.current

    var deleteConfirm by remember {
        mutableStateOf(false)
    }

    val onDelete = remember {
        { deleteConfirm = true }
    }

    if (deleteConfirm) {
        AlertDialogComponent(title = context.getString(R.string.delete_dialog_title),
            text = context.getString(R.string.delete_one_item),
            onNegativeButtonClick = { deleteConfirm = false },
            positiveButtonText = context.getString(R.string.delete),
            onPositiveButtonClick = {
                deleteConfirm = false
                viewModel.onDeleteEntity()
            })
    }

    OperationScreen(
        screenSettings = OperationScreenSettings(
            title = screenTitle(getEntitySetScreenInfo(viewModel.entitySet), ScreenType.Details),
            actionItems = listOf(
                ActionItem(
                    nameRes = R.string.menu_update,
                    iconRes = R.drawable.ic_edit_24dp,
                    overflowMode = OverflowMode.IF_NECESSARY,
                    doAction = { navigateToEdit(viewModel.entitySet) }),
                ActionItem(
                    nameRes = R.string.menu_delete,
                    iconRes = R.drawable.ic_delete_24dp,
                    overflowMode = OverflowMode.IF_NECESSARY,
                    doAction = onDelete
                ),
            ),
            navigateUp = navigateUp,
        ),
        onFinishSuccess = navigateUp,
        modifier = Modifier,
        viewModel = viewModel
    ) {
        Column() {
            val entity = masterEntity
            FioriObjectHeader(
                primaryPage = defaultObjectHeaderData(
                    title = viewModel.getEntityTitle(entity),
                    imageUrl = EntityMediaResource.getMediaResourceUrl(
                        entity,
                        SAPServiceManager.serviceRoot
                    ),
                    imageChars = viewModel.getAvatarText(entity)
                ),
                statusLayout = FioriObjectHeaderStatusLayout.Inline
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.serviceOrder.name,
                            value = entity.getOptionalValue(Partner.serviceOrder)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.partnerNumber.name,
                            value = entity.getOptionalValue(Partner.partnerNumber)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.serviceObjectType.name,
                            value = entity.getOptionalValue(Partner.serviceObjectType)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.firstName.name,
                            value = entity.getOptionalValue(Partner.firstName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.lastName.name,
                            value = entity.getOptionalValue(Partner.lastName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.partnerFct.name,
                            value = entity.getOptionalValue(Partner.partnerFct)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.addrNr.name,
                            value = entity.getOptionalValue(Partner.addrNr)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.addrNp.name,
                            value = entity.getOptionalValue(Partner.addrNp)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.houseNo.name,
                            value = entity.getOptionalValue(Partner.houseNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.street.name,
                            value = entity.getOptionalValue(Partner.street)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.strSuppl3.name,
                            value = entity.getOptionalValue(Partner.strSuppl3)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.postlCod1.name,
                            value = entity.getOptionalValue(Partner.postlCod1)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.city.name,
                            value = entity.getOptionalValue(Partner.city)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.country.name,
                            value = entity.getOptionalValue(Partner.country)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Partner.region.name,
                            value = entity.getOptionalValue(Partner.region)?.toString() ?: ""
                        )
                    )
            }
        }
    }
}

