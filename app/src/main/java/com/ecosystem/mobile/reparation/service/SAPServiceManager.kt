package com.ecosystem.mobile.reparation.service

import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_Entities
import com.sap.cloud.android.odata.z_business_partner_srv_entities.Z_BUSINESS_PARTNER_SRV_Entities
import com.sap.cloud.android.odata.z_api_produits_marques_srv_entities.Z_API_PRODUITS_MARQUES_SRV_Entities
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.odata.OnlineODataProvider
import com.sap.cloud.mobile.odata.http.OKHttpHandler

object SAPServiceManager {
	
    private const val CONNECTION_ID_Z_API_SERVICE_ORDER_SRV_ENTITIES: String = "com.ecosystem.mobile.reparation"
    private const val CONNECTION_ID_Z_BUSINESS_PARTNER_SRV_ENTITIES: String = "com.ecosystem.mobile.bp"
    private const val CONNECTION_ID_Z_API_PRODUITS_MARQUES_SRV_ENTITIES: String = "com.ecosystem.mobile.product"
	
	private lateinit var appConfig: AppConfig
	fun initSAPServiceManager(config: AppConfig) {
	        appConfig = config
	}

    var serviceRoot: String = ""
        private set
        get() {
            return (z_API_SERVICE_ORDER_SRV_Entities?.provider as OnlineODataProvider).serviceRoot
        }

    var z_API_SERVICE_ORDER_SRV_Entities: Z_API_SERVICE_ORDER_SRV_Entities? = null
        private set
        get() {
            return field ?: throw IllegalStateException("SAPServiceManager was not initialized")
        }
    var z_BUSINESS_PARTNER_SRV_Entities: Z_BUSINESS_PARTNER_SRV_Entities? = null
        private set
        get() {
            return field ?: throw IllegalStateException("SAPServiceManager was not initialized")
        }
    var z_API_PRODUITS_MARQUES_SRV_Entities: Z_API_PRODUITS_MARQUES_SRV_Entities? = null
        private set
        get() {
            return field ?: throw IllegalStateException("SAPServiceManager was not initialized")
        }

    fun openODataStore(callback: () -> Unit = {}) {
		appConfig.serviceUrl.let { _serviceURL ->
		    z_API_SERVICE_ORDER_SRV_Entities = Z_API_SERVICE_ORDER_SRV_Entities (
		        OnlineODataProvider("SAPService", _serviceURL + CONNECTION_ID_Z_API_SERVICE_ORDER_SRV_ENTITIES).apply {
		            networkOptions.httpHandler = OKHttpHandler(ClientProvider.get())
		            serviceOptions.checkVersion = false
		            serviceOptions.requiresType = true
		            serviceOptions.cacheMetadata = false
		        }
		    )
		    z_BUSINESS_PARTNER_SRV_Entities = Z_BUSINESS_PARTNER_SRV_Entities (
		        OnlineODataProvider("SAPService", _serviceURL + CONNECTION_ID_Z_BUSINESS_PARTNER_SRV_ENTITIES).apply {
		            networkOptions.httpHandler = OKHttpHandler(ClientProvider.get())
		            serviceOptions.checkVersion = false
		            serviceOptions.requiresType = true
		            serviceOptions.cacheMetadata = false
		        }
		    )
		    z_API_PRODUITS_MARQUES_SRV_Entities = Z_API_PRODUITS_MARQUES_SRV_Entities (
		        OnlineODataProvider("SAPService", _serviceURL + CONNECTION_ID_Z_API_PRODUITS_MARQUES_SRV_ENTITIES).apply {
		            networkOptions.httpHandler = OKHttpHandler(ClientProvider.get())
		            serviceOptions.checkVersion = false
		            serviceOptions.requiresType = true
		            serviceOptions.cacheMetadata = false
		        }
		    )
		}
        callback.invoke()
    }

}
