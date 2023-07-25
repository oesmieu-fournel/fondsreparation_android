package com.ecosystem.mobile.reparation.ui

import androidx.annotation.NonNull
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.sap.cloud.android.odata.z_api_service_order_srv_entities.Z_API_SERVICE_ORDER_SRV_EntitiesMetadata
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class CustomFlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/request"
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository = RepositoryFactory.getRepository(Z_API_SERVICE_ORDER_SRV_EntitiesMetadata.EntitySets.headerQuerySet, null )


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getRequestsList" -> {
                    scope.launch {
                        val requestList = repository.read()
                        requestList.collectLatest {
                            val requestJsonList = com.sap.cloud.mobile.odata.ToJSON.entityList(it)
                            result.success(requestJsonList)
                        }
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