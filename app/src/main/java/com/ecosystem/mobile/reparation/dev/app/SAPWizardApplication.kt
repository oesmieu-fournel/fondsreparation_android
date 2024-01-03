package com.ecosystem.mobile.reparation.dev.app

import android.app.Application
import android.util.Log
import com.ecosystem.mobile.reparation.dev.BuildConfig
import com.ecosystem.mobile.reparation.dev.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.dev.repository.RepositoryFactory
import com.sap.cloud.mobile.foundation.logging.LoggingService
import com.sap.cloud.mobile.foundation.mobileservices.MobileService
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer
import com.sap.cloud.mobile.foundation.mobileservices.ServiceListener
import com.sap.cloud.mobile.foundation.mobileservices.ServiceResult
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import com.sap.cloud.mobile.foundation.theme.ThemeDownloadService
import com.sap.cloud.mobile.foundation.user.DeviceUser
import com.sap.cloud.mobile.foundation.user.User
import com.sap.cloud.mobile.foundation.user.UserService
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.view.FlutterMain
import okhttp3.OkHttpClient


class SAPWizardApplication: Application() {

    internal var isApplicationUnlocked = false

    lateinit var httpClient : OkHttpClient
    lateinit var appConfig: AppConfig
    lateinit var flutterEngine : FlutterEngine


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

        SDKInitializer.start(this, * services.toTypedArray())
    }

    private fun initFlutterEngine(){
        // Instantiate a FlutterEngine.
        flutterEngine = FlutterEngine(this)

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            //DartExecutor.DartEntrypoint.createDefault()
            DartExecutor.DartEntrypoint(
                FlutterMain.findAppBundlePath(),
                BuildConfig.FLAVOR)
        )

        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
            .getInstance()
            .put("flutter_engine", flutterEngine)
    }


    companion object {
        const val KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference"
    }
}
