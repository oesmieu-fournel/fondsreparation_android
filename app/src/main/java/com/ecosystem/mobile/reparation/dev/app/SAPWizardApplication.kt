package com.ecosystem.mobile.reparation.dev.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import com.ecosystem.mobile.reparation.dev.BuildConfig
import com.ecosystem.mobile.reparation.dev.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.dev.repository.RepositoryFactory
import com.ecosystem.mobile.reparation.dev.ui.CustomFlutterActivity
import com.ecosystem.mobile.reparation.dev.ui.push_notifications.PushNotificationAlertDialog
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.logging.LoggingService
import com.sap.cloud.mobile.foundation.mobileservices.MobileService
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer
import com.sap.cloud.mobile.foundation.mobileservices.ServiceListener
import com.sap.cloud.mobile.foundation.mobileservices.ServiceResult
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.foundation.remotenotification.BasePushService
import com.sap.cloud.mobile.foundation.remotenotification.FirebasePushService
import com.sap.cloud.mobile.foundation.remotenotification.PushCallbackListener
import com.sap.cloud.mobile.foundation.remotenotification.PushRemoteMessage
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import com.sap.cloud.mobile.foundation.theme.ThemeDownloadService
import com.sap.cloud.mobile.foundation.usage.UsageService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.view.FlutterMain
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory


class SAPWizardApplication : Application() {

    internal var isApplicationUnlocked = false

    lateinit var httpClient: OkHttpClient
    lateinit var appConfig: AppConfig
    lateinit var flutterEngine: FlutterEngine
    var pushRemoteMessage: PushRemoteMessage? = null
    lateinit var firebasePushService: FirebasePushService


    override fun onCreate() {
        super.onCreate()
        initFlutterEngine()
        initServices()
    }

    /**
     * Clears all user-specific data and configuration from the application, essentially resetting it to its initial
     * state.
     *
     * If client code wants to handle the reset logic of a service, here is an example:
     *
     *   SDKInitializer.resetServices { service ->
     *       return@resetServices if( service is PushService ) {
     *           PushService.unregisterPushSync(object: CallbackListener {
     *               override fun onSuccess() {
     *               }
     *
     *               override fun onError(p0: Throwable) {
     *               }
     *           })
     *           true
     *       } else {
     *           false
     *       }
     *   }
     */
    suspend fun resetApplication() {
        isApplicationUnlocked = false
        SharedPreferenceRepository(this).resetSharedPreference()
        RepositoryFactory.reset()
        SDKInitializer.resetServices()

    }

    private fun initServices() {
        val services = mutableListOf<MobileService>()
        services.add(ThemeDownloadService(this))
        services.add(LoggingService(autoUpload = false).apply {
            policy = LogPolicy(logLevel = "WARN", entryExpiry = 0, maxFileNumber = 4)
            logToConsole = true
        })

        val usageService = UsageService().apply {
            setAutoSession(true)
        }
        services.add(usageService)

        firebasePushService = FirebasePushService().apply {
            isEnableAutoMessageHandling = true
            setPushCallbackListener(object : PushCallbackListener {
                override fun onReceive(context: Context, message: PushRemoteMessage) {
                    pushRemoteMessage = message
                }


            })
            backgroundNotificationInterceptor = object :
                BasePushService.BackgroundNotificationInterceptor {
                override fun onBackgroundNotificationMessageReceived(pushNotificationEvent: BasePushService.PushNotificationEvent) {
                    pushNotificationEvent.displayNotification(pushNotificationEvent.pushRemoteMessage)
                    pushRemoteMessage = pushNotificationEvent.pushRemoteMessage
                }
            }
            foregroundNotificationInterceptor =
                object : BasePushService.ForegroundNotificationInterceptor {
                    override fun onForegroundNotificationMessageReceived(pushNotificationEvent: BasePushService.PushNotificationEvent) {
                        if(pushNotificationEvent.pushRemoteMessage == null){
                            Log.d("SAPWizardApplication", "pushRemoteMessage is null")
                            return
                        }
                        val message = pushNotificationEvent.pushRemoteMessage!!
                        val foregroundActivity = AppLifecycleCallbackHandler.getInstance().activity as FlutterActivity?
                        //if we can display the push message in the foreground
                        if( foregroundActivity != null && foregroundActivity.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
                            foregroundActivity.runOnUiThread {
                                PushNotificationAlertDialog()(
                                    foregroundActivity,
                                    message,
                                    this@apply
                                ).show()
                            }
                            pushRemoteMessage = null
                        }
                        else{
                            //if we can't display the push message, we stock it to display it later
                            pushRemoteMessage = message
                        }


                    }

                }

        }
        services.add(firebasePushService)

        SDKInitializer.start(this, * services.toTypedArray())
    }
    private fun initFlutterEngine() {
        // Instantiate a FlutterEngine.
        flutterEngine = FlutterEngine(this)

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            //DartExecutor.DartEntrypoint.createDefault()
            DartExecutor.DartEntrypoint(
                FlutterMain.findAppBundlePath(),
                BuildConfig.FLAVOR
            )
        )

        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
            .getInstance()
            .put("flutter_engine", flutterEngine)
    }


    companion object {
        const val KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference"
        internal val logger = LoggerFactory.getLogger(SAPWizardApplication::class.java)
    }
}
