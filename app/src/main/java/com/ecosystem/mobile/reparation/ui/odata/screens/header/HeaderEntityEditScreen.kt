package com.ecosystem.mobile.reparation.ui.odata.screens.header

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
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header

val HeaderEntityEditScreen: @Composable (
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
                masterEntity.getOptionalValue(Header.catDesc)?.toString() ?: "",
                Header.catDesc,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.serviceObjectType)?.toString() ?: "",
                Header.serviceObjectType,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.origin)?.toString() ?: "",
                Header.origin,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.requestedServiceStartDate)?.toString() ?: "",
                Header.requestedServiceStartDate,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.serviceOrderType)?.toString() ?: "",
                Header.serviceOrderType,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.description)?.toString() ?: "",
                Header.description,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.soldToParty)?.toString() ?: "",
                Header.soldToParty,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.purchaseOrderByCustomer)?.toString() ?: "",
                Header.purchaseOrderByCustomer,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.schema)?.toString() ?: "",
                Header.schema,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.category)?.toString() ?: "",
                Header.category,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.status)?.toString() ?: "",
                Header.status,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.irisCode)?.toString() ?: "",
                Header.irisCode,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.rejectStatus)?.toString() ?: "",
                Header.rejectStatus,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.rejectStatusShort)?.toString() ?: "",
                Header.rejectStatusShort,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(Header.rejectStatusLong)?.toString() ?: "",
                Header.rejectStatusLong,
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
