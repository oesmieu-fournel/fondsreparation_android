package com.ecosystem.mobile.reparation.dev.ui

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.ecosystem.mobile.reparation.dev.app.SAPWizardApplication
import com.ecosystem.mobile.reparation.dev.flutter_channel.order.OrderChannelHandler
import com.ecosystem.mobile.reparation.dev.service.api.mobile_service.RetrieveUserInfos
import com.ecosystem.mobile.reparation.dev.ui.push_notifications.PushNotificationAlertDialog
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.remotenotification.PushRemoteMessage
import com.sap.cloud.mobile.foundation.remotenotification.BasePushService
import com.sap.cloud.mobile.foundation.remotenotification.FirebasePushService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.slf4j.LoggerFactory

class CustomFlutterActivity : FlutterActivity() {
    private val CHANNEL = "flutter/order"
    private var currentUserId: String = "UNKNOWN"
    private lateinit var orderChannelHandler: OrderChannelHandler

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }


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

    override fun onResume() {
        super.onResume()
        (application as SAPWizardApplication)?.pushRemoteMessage?.let {
            PushNotificationAlertDialog()(
                this,
                it,
                (application as SAPWizardApplication).firebasePushService
            ).show()

        }
        (application as SAPWizardApplication)?.pushRemoteMessage = null
    }

    companion object {
        internal val logger = LoggerFactory.getLogger(CustomFlutterActivity::class.java)
    }
}