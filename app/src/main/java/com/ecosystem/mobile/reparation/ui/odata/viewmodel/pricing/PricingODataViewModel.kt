package com.ecosystem.mobile.reparation.ui.odata.viewmodel.pricing

import android.app.Application
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata;
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.Property

class PricingODataViewModel(
    application: Application,
    private val orderByProperty: Property?,
    override val parent: EntityValue? = null,
    private val navigationPropertyName: String? = null,
) : ODataViewModel(
    application,
    Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.pricingSet,
    orderByProperty,
    parent,
    navigationPropertyName
) {

//    override fun getAvatarText(entity: EntityValue?): String {
//        val customer = entity as Customer?
//        return customer?.let { "${it.lastName?.first() ?: '?'}${it.firstName?.first() ?: '?'}" }
//            ?: "??"
//    }
}
