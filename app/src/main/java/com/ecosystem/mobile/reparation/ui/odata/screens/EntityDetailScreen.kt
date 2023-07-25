package com.ecosystem.mobile.reparation.ui.odata.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.ui.odata.*
import com.sap.cloud.mobile.fiori.compose.common.FioriIcon
import com.sap.cloud.mobile.fiori.compose.common.FioriImage
import com.sap.cloud.mobile.fiori.compose.avatar.model.FioriAvatarData
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
import kotlinx.coroutines.flow.map

/* generic entity detail screen
@Composable
fun EntityDetailsScreen(
    modifier: Modifier = Modifier,
    onNavigateProperty: (EntityValue, NavigationProperty) -> Unit,
    navigateToEdit: (EntitySet) -> Unit,
    navigateUp: () -> Unit,
    viewModel: ODataViewModel
) {
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
        modifier = modifier,
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

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                items(entity.entityType.propertyList.toList())
                { property ->
                    if (property is NavigationProperty) {
                        TextButton(onClick = {
                            onNavigateProperty(entity, property)
                        }) {
                            Text(property.name)
                        }
                    } else if (property.dataType.isBasic) FioriKeyValueCell(
                        content = FioriKeyValueCellContent(
                            key = property.name,
                            value = entity.getOptionalValue(property)?.toString() ?: ""
                        )
                    )
                }
            }
        }
    }
} */

fun defaultObjectHeaderData(
    title: String,
    imageByteArray: ByteArray? = null,
    imageUrl: String? = null,
    imageChars: String? = null
): FioriObjectHeaderData {

    val content = FioriObjectHeaderData(
        title
    )

    content.detailImage = imageByteArray?.let {
        if(it.isNotEmpty()) {
            FioriAvatarData(
                FioriImage(
                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size),
                    contentDescription = "Detail image"
                )
            )
        } else {
            //load image fail
            FioriAvatarData(
                FioriImage(
                    resId = R.drawable.ic_sync_error,
                    contentDescription = "Load fail"
                )
            )
        }
    } ?: imageUrl?.let {
        FioriAvatarData(
            FioriImage(
                url = it,
                contentDescription = "Detail image"
            )
        )
    } ?: imageChars?.let {
        FioriAvatarData(
            text = it,
            textFontSize = 24.sp,
        )
    } ?: FioriAvatarData(
        FioriImage(
            resId = R.drawable.ic_sync,
            contentDescription = "Loading image"
        )
    )

    content.subtitle = "This is a subtitle that can take up to a maximum of three lines."

    content.accessoryKpi = "accKpi"
    content.accessoryKpiLabel = "accKpiLabel"



    content.status = FioriObjectHeaderStatusData(
        label = "Status",
        icon = FioriIcon(
            resId = R.drawable.ic_home_24dp,
            contentDescription = "Positive status icon",
        ),
        type = FioriObjectHeaderStatusType.Positive,
        isIconAtStart = true
    )
    content.subStatus = FioriObjectHeaderStatusData(
        label = "SubStatus",
        icon = FioriIcon(
            resId = R.drawable.ic_error_state,
            contentDescription = "Critical status icon",
        ),
        type = FioriObjectHeaderStatusType.Critical,
        isIconAtStart = false
    )


    content.labelItems = listOf(
        FioriObjectHeaderLabelItemData(
            label = "Atribute1"
        ),
        FioriObjectHeaderLabelItemData(
            label = "Atribute2",
            icon = FioriIcon(
                resId = R.drawable.ic_home_24dp
            ),
            isIconAtStart = false
        ),
        FioriObjectHeaderLabelItemData(
            label = "Subtitle"
        ),
        FioriObjectHeaderLabelItemData(
            label = "3/28/2022",
            icon = FioriIcon(
                resId = R.drawable.ic_delete_24dp
            )
        )
    )

    content.description = "description show here"

    return content
}
