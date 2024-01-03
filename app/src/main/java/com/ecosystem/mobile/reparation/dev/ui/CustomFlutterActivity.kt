package com.ecosystem.mobile.reparation.dev.ui

import androidx.annotation.NonNull
import com.ecosystem.mobile.reparation.dev.flutter_channel.order.OrderChannelHandler
import com.ecosystem.mobile.reparation.dev.service.api.mobile_service.RetrieveUserInfos
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.slf4j.LoggerFactory

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