package com.ecosystem.mobile.reparation.app

import android.app.Application
import com.ecosystem.mobile.reparation.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.repository.RepositoryFactory
import com.sap.cloud.mobile.foundation.mobileservices.MobileService
import com.sap.cloud.mobile.foundation.mobileservices.SDKInitializer
import com.sap.cloud.mobile.foundation.logging.LoggingService
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import com.sap.cloud.mobile.foundation.theme.ThemeDownloadService

class SAPWizardApplication: Application() {

    internal var isApplicationUnlocked = false


    override fun onCreate() {
        super.onCreate()
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


    companion object {
        const val KEY_LOG_SETTING_PREFERENCE = "key.log.settings.preference"
    }
}
