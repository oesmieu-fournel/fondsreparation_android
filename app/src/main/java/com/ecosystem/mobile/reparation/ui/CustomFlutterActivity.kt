package com.ecosystem.mobile.reparation.ui

import android.widget.Toast
import androidx.annotation.NonNull
import com.ecosystem.mobile.reparation.app.SAPWizardApplication
import com.ecosystem.mobile.reparation.repository.Repository
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.ecosystem.mobile.reparation.service.mobile_service.RetrieveUser
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header
import com.sap.cloud.mobile.flows.compose.core.FlowContextRegistry
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class CustomFlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/order"
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val customRepository = RepositoryFactory.customRepository
    private var currentUserId : String = "UNKNOWN"


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        scope.launch {
            currentUserId = RetrieveUser(this@CustomFlutterActivity).userName()
        }

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getOrdersList" -> {

                    val page = call.argument<Int>("page") ?: 0
                    scope.launch {
                        val headerListFlow = customRepository.readHeaderList(currentUserId, page = page)
                        headerListFlow.collectLatest {
                            val headListJson = com.sap.cloud.mobile.odata.ToJSON.entityList(it)
                            result.success(headListJson)
                        }
                    }
                }

                "getOrder" -> {
                    scope.launch {
                        val serviceOrder = call.argument<String>("serviceOrder") ?: ""
                        val headerFlow = customRepository.readHeader(serviceOrder)
                        headerFlow.collectLatest {
                            val headerJson = com.sap.cloud.mobile.odata.ToJSON.entity(it)
                            result.success(headerJson)
                        }
                    }
                }

                "getPartners" -> {
                    scope.launch {
                        val partnersFlow = customRepository.readPartner(currentUserId)
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

                "createUpdateOrder" -> {
                    scope.launch {
                        val newOrder = call.argument<String>("updatedOrder")?.let {
                            when (val creatingResult =
                                customRepository.suspendCreateUpdateHeader(it)) {
                                is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                                    result.success(
                                        com.sap.cloud.mobile.odata.ToJSON.entity(
                                            creatingResult.newEntity as Header
                                        )
                                    )
                                }

                                is Repository.SuspendOperationResult.SuspendOperationFail -> {
                                    result.error(
                                        "400",
                                        creatingResult.error.localizedMessage
                                            ?: "Error during update",
                                        null
                                    )
                                }
                            }

                        }
                    }
                }


                "updateOrderStatus" -> {
                    scope.launch {
                        val updatedOrder = call.argument<String>("serviceOrder")
                        val status = call.argument<String>("status")

                        if (updatedOrder != null && status != null) {
                            when (val creatingResult =
                                customRepository.updateHeaderStatus(updatedOrder, status)) {
                                is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                                    result.success(
                                        com.sap.cloud.mobile.odata.ToJSON.entity(
                                            creatingResult.newEntity as Header
                                        )
                                    )
                                }

                                is Repository.SuspendOperationResult.SuspendOperationFail -> {
                                    result.error(
                                        "400",
                                        creatingResult.error.localizedMessage
                                            ?: "Error during update",
                                        null
                                    )
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


    private fun makeToast() {
        Toast.makeText(
            application,
            "SCM user not ready yet.",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        internal val logger = LoggerFactory.getLogger(CustomFlutterActivity::class.java)
    }
}