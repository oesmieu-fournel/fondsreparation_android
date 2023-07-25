package com.ecosystem.mobile.reparation.ui.odata.screens.customer

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
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Customer

val CustomerEntityDetailScreen: @Composable (
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
                            key = Customer.serviceOrder.name,
                            value = entity.getOptionalValue(Customer.serviceOrder)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.customernumber.name,
                            value = entity.getOptionalValue(Customer.customernumber)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.title.name,
                            value = entity.getOptionalValue(Customer.title)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.firstName.name,
                            value = entity.getOptionalValue(Customer.firstName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.lastName.name,
                            value = entity.getOptionalValue(Customer.lastName)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.houseNo.name,
                            value = entity.getOptionalValue(Customer.houseNo)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.street.name,
                            value = entity.getOptionalValue(Customer.street)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.strSuppl3.name,
                            value = entity.getOptionalValue(Customer.strSuppl3)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.postalCode.name,
                            value = entity.getOptionalValue(Customer.postalCode)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.city.name,
                            value = entity.getOptionalValue(Customer.city)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.country.name,
                            value = entity.getOptionalValue(Customer.country)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.region.name,
                            value = entity.getOptionalValue(Customer.region)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.telNumber.name,
                            value = entity.getOptionalValue(Customer.telNumber)?.toString() ?: ""
                        )
                    )
                FioriKeyValueCell(
                    content = FioriKeyValueCellContent(
                            key = Customer.email.name,
                            value = entity.getOptionalValue(Customer.email)?.toString() ?: ""
                        )
                    )
            }
        }
    }
}

