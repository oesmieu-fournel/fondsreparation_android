package com.ecosystem.mobile.reparation.flutter_channel.order

import com.ecosystem.mobile.reparation.model.files.FilesInformations
import com.ecosystem.mobile.reparation.repository.Repository
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Header
import com.sap.cloud.mobile.odata.ToJSON
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class OrderChannelHandler(val currentUserId : String) : MethodChannel.MethodCallHandler {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val customRepository = RepositoryFactory.customRepository
    private val filesRepository = RepositoryFactory.filesRepository
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getOrdersList" -> {
                getOrdersList(call, result)
            }

            "getOrder" -> {
               getOrder(call, result)
            }

            "getPartners" -> {
                getPartners(result)
            }

            "getProducts" -> {
                getProducts(result)
            }

            "createUpdateOrder" -> {
                createUpdateOrder(call, result)
            }


            "updateOrderStatus" -> {
                updateOrderStatus(call, result)
            }
            "uploadFiles" -> {
                uploadFiles(call, result)
            }

            else ->
                result.notImplemented()
        }
    }

    private fun uploadFiles(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        scope.launch {
            call.argument<String>("serviceOrder")?.let { updatedOrder ->
                val filesInformationJson = call.argument<String>("filesInformation")
                try {
                    val gson = Gson()
                    val filesInformation = gson.fromJson(
                        filesInformationJson,
                        FilesInformations::class.java
                    )
                    val filesInformationUpdated =
                        filesRepository.uploadFiles(updatedOrder, filesInformation)
                    result.success(
                        gson.toJson(filesInformationUpdated)
                    )

                } catch (exception: JsonSyntaxException) {
                    result.error(
                        "400",
                        "Error during attachments upload",
                        null
                    )
                }
            }
        }
    }

    private fun updateOrderStatus(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        scope.launch {
            val updatedOrder = call.argument<String>("serviceOrder")
            val status = call.argument<String>("status")

            if (updatedOrder != null && status != null) {
                when (val creatingResult =
                    customRepository.updateHeaderStatus(updatedOrder, status)) {
                    is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                        result.success(
                            ToJSON.entity(
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

    private fun createUpdateOrder(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        scope.launch {
            val newOrder = call.argument<String>("updatedOrder")?.let {
                when (val creatingResult =
                    customRepository.suspendCreateUpdateHeader(it)) {
                    is Repository.SuspendOperationResult.SuspendOperationSuccess -> {
                        result.success(
                            ToJSON.entity(
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

    private fun getProducts(result: MethodChannel.Result) {
        scope.launch {
            val productsFlow = customRepository.readProducts()
            productsFlow.collectLatest {
                val productsJson = ToJSON.entityList(it)
                result.success(productsJson)
            }
        }
    }

    private fun getPartners(result: MethodChannel.Result) {
        scope.launch {
            val partnersFlow = customRepository.readPartner(currentUserId)
            partnersFlow.collectLatest {
                val partnersJson = ToJSON.entity(it)
                result.success(partnersJson)
            }
        }
    }

    private fun getOrder(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        scope.launch {
            val serviceOrder = call.argument<String>("serviceOrder") ?: ""
            val headerFlow = customRepository.readHeader(serviceOrder)
            headerFlow.collectLatest {
                val headerJson = ToJSON.entity(it)
                result.success(headerJson)
            }
        }
    }

    private fun getOrdersList(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        val page = call.argument<Int>("page") ?: 0
        scope.launch {
            val headerListFlow =
                customRepository.readHeaderList(currentUserId, page = page)
            headerListFlow.collectLatest {
                val headListJson = ToJSON.entityList(it)
                result.success(headListJson)
            }
        }
    }
}