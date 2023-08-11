package com.ecosystem.mobile.reparation.ui

import androidx.annotation.NonNull
import com.ecosystem.mobile.reparation.app.SAPWizardApplication
import com.ecosystem.mobile.reparation.repository.Repository
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.ecosystem.mobile.reparation.service.SAPServiceManager.z_API_SERVICE_ORDER_SRV_Entities
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header
import com.sap.cloud.mobile.odata.FromJSON
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class CustomFlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/request"
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val customRepository = RepositoryFactory.customRepository


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getRequestsList" -> {
                    val page = call.argument<Int>("page") ?: 0
                    scope.launch {
                        val headerListFlow = customRepository.readHeaderList("500009", page = page)
                        headerListFlow.collectLatest {
                            val headListJson = com.sap.cloud.mobile.odata.ToJSON.entityList(it)
                            result.success(headListJson)
                        }
                    }
                }

                "getRequest" -> {
                    scope.launch {
                        val serviceOrder = call.argument<String>("serviceOrder") ?: ""
                        val headerFlow = customRepository.readHeader(serviceOrder)
                        headerFlow.collectLatest {
                            val headerJson = com.sap.cloud.mobile.odata.ToJSON.entity(it)

                            /*val newHeaderFromJson = z_API_SERVICE_ORDER_SRV_Entities?.executeQuery(
                                FromJSON.entity(headerJson).from(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerSet).expand(Header.partners)
                            )?.requiredEntity as Header*//*
                            val newHeaderFromJson = z_API_SERVICE_ORDER_SRV_Entities?.getHeader(FromJSON.entity(headerJson).expand(Header.partners,Header.items, Header.customers))
                            val dummy = newHeaderFromJson
                            val partnersJson = newHeaderFromJson?.let {
                                    com.sap.cloud.mobile.odata.ToJSON.entityList(it.partners)
                            } ?: ""
                            val partnersSet = z_API_SERVICE_ORDER_SRV_Entities?.getPartnerSet(FromJSON.entityList(partnersJson))
                            newHeaderFromJson?.partners = partnersSet ?: listOf()
                            when (val creatingResult = customRepository.suspendCreateUpdateHeader(headerJson)) {
                                is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                                    val successMessage = "succès"
                                }

                                is Repository.SuspendOperationResult.SuspendOperationFail -> {
                                    val errorMessage = "error"
                                }
                            }
                            val partners = newHeaderFromJson?.partners
                            partners?.size*/
                            result.success(headerJson)
                        }
                    }
                }

                "getPartners" -> {
                    scope.launch {
                        val partnersFlow = customRepository.readPartner("500009")
                        partnersFlow.collectLatest {
                            val partnersJson = com.sap.cloud.mobile.odata.ToJSON.entity(it)
                            result.success(partnersJson)
                        }
                    }
                }

                "getProducts" -> {
                    scope.launch {
                        val productsFlow = customRepository.readProducts()
                        productsFlow.collectLatest {
                            val productsJson = com.sap.cloud.mobile.odata.ToJSON.entityList(it)
                            result.success(productsJson)
                        }
                    }
                }

                "createUpdateRequest" -> {
                    scope.launch {
                        val newRequest = call.argument<String>("updatedRequest")?.let {
                            when (val creatingResult = customRepository.suspendCreateUpdateHeader(it)) {
                                is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                                    result.success(
                                        com.sap.cloud.mobile.odata.ToJSON.entity(
                                            creatingResult.newEntity as Header
                                        )
                                    )
                                }

                                is Repository.SuspendOperationResult.SuspendOperationFail -> {
                                    result.error("400", creatingResult.error.localizedMessage ?: "Error during update", null)
                                }
                            }

                        }
                    }
                }

                "getCookies" -> {

                    val application = (application as SAPWizardApplication)
                    val httpClient = application.httpClient
                    val cookieJar = httpClient.cookieJar
                    application.appConfig.serviceUrl.toHttpUrlOrNull()?.let {
                        var cookies = ""
                        cookieJar.loadForRequest(it).forEach { cookie ->
                            cookies += cookie.toString()
                        }
                        result.success(cookies)
                    }
                }


                else ->
                    result.notImplemented()
            }
        }
    }

    companion object {
        internal val logger = LoggerFactory.getLogger(CustomFlutterActivity::class.java)
    }
}