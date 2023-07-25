package com.ecosystem.mobile.reparation.ui.odata

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ecosystem.mobile.reparation.R
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.mobile.odata.EntitySet
import com.ecosystem.mobile.reparation.ui.odata.screens.attachment.AttachmentEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.attachment.AttachmentEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.attachment.AttachmentEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.customer.CustomerEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.customer.CustomerEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.customer.CustomerEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headercontact.HeaderContactEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headercontact.HeaderContactEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headercontact.HeaderContactEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headerquery.HeaderQueryEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headerquery.HeaderQueryEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.headerquery.HeaderQueryEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.header.HeaderEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.header.HeaderEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.header.HeaderEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.item.ItemEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.item.ItemEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.item.ItemEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.partner.PartnerEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.partner.PartnerEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.partner.PartnerEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.pricing.PricingEntitiesScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.pricing.PricingEntityEditScreen
import com.ecosystem.mobile.reparation.ui.odata.screens.pricing.PricingEntityDetailScreen
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.NavigationProperty


enum class EntitySetScreenInfo(
    val entitySet: EntitySet, val setTitleId: Int, val itemTitleId: Int, val iconId: Int
) {
	AttachmentSet(
		EntitySets.attachmentSet, 
		R.string.eset_attachmentset,
		R.string.eset_attachmentset_single,
	    R.drawable.ic_android_blue
	    ),
	CustomerSet(
		EntitySets.customerSet, 
		R.string.eset_customerset,
		R.string.eset_customerset_single,
	    R.drawable.ic_android_white
	    ),
	HeaderContactSet(
		EntitySets.headerContactSet, 
		R.string.eset_headercontactset,
		R.string.eset_headercontactset_single,
	    R.drawable.ic_android_blue
	    ),
	HeaderQuerySet(
		EntitySets.headerQuerySet, 
		R.string.eset_headerqueryset,
		R.string.eset_headerqueryset_single,
	    R.drawable.ic_android_white
	    ),
	HeaderSet(
		EntitySets.headerSet, 
		R.string.eset_headerset,
		R.string.eset_headerset_single,
	    R.drawable.ic_android_blue
	    ),
	ItemSet(
		EntitySets.itemSet, 
		R.string.eset_itemset,
		R.string.eset_itemset_single,
	    R.drawable.ic_android_white
	    ),
	PartnerSet(
		EntitySets.partnerSet, 
		R.string.eset_partnerset,
		R.string.eset_partnerset_single,
	    R.drawable.ic_android_blue
	    ),
	PricingSet(
		EntitySets.pricingSet, 
		R.string.eset_pricingset,
		R.string.eset_pricingset_single,
	    R.drawable.ic_android_white
	    )
}

enum class EntityScreens(
    val entitySet: EntitySet, val entityListScreen: @Composable (
        navigateToDetails: (EntitySet) -> Unit, // navigate to details screen
        navigateToEdit: (EntitySet) -> Unit, // navigate to edit screen
        navigateToAdd: () -> Unit, // navigate to creation screen
        navigateToHome: () -> Unit, navigateUp: () -> Unit, viewModel: ODataViewModel
    ) -> Unit, val entityEditScreen: @Composable (
        navigateUp: () -> Unit, viewModel: ODataViewModel
    ) -> Unit, val entityDetailScreen: @Composable (
        onNavigateProperty: (EntityValue, NavigationProperty) -> Unit, navigateToEdit: (EntitySet) -> Unit, navigateUp: () -> Unit, viewModel: ODataViewModel
    ) -> Unit
) {
	AttachmentSet(
		EntitySets.attachmentSet,
		AttachmentEntitiesScreen,
		AttachmentEntityEditScreen,
	    AttachmentEntityDetailScreen
	),
	CustomerSet(
		EntitySets.customerSet,
		CustomerEntitiesScreen,
		CustomerEntityEditScreen,
	    CustomerEntityDetailScreen
	),
	HeaderContactSet(
		EntitySets.headerContactSet,
		HeaderContactEntitiesScreen,
		HeaderContactEntityEditScreen,
	    HeaderContactEntityDetailScreen
	),
	HeaderQuerySet(
		EntitySets.headerQuerySet,
		HeaderQueryEntitiesScreen,
		HeaderQueryEntityEditScreen,
	    HeaderQueryEntityDetailScreen
	),
	HeaderSet(
		EntitySets.headerSet,
		HeaderEntitiesScreen,
		HeaderEntityEditScreen,
	    HeaderEntityDetailScreen
	),
	ItemSet(
		EntitySets.itemSet,
		ItemEntitiesScreen,
		ItemEntityEditScreen,
	    ItemEntityDetailScreen
	),
	PartnerSet(
		EntitySets.partnerSet,
		PartnerEntitiesScreen,
		PartnerEntityEditScreen,
	    PartnerEntityDetailScreen
	),
	PricingSet(
		EntitySets.pricingSet,
		PricingEntitiesScreen,
		PricingEntityEditScreen,
	    PricingEntityDetailScreen
	),
}

val entityTypeNameSetMap = EntitySetScreenInfo.values().associate {
    Pair(
        it.entitySet.entityType.localName, it.entitySet
    )
}

fun getEntitySetScreenInfo(entitySet: EntitySet): EntitySetScreenInfo =
    EntitySetScreenInfo.values().first { it.entitySet == entitySet }

fun getEntityScreens(entitySet: EntitySet): EntityScreens? =
    EntityScreens.values().firstOrNull() { it.entitySet == entitySet }

enum class ScreenType {
    List, Details, Update, Create, NavigatedList
}

@Composable
fun screenTitle(entitySetScreenInfo: EntitySetScreenInfo, screenType: ScreenType): String {
    return when (screenType) {
        //TODO: navigated list title?
        ScreenType.List, ScreenType.NavigatedList -> stringResource(id = entitySetScreenInfo.setTitleId)
        ScreenType.Details -> stringResource(id = entitySetScreenInfo.itemTitleId)
        ScreenType.Update -> stringResource(id = R.string.title_update_fragment) + " ${
            stringResource(
                id = entitySetScreenInfo.itemTitleId
            )
        }"
        ScreenType.Create -> stringResource(
            id = R.string.title_create_fragment,
            stringResource(id = entitySetScreenInfo.itemTitleId)
        )
    }
}
