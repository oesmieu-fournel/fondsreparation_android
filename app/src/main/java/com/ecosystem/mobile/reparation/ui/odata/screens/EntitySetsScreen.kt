package com.ecosystem.mobile.reparation.ui.odata.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.ui.WelcomeActivity
import com.ecosystem.mobile.reparation.ui.odata.ActionItem
import com.ecosystem.mobile.reparation.ui.AlertDialogComponent
import com.ecosystem.mobile.reparation.ui.odata.EntitySetScreenInfo
import com.ecosystem.mobile.reparation.ui.odata.OverflowMode
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.EntitySetViewModel
import com.sap.cloud.mobile.fiori.compose.avatar.model.FioriAvatarConstruct
import com.sap.cloud.mobile.fiori.compose.avatar.model.FioriAvatarData
import com.sap.cloud.mobile.fiori.compose.avatar.model.FioriAvatarType
import com.sap.cloud.mobile.fiori.compose.common.FioriImage
import com.sap.cloud.mobile.fiori.compose.objectcell.model.FioriObjectCellData
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCell
import com.sap.cloud.mobile.fiori.compose.objectcell.ui.FioriObjectCellUiState
import com.sap.cloud.mobile.flows.compose.core.FlowContextRegistry
import com.sap.cloud.mobile.flows.compose.flows.FlowType
import com.sap.cloud.mobile.flows.compose.flows.FlowUtil
import com.sap.cloud.mobile.odata.EntitySet

@Composable
fun EntitySetScreen(
    list: List<EntitySetScreenInfo>,
    onRowClick: (EntitySet) -> Unit,
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit
) {
    val viewModel: EntitySetViewModel = viewModel()
    val isMultipleUserMode = viewModel.isMultipleUserMode.collectAsState()

    var startLogout by remember { mutableStateOf(false) }
    var startDeleteRegistration by remember { mutableStateOf(false) }

    if (startLogout) {
        startLogout = false
        startLogoutFlow()
    }

    if (startDeleteRegistration) {
        deleteRegistration { startDeleteRegistration = false }
    }

    OperationScreen(
        screenSettings = OperationScreenSettings(
            title = stringResource(id = R.string.application_name),
            actionItems = listOf(
                ActionItem(
                    nameRes = R.string.menu_item_settings,
                    overflowMode = OverflowMode.ALWAYS_OVERFLOW,
                    doAction = navigateToSettings
                ),
                ActionItem(
                    nameRes = R.string.logout,
                    overflowMode = OverflowMode.ALWAYS_OVERFLOW,
                    doAction = { startLogout = true },
                ),
                ActionItem(
                    nameRes = R.string.delete_registration,
                    overflowMode = if (isMultipleUserMode.value) OverflowMode.ALWAYS_OVERFLOW else OverflowMode.NOT_SHOWN,
                    doAction = { startDeleteRegistration = true },
                ),
            )
        ),
        modifier = modifier,
        viewModel = viewModel
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(list) { item ->
                val objectCellData = FioriObjectCellData(
                    headline = stringResource(id = item.setTitleId),
                    avatar = FioriAvatarConstruct(
                        hasBadge = false,
                        type = FioriAvatarType.SINGLE,
                        avatarList = listOf(
                            FioriAvatarData(
                                image = FioriImage(resId = item.iconId),
                                size = 40.dp,
                            )
                        )
                    )
                )

                FioriObjectCell(
                    uiState = FioriObjectCellUiState(
                        objectCellData, isRead = true, displayProcess = false
                    ),
                    onClick = {
                        onRowClick(
                            item.entitySet
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun deleteRegistration(dismissDialog: () -> Unit) {
    val context = LocalContext.current

    AlertDialogComponent(
        title = context.getString(
            com.sap.cloud.mobile.onboarding.compose.R.string.dialog_warning_title
        ),
        text = context.getString(R.string.delete_registration_warning),
        onPositiveButtonClick = {
            dismissDialog()
            startDeleteRegFlow(context)

        },
        positiveButtonText = context.getString(R.string.yes),
        onNegativeButtonClick = dismissDialog,
    )
}

private fun startDeleteRegFlow(context: Context, finishCallback: (Int)-> Unit = {}) {
    FlowUtil.startFlow(
        context = context,
        flowContext = FlowContextRegistry.flowContext.copy(
            flowType = FlowType.DeleteRegistration, flow = null
        )
    ) { resultCode, _ ->
        finishCallback(resultCode)
        if (resultCode == Activity.RESULT_OK) {
            val intent = Intent(context, WelcomeActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun startLogoutFlow() {
    val context = LocalContext.current
    FlowUtil.startFlow(
        context = context,
        flowContext = FlowContextRegistry.flowContext.copy(
            flowType = FlowType.Logout, flow = null
        )
    ) { resultCode, _ ->
        if (resultCode == Activity.RESULT_OK) {
            val intent = Intent(context, WelcomeActivity::class.java).also {
                it.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            }
            context.startActivity(intent)
        }
    }
}

@Preview
@Composable
fun EntitySetScreenPreview() {
    val entitySetNames = EntitySetScreenInfo.values().toList()
    EntitySetScreen(entitySetNames, { println("click $it row") }) {}
}
