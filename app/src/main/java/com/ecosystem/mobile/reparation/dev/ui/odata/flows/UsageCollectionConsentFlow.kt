package com.ecosystem.mobile.reparation.dev.ui.odata.flows

import android.app.Activity
import android.content.Context
import com.sap.cloud.mobile.flows.compose.core.ConsentType
import com.sap.cloud.mobile.flows.compose.flows.BaseFlow
import com.sap.cloud.mobile.onboarding.compose.screens.ConsentScreen

class UsageCollectionConsentFlow(context: Context) :
    BaseFlow(context, "UsageCollectionConsentFlow") {
    override fun initialize() {
        addSingleStep("step_UsageCollectionConsent") {
            ConsentScreen(consentType = ConsentType.USAGE.name, agreeButtonClickListener = {
                flowDone("step_UsageCollectionConsent")
            }, disagreeButtonClickListener = {
                terminateFlow(resultCode = Activity.RESULT_CANCELED)
            })
        }
    }

}
