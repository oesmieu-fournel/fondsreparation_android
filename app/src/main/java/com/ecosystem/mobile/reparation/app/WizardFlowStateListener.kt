package com.ecosystem.mobile.reparation.app

import android.widget.Toast
import ch.qos.logback.classic.Level
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.data.SharedPreferenceRepository
import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.ecosystem.mobile.reparation.service.api.mobile_service.RetrieveUserInfos
import com.sap.cloud.mobile.fiori.compose.common.PainterBuilder
import com.sap.cloud.mobile.flows.compose.core.FlowContext
import com.sap.cloud.mobile.flows.compose.ext.FlowStateListener
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.foundation.settings.policies.ClientPolicies
import com.sap.cloud.mobile.foundation.settings.policies.LogPolicy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory


class WizardFlowStateListener(private val application: SAPWizardApplication) :
    FlowStateListener() {

    override suspend fun onAppConfigRetrieved(appConfig: AppConfig) {
        logger.debug("onAppConfigRetrieved: $appConfig")
        SAPServiceManager.initSAPServiceManager(appConfig)
        application.appConfig = appConfig
    }

    override suspend fun onApplicationReset() {
        this.application.resetApplication()
    }

    override suspend fun onHttpClientReady(httpClient: OkHttpClient) {
        super.onHttpClientReady(httpClient)
        application.httpClient = httpClient
    }

    override suspend fun onApplicationLocked() {
        super.onApplicationLocked()
        application.isApplicationUnlocked = false
    }

    override suspend fun onFlowFinished(flowName: String?) {
        flowName?.let {
            application.isApplicationUnlocked = true
        }

        SAPServiceManager.openODataStore()
        PainterBuilder.setupImageLoader(
            application, ClientProvider.get()
        )

    }

    override suspend fun onClientPolicyRetrieved(policies: ClientPolicies) {
        policies.logPolicy?.also { logSettings ->
            val preferenceRepository = SharedPreferenceRepository(application)
            val currentSettings =
                preferenceRepository.userPreferencesFlow.first().logSetting

            if (currentSettings.logLevel != logSettings.logLevel) {
                preferenceRepository.updateLogLevel(LogPolicy.getLogLevel(logSettings))

                AppLifecycleCallbackHandler.getInstance().activity?.let {
                    it.runOnUiThread {
                        val logString = when (LogPolicy.getLogLevel(logSettings)) {
                            Level.ALL -> application.getString(R.string.log_level_path)
                            Level.INFO -> application.getString(R.string.log_level_info)
                            Level.WARN -> application.getString(R.string.log_level_warning)
                            Level.ERROR -> application.getString(R.string.log_level_error)
                            Level.OFF -> application.getString(R.string.log_level_none)
                            else -> application.getString(R.string.log_level_debug)
                        }
                        Toast.makeText(
                            application,
                            String.format(
                                application.getString(R.string.log_level_changed),
                                logString
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        logger.info(
                            String.format(
                                application.getString(R.string.log_level_changed),
                                logString
                            )
                        )
                    }
                }
            }
        }
        RetrieveUserInfos(application).userName()

    }

    private fun makeToast() {
        Toast.makeText(
            application,
            "SCM user not ready yet.",
            Toast.LENGTH_SHORT
        ).show()
    }


    companion object {
        private val logger = LoggerFactory.getLogger(WizardFlowStateListener::class.java)
    }
}

fun FlowContext.isUserSwitch(): Boolean {
    return getPreviousUser()?.let {
        getCurrentUser() != it
    } ?: false
}
