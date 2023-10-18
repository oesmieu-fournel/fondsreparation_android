package com.ecosystem.mobile.reparation.ui

import androidx.annotation.NonNull
import com.ecosystem.mobile.reparation.flutter_channel.order.OrderChannelHandler
import com.ecosystem.mobile.reparation.service.api.mobile_service.RetrieveUserInfos
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class CustomFlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/order"
    private var currentUserId: String = "UNKNOWN"
    private lateinit var orderChannelHandler : OrderChannelHandler


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        currentUserId = RetrieveUserInfos(this@CustomFlutterActivity).userName()
        orderChannelHandler = OrderChannelHandler(currentUserId)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            orderChannelHandler.onMethodCall(call, result)
        }
    }

    companion object {
        internal val logger = LoggerFactory.getLogger(CustomFlutterActivity::class.java)
    }
}