package com.ecosystem.mobile.reparation.ui.odata

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.ODataViewModel
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata;
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.attachment.AttachmentODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.customer.CustomerODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.headercontact.HeaderContactODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.headerquery.HeaderQueryODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.header.HeaderODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.item.ItemODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.partner.PartnerODataViewModel
import com.ecosystem.mobile.reparation.ui.odata.viewmodel.pricing.PricingODataViewModel
import com.sap.cloud.mobile.odata.EntitySet
import com.sap.cloud.mobile.odata.EntityValue
import com.sap.cloud.mobile.odata.Property

class ODataEntityViewModelFactory(
    private val application: Application,
    private val entitySet: EntitySet,
    private val orderByProperty: Property?,
    private val parent: EntityValue? = null,
    private val navigationPropertyName: String? = null,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (entitySet) {
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.attachmentSet -> AttachmentODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.customerSet -> CustomerODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerContactSet -> HeaderContactODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet -> HeaderQueryODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerSet -> HeaderODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.itemSet -> ItemODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.partnerSet -> PartnerODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
            else -> PricingODataViewModel(
                application,
                orderByProperty,
                parent,
                navigationPropertyName
            ) as T
        }
    }
}
