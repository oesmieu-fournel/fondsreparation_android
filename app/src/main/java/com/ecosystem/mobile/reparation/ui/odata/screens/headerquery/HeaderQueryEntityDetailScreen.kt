package com.ecosystem.mobile.reparation.ui.odata.screens.headerquery

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
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderQuery

val HeaderQueryEntityDetailScreen: @Composable (
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
                            key = HeaderQuery.contactNo.name,
                            value = entity.getOptionalValue(HeaderQuery.contactNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.serviceOrder.name,
                            value = entity.getOptionalValue(HeaderQuery.serviceOrder)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.statusDesc.name,
                            value = entity.getOptionalValue(HeaderQuery.statusDesc)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.modifStatusDate.name,
                            value = entity.getOptionalValue(HeaderQuery.modifStatusDate)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.repairNo.name,
                            value = entity.getOptionalValue(HeaderQuery.repairNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.repairFirstName.name,
                            value = entity.getOptionalValue(HeaderQuery.repairFirstName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.repairLastName.name,
                            value = entity.getOptionalValue(HeaderQuery.repairLastName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subContractNo.name,
                            value = entity.getOptionalValue(HeaderQuery.subContractNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.description.name,
                            value = entity.getOptionalValue(HeaderQuery.description)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.creationDate.name,
                            value = entity.getOptionalValue(HeaderQuery.creationDate)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.externalRef.name,
                            value = entity.getOptionalValue(HeaderQuery.externalRef)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.repairDate.name,
                            value = entity.getOptionalValue(HeaderQuery.repairDate)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.product.name,
                            value = entity.getOptionalValue(HeaderQuery.product)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.productDescription.name,
                            value = entity.getOptionalValue(HeaderQuery.productDescription)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.status.name,
                            value = entity.getOptionalValue(HeaderQuery.status)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopNo.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopFirstName.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopFirstName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopLastName.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopLastName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopHouseNo.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopHouseNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopStreet.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopStreet)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopStrSuppl3.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopStrSuppl3)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopPostlCod1.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopPostlCod1)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopCity.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopCity)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopCountry.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopCountry)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.workshopRegion.name,
                            value = entity.getOptionalValue(HeaderQuery.workshopRegion)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subContractFirstName.name,
                            value = entity.getOptionalValue(HeaderQuery.subContractFirstName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subContractLastName.name,
                            value = entity.getOptionalValue(HeaderQuery.subContractLastName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subContractHouseNo.name,
                            value = entity.getOptionalValue(HeaderQuery.subContractHouseNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractStreet.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractStreet)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractStrSuppl3.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractStrSuppl3)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractPostlCod1.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractPostlCod1)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractCity.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractCity)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractCountry.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractCountry)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.subcontractRegion.name,
                            value = entity.getOptionalValue(HeaderQuery.subcontractRegion)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = HeaderQuery.origin.name,
                            value = entity.getOptionalValue(HeaderQuery.origin)?.toString() ?: ""
                        )
                    )
                TextButton(onClick = {
                    onNavigateProperty(entity, HeaderQuery.item as NavigationProperty)
                }) {
                    Text("Item")
                }
                TextButton(onClick = {
                    onNavigateProperty(entity, HeaderQuery.pricing as NavigationProperty)
                }) {
                    Text("Pricing")
                }
                TextButton(onClick = {
                    onNavigateProperty(entity, HeaderQuery.header as NavigationProperty)
                }) {
                    Text("Header")
                }
                TextButton(onClick = {
                    onNavigateProperty(entity, HeaderQuery.partnerCustomer as NavigationProperty)
                }) {
                    Text("PartnerCustomer")
                }
                TextButton(onClick = {
                    onNavigateProperty(entity, HeaderQuery.partner as NavigationProperty)
                }) {
                    Text("Partner")
                }
            }
        }
    }
}

