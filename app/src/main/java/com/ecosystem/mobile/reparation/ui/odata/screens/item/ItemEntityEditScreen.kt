package com.ecosystem.mobile.reparation.ui.odata.screens.item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.ui.odata.*
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.fiori.compose.text.model.FioriTextFieldContent
import com.sap.cloud.mobile.fiori.compose.text.ui.FioriSimpleTextField
import com.ecosystem.mobile.reparation.ui.odata.screens.NavigateUpWithAlert
import com.ecosystem.mobile.reparation.ui.odata.screens.FieldUIState
import com.ecosystem.mobile.reparation.ui.odata.screens.OperationScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.OperationScreenSettings
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Item

val ItemEntityEditScreen: @Composable (
    navigateUp: () -> Unit,
    viewModel: ODataViewModel
) -> Unit = { navigateUp, viewModel ->
    val masterEntity by viewModel.masterEntity.collectAsState()
    val isCreation = !masterEntity.hasKey()
    var isNavigateUp by remember {
        mutableStateOf(false)
    }

    val onNavigateBack = remember {
        {
            isNavigateUp = true
        }
    }

    if (isNavigateUp) {
        NavigateUpWithAlert({ isNavigateUp = false }, navigateUp)
    }

    BackHandler {
        isNavigateUp = true
    }

    val fieldStates = remember {
        listOf<FieldUIState>(
            FieldUIState(
                masterEntity.getOptionalValue(Item.serviceObjectType)?.toString() ?: "",
                Item.serviceObjectType,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.product)?.toString() ?: "",
                Item.product,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.partnerProduct)?.toString() ?: "",
                Item.partnerProduct,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemDescription)?.toString() ?: "",
                Item.itemDescription,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.commercialReference)?.toString() ?: "",
                Item.commercialReference,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemQty)?.toString() ?: "",
                Item.itemQty,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemType)?.toString() ?: "",
                Item.itemType,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemSchema)?.toString() ?: "",
                Item.itemSchema,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemCategory)?.toString() ?: "",
                Item.itemCategory,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Item.itemStatus)?.toString() ?: "",
                Item.itemStatus,
                false
            ),
        ).map { viewModel.validateField(it, it.value) } //perform init validation
        .toMutableStateList()
    }


    val actions = listOf(
        ActionItem(
            nameRes = R.string.save,
            iconRes = R.drawable.ic_done_24dp,
            overflowMode = OverflowMode.IF_NECESSARY,
            enabled = fieldStates.none { it.isError },
            doAction = {
                viewModel.onSaveAction(
                    masterEntity,
                    fieldStates.map { Pair(it.property, it.value) })

            }),
    )

    OperationScreen(
        screenSettings = OperationScreenSettings(
            title = screenTitle(
                getEntitySetScreenInfo(viewModel.entitySet),
                if (isCreation) ScreenType.Create else ScreenType.Update
            ),
            navigateUp = onNavigateBack,
            actionItems = actions
        ),
        onFinishSuccess = navigateUp,
        modifier = Modifier,
        viewModel = viewModel
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            itemsIndexed(fieldStates)
            { index, uiState ->
                FioriSimpleTextField(
                    value = uiState.value,
                    onValueChange = {
                        fieldStates[index] =
                            viewModel.validateField(uiState, it)
                    },
                    content = FioriTextFieldContent(
                        label = uiState.property.name,
                        required = !uiState.property.isNullable,
                        errorMessage = uiState.errorMessage
                    ),
                    isError = uiState.isError,
                )
            }
        }
    }
}
