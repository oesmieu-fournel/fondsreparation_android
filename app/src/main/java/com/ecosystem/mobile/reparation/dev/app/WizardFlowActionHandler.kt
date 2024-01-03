package com.ecosystem.mobile.reparation.dev.app

import android.app.Activity
import android.text.SpannedString
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.getSpans
import com.ecosystem.mobile.reparation.dev.BuildConfig
import com.sap.cloud.mobile.fiori.compose.dialog.FioriAlertDialog
import com.sap.cloud.mobile.fiori.compose.theme.fioriHorizonAttributes
import com.ecosystem.mobile.reparation.dev.R
import com.sap.cloud.mobile.flows.compose.ext.CustomStepInsertionPoint
import com.sap.cloud.mobile.flows.compose.ext.FlowActionHandler
import com.sap.cloud.mobile.flows.compose.flows.BaseFlow
import com.sap.cloud.mobile.flows.compose.flows.FlowType
import com.sap.cloud.mobile.foundation.ext.SDKCustomTabsLauncher
import com.sap.cloud.mobile.onboarding.compose.screens.LaunchScreen
import com.sap.cloud.mobile.onboarding.compose.screens.rememberLaunchScreenState
import com.sap.cloud.mobile.onboarding.compose.settings.LocalScreenSettings

class WizardFlowActionHandler(val application: com.ecosystem.mobile.reparation.dev.app.SAPWizardApplication) : FlowActionHandler() {
    private var showDemoDialog by mutableStateOf(false)


    @Composable
    private fun getAnnotatedString(rId: Int): AnnotatedString {
        val context = LocalContext.current
        val settings = LocalScreenSettings.current.launchScreenSettings
        val spannedString = context.getText(rId) as SpannedString
        val annotations = spannedString.getSpans<android.text.Annotation>(0, spannedString.length)
        val annotatedString = buildAnnotatedString {
            append(stringResource(rId))
            annotations.forEach { annotation ->
                val start = spannedString.getSpanStart(annotation)
                val end = spannedString.getSpanEnd(annotation)
                if (annotation.key == "key") {
                    when (annotation.value) {
                        "eula" -> {
                            addStringAnnotation(
                                tag = annotation.value,
                                annotation = settings.eulaUrl,
                                start = start, end = end
                            )
                        }

                        "term" -> {
                            addStringAnnotation(
                                tag = annotation.value,
                                annotation = settings.privacyPolicyUrl,
                                start = start, end = end
                            )
                        }
                    }
                }
                addStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.fioriHorizonAttributes.SapFioriColorT4
                    ), start, end
                )
            }
        }

        return annotatedString
    }

    @Composable
    private fun PrivacyDialogContent(annotatedString: AnnotatedString) {
        val context = LocalContext.current
        ClickableText(
            text = annotatedString,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            style = MaterialTheme.fioriHorizonAttributes.textAppearanceBody1.copy(
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    start = offset, end = offset
                ).firstOrNull()?.also { annotation ->
                    if (SDKCustomTabsLauncher.customTabsSupported(context)) {
                        SDKCustomTabsLauncher.launchCustomTabs(context, annotation.item)
                    }
                }
            },
        )
    }

    override fun getFlowCustomizationSteps(
        flow: BaseFlow,
        insertionPoint: CustomStepInsertionPoint
    ) {
        if (flow.flowName == FlowType.Onboarding.name) {
            when (insertionPoint) {
                CustomStepInsertionPoint.BeforeEula -> {
                    flow.addSingleStep(step_welcome, secure = false) {
                        val context = LocalContext.current
                        val settings = LocalScreenSettings.current.launchScreenSettings
                        val state = rememberLaunchScreenState(
                            showTermLinks = true,
                            defaultAgreeStatus = false
                        )
                        CustomLaunchScreen(
                            primaryViewClickListener = {
                                flow.flowDone(step_welcome)
                            },
                            secondaryViewClickListener = {
                                showDemoDialog = true
                            },
                            state = state,
                            launchScreenSettings = settings
                        )
                        if (showDemoDialog) {
                            FioriAlertDialog(
                                title = context.getString(R.string.launch_screen_demo_dialog_title),
                                text = context.getString(R.string.launch_screen_demo_dialog_message),
                                confirmButtonText = context.getString(R.string.launch_screen_demo_dialog_button_goback),
                                onConfirmButtonClick = {
                                    showDemoDialog = false
                                }
                            )
                        }
                    }
                }

                else -> Unit
            }
        }
    }


    @Composable
    fun CustomLaunchScreen(
        state: com.sap.cloud.mobile.onboarding.compose.screens.LaunchScreenState,
        primaryViewClickListener: com.sap.cloud.mobile.onboarding.compose.settings.ViewClickListener,
        secondaryViewClickListener: com.sap.cloud.mobile.onboarding.compose.settings.ViewClickListener?,
        launchScreenSettings: com.sap.cloud.mobile.onboarding.compose.settings.LaunchScreenSettings?,
    ): kotlin.Unit {
        Image(
            painter = painterResource(id = R.drawable.ic_half_grey_circle),
            contentDescription = stringResource(R.string.half_grey_circle)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.portail_reparateurs_picto),
                contentDescription = stringResource(R.string.reparateur_picto),
                modifier = Modifier
                    .padding(vertical = 50.dp)
                    .size(250.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.portail_reparateurs_logo),
                contentDescription = stringResource(R.string.reparateur_logo)
            )
            Text(text = BuildConfig.VERSION_NAME)
            Button(
                onClick = { primaryViewClickListener() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x0FF002E50),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(vertical = 50.dp)
            ) {
                Text(text = stringResource(R.string.se_connecter))
            }
            val annotatedString = buildAnnotatedString {
                val text = "Retrouvez nos CGU"
                val startIndex = text.indexOf("CGU")
                val endIndex = startIndex + 3
                append(text)
                addStyle(
                    style = SpanStyle(
                        fontSize = 12.sp,
                    ), start = 0, end = text.length - 1
                )
                addStyle(
                    style = SpanStyle(
                        color = Color(0xff64B5F6),
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.Underline
                    ), start = startIndex, end = endIndex
                )

                addStringAnnotation(
                    tag = "URL",
                    annotation = launchScreenSettings?.eulaUrl ?: "",
                    start = startIndex,
                    end = endIndex
                )
            }

            val uriHandler = LocalUriHandler.current
            ClickableText(text = annotatedString, onClick = {
                annotatedString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        try {
                            uriHandler.openUri(stringAnnotation.item)
                        } catch (e: Exception) {
                            // handle it
                        }
                    }
            })
        }
    }

    override fun shouldStartTimeoutFlow(activity: Activity): Boolean = when (activity) {
        else -> super.shouldStartTimeoutFlow(activity)
    }

    companion object {
        private const val step_welcome = "step_custom_welcome"
    }

}
