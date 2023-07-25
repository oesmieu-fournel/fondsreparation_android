package com.ecosystem.mobile.reparation.ui.odata.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.ui.odata.*
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.fiori.compose.text.model.FioriTextFieldContent
import com.sap.cloud.mobile.fiori.compose.text.ui.FioriSimpleTextField
import com.sap.cloud.mobile.odata.NavigationProperty
import com.sap.cloud.mobile.odata.Property
import com.ecosystem.mobile.reparation.ui.AlertDialogComponent

data class FieldUIState(
    val value: String,
    val property: Property,
    val isError: Boolean = false,
    val errorMessage: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is FieldUIState) return false
        return (this.value == other.value && this.property.name == other.property.name && this.isError == other.isError && errorMessage == other.errorMessage)
    }
}

/* generic entity edit screen
@Composable
fun EntityEditScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: ODataViewModel
) {
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

    //TODO: move to viewModel?
    val simpleTextFieldStates = remember {
        masterEntity.let { entity ->
            entity.entityType.propertyList.toList()
                .filter {
                    it !is NavigationProperty && it.dataType.isBasic && !it.isKey
                }.map {
                    FieldUIState(
                        entity.getOptionalValue(it)?.toString() ?: "",
//                        entity.getOptionalValue(it),
                        it,
                        false
                    )
                }.map { viewModel.validateField(it, it.value) } //perform init validation
        }.toMutableStateList()
    }

    val actions = listOf(
        ActionItem(
            nameRes = R.string.save,
            iconRes = R.drawable.ic_done_24dp,
            overflowMode = OverflowMode.IF_NECESSARY,
            enabled = simpleTextFieldStates.none { it.isError },
            doAction = {
                viewModel.onSaveAction(
                    masterEntity,
                    simpleTextFieldStates.map { Pair(it.property, it.value) })

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
        modifier = modifier,
        viewModel = viewModel
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp)
        ) {
            itemsIndexed(simpleTextFieldStates)
            { index, uiState ->
                FioriSimpleTextField(
                    value = uiState.value,
                    onValueChange = {
                        simpleTextFieldStates[index] =
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
} */

@Composable
fun NavigateUpWithAlert(dismissDialog: () -> Unit, navigateUp: () -> Unit) {
    val context = LocalContext.current
    AlertDialogComponent(
        title = context.getString(R.string.before_navigation_dialog_title),
        text = context.getString(R.string.before_navigation_dialog_message),
        onNegativeButtonClick = dismissDialog,
        positiveButtonText = context.getString(R.string.before_navigation_dialog_positive_button),
        negativeButtonText = context.getString(R.string.before_navigation_dialog_negative_button),
        onPositiveButtonClick = {
            dismissDialog()
            navigateUp()
        }
    )
}
