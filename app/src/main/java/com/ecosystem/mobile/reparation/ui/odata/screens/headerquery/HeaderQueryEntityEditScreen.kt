package com.ecosystem.mobile.reparation.ui.odata.screens.headerquery

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
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderQuery

val HeaderQueryEntityEditScreen: @Composable (
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
                masterEntity.getOptionalValue(HeaderQuery.statusDesc)?.toString() ?: "",
                HeaderQuery.statusDesc,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.modifStatusDate)?.toString() ?: "",
                HeaderQuery.modifStatusDate,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.repairNo)?.toString() ?: "",
                HeaderQuery.repairNo,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.repairFirstName)?.toString() ?: "",
                HeaderQuery.repairFirstName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.repairLastName)?.toString() ?: "",
                HeaderQuery.repairLastName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subContractNo)?.toString() ?: "",
                HeaderQuery.subContractNo,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.description)?.toString() ?: "",
                HeaderQuery.description,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.creationDate)?.toString() ?: "",
                HeaderQuery.creationDate,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.externalRef)?.toString() ?: "",
                HeaderQuery.externalRef,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.repairDate)?.toString() ?: "",
                HeaderQuery.repairDate,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.product)?.toString() ?: "",
                HeaderQuery.product,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.productDescription)?.toString() ?: "",
                HeaderQuery.productDescription,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.status)?.toString() ?: "",
                HeaderQuery.status,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopNo)?.toString() ?: "",
                HeaderQuery.workshopNo,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopFirstName)?.toString() ?: "",
                HeaderQuery.workshopFirstName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopLastName)?.toString() ?: "",
                HeaderQuery.workshopLastName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopHouseNo)?.toString() ?: "",
                HeaderQuery.workshopHouseNo,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopStreet)?.toString() ?: "",
                HeaderQuery.workshopStreet,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopStrSuppl3)?.toString() ?: "",
                HeaderQuery.workshopStrSuppl3,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopPostlCod1)?.toString() ?: "",
                HeaderQuery.workshopPostlCod1,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopCity)?.toString() ?: "",
                HeaderQuery.workshopCity,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopCountry)?.toString() ?: "",
                HeaderQuery.workshopCountry,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.workshopRegion)?.toString() ?: "",
                HeaderQuery.workshopRegion,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subContractFirstName)?.toString() ?: "",
                HeaderQuery.subContractFirstName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subContractLastName)?.toString() ?: "",
                HeaderQuery.subContractLastName,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subContractHouseNo)?.toString() ?: "",
                HeaderQuery.subContractHouseNo,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractStreet)?.toString() ?: "",
                HeaderQuery.subcontractStreet,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractStrSuppl3)?.toString() ?: "",
                HeaderQuery.subcontractStrSuppl3,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractPostlCod1)?.toString() ?: "",
                HeaderQuery.subcontractPostlCod1,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractCity)?.toString() ?: "",
                HeaderQuery.subcontractCity,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractCountry)?.toString() ?: "",
                HeaderQuery.subcontractCountry,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.subcontractRegion)?.toString() ?: "",
                HeaderQuery.subcontractRegion,
                false
            ),
            FieldUIState(
                masterEntity.getOptionalValue(HeaderQuery.origin)?.toString() ?: "",
                HeaderQuery.origin,
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
