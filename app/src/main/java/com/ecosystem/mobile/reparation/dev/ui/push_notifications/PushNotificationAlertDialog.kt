package com.ecosystem.mobile.reparation.dev.ui.push_notifications

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.sap.cloud.mobile.foundation.remotenotification.FirebasePushService
import com.sap.cloud.mobile.foundation.remotenotification.PushRemoteMessage

class PushNotificationAlertDialog {
    operator fun invoke(
            context: Context,
            message: PushRemoteMessage,
            pushService: FirebasePushService,
        ): AlertDialog.Builder = AlertDialog.Builder(context)
            .setMessage(
                message.alert?.replaceFirstChar { it.uppercaseChar() }
                    ?: "Attention, votre application sera disponible, veuillez ne pas essayer de synchroniser l'application pendant cette période.\n" +
                    "Merci de votre compréhension.",
            )
            .setTitle(
                message.title?.replaceFirstChar { it.uppercaseChar() }
                    ?: "MEP prévue aujourd'hui de 12H-14H",
            )
            .setPositiveButton("C'est noté !") { dialog, which ->
                message.notificationID?.let {
                    pushService.updatePushMessageStatus(
                        it,
                        PushRemoteMessage.NotificationStatus.CONSUMED
                    )
                }
            }
}