package com.ecosystem.mobile.reparation.dev.ui.odata.flows

import android.app.Activity
import android.content.Context
import com.sap.cloud.mobile.flows.compose.core.ConsentType
import com.sap.cloud.mobile.flows.compose.flows.BaseFlow
import com.sap.cloud.mobile.onboarding.compose.screens.ConsentScreen

class CrashReportCollectionConsentFlow(context: Context) :
    BaseFlow(context, "CrashReportCollectionConsentFlow") {
    override fun initialize() {
        addSingleStep("step_UsageCollectionConsent") {
            ConsentScreen(consentType = ConsentType.CRASH_REPORT.name, agreeButtonClickListener = {
                flowDone("step_UsageCollectionConsent")
            }, disagreeButtonClickListener = {
                terminateFlow(resultCode = Activity.RESULT_CANCELED)
            })
        }
    }

}
