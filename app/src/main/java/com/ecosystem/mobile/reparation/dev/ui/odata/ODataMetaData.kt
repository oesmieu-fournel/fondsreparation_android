package com.ecosystem.mobile.reparation.dev.ui.odata

import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets;
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.Property

enum class ODataMetaData(
    val entitySet: EntitySet,
    val orderByProperty: Property?,
) {
	AttachmentSet(
		EntitySets.attachmentSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Attachment.serviceOrder)
	    ,
	CustomerSet(
		EntitySets.customerSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Customer.serviceOrder)
	    ,
	HeaderContactSet(
		EntitySets.headerContactSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderContact.repairNo)
	    ,
	HeaderQuerySet(
		EntitySets.headerQuerySet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.HeaderQuery.repairNo)
	    ,
	HeaderSet(
		EntitySets.headerSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header.serviceOrder)
	    ,
	ItemSet(
		EntitySets.itemSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Item.serviceOrder)
	    ,
	PartnerSet(
		EntitySets.partnerSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Partner.serviceOrder)
	    ,
	PricingSet(
		EntitySets.pricingSet, 
		com.sap.cloud.android.odata.z_api_service_order_srv_entities.Pricing.serviceOrder)
	    
}

fun getOrderByProperty(entitySet: EntitySet): Property? {
    return ODataMetaData.values().first { it.entitySet == entitySet }.orderByProperty
}
