package com.ecosystem.mobile.reparation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ecosystem.mobile.reparation.BuildConfig
import com.ecosystem.mobile.reparation.R
import com.ecosystem.mobile.reparation.app.SAPWizardApplication
import com.ecosystem.mobile.reparation.app.WizardFlowActionHandler
import com.ecosystem.mobile.reparation.app.WizardFlowStateListener
import com.ecosystem.mobile.reparation.service.SAPServiceManager
import com.sap.cloud.mobile.fiori.compose.common.PainterBuilder
import com.sap.cloud.mobile.foundation.common.ClientProvider
import com.sap.cloud.mobile.flows.compose.core.FlowContext
import com.sap.cloud.mobile.flows.compose.ext.FlowOptions
import com.sap.cloud.mobile.flows.compose.flows.FlowUtil
import com.sap.cloud.mobile.foundation.configurationprovider.FileConfigurationProvider
import com.sap.cloud.mobile.foundation.configurationprovider.ProviderConfiguration
import com.sap.cloud.mobile.foundation.configurationprovider.ProviderInputs
import com.sap.cloud.mobile.foundation.mobileservices.ApplicationStates
import com.sap.cloud.mobile.foundation.mobileservices.TimeoutLockService
import com.sap.cloud.mobile.foundation.model.AppConfig
import com.sap.cloud.mobile.onboarding.compose.settings.CustomScreenSettings
import com.sap.cloud.mobile.onboarding.compose.settings.EulaScreenSettings
import com.sap.cloud.mobile.onboarding.compose.settings.LaunchScreenContentSettings
import com.sap.cloud.mobile.onboarding.compose.settings.LaunchScreenSettings
import com.sap.cloud.mobile.onboarding.compose.settings.QRCodeReaderScreenSettings
import io.flutter.embedding.android.FlutterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class WelcomeActivity : ComponentActivity() {

    private lateinit var providerConfiguration: ProviderConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            var showError by remember { mutableStateOf(false) }
            if (showError) {
                val errorMessage = providerConfiguration.returnError?.errorMessage
                val dialogMessage = if (errorMessage != null) stringResource(
                    R.string.config_loader_on_error_description,
                    "com.sap.configuration.provider.fileconfiguration",
                    errorMessage
                ) else stringResource(R.string.config_loader_complete_error_description)

                AlertDialogComponent(
                    text = dialogMessage,
                    onPositiveButtonClick = {
                        this.finish()
                    }
                )
            }

            lifecycleScope.launch(Dispatchers.Default) {
                providerConfiguration = loadConfiguration(this@WelcomeActivity)
                if (providerConfiguration.providerSuccess) {
                    val appConfig =
                        AppConfig.createAppConfigFromJsonString(providerConfiguration.configuration.toString())
                    startOnboarding(this@WelcomeActivity, appConfig)
                } else {
                    showError = true
                }
            }
        }
    }

    companion object {
        internal val logger = LoggerFactory.getLogger(WelcomeActivity::class.java)
    }

}

private fun loadConfiguration(context: Context): ProviderConfiguration {
    return FileConfigurationProvider(
        context, BuildConfig.FILE_CONFIGURATION
    ).provideConfiguration(
        ProviderInputs()
    )
}

fun startOnboarding(context: Context, appConfig: AppConfig) {
    TimeoutLockService.updateApplicationLockState(true)
    WelcomeActivity.logger.debug("Before starting flow, lock state: {}", ApplicationStates.applicationLocked)
    FlowUtil.startFlow(
        context,
        flowContext = getOnboardingFlowContext(context, appConfig)
    ) { resultCode, _ ->
        if (resultCode == Activity.RESULT_OK) {
			SAPServiceManager.openODataStore() {
                context.startActivity(
                    FlutterActivity.CachedEngineIntentBuilder(
                        CustomFlutterActivity::class.java,
                        "flutter_engine"
                    ).build(context) )
                //context.startActivity(FlutterActivity.NewEngineIntentBuilder(CustomFlutterActivity::class.java,).build(context))
			}
			PainterBuilder.setupImageLoader(
                context, ClientProvider.get()
            )
            WelcomeActivity.logger.debug("After flow, lock state: {}",  ApplicationStates.applicationLocked)
        } else startOnboarding(context, appConfig)
    }
}

private fun prepareScreenSettings() =
    CustomScreenSettings(
        launchScreenSettings = LaunchScreenSettings(
            titleResId = R.string.application_name,
            contentSettings = LaunchScreenContentSettings(
                contentImage = R.drawable.graphic_cloud_3x,
            ),
            eulaUrl = "https://portail-reparateurs.ecosystem.eco/cgu",
            bottomPrivacyUrl = "http://www.sap.com"
        ),
        qrCodeReaderScreenSettings = QRCodeReaderScreenSettings(
            scanInternal = 50L
        ),
        eulaSettings = EulaScreenSettings(eulaUrl = "file:///android_asset/CGU_Mobile.html")
    )

/** Returns the flow context for onboarding.*/
fun getOnboardingFlowContext(context: Context, appConfig: AppConfig) = FlowContext(
    appConfig = appConfig,
    flowActionHandler = WizardFlowActionHandler(context.applicationContext as SAPWizardApplication),
    flowStateListener = WizardFlowStateListener(context.applicationContext as SAPWizardApplication),
    flowOptions = FlowOptions(
//                oAuthAuthenticationOption = OAuth2WebOption.WEB_VIEW,
        useDefaultEulaScreen = true,
        screenSettings = prepareScreenSettings(),
        fullScreen = false
    )
)


