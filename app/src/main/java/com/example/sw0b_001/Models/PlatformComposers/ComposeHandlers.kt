package com.example.sw0b_001.Models.PlatformComposers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.PublisherHandler
import com.example.sw0b_001.Models.SMSHandler
import com.example.sw0b_001.Models.ThreadExecutorPool

object ComposeHandlers {

    fun compose(context: Context, formattedContent: String, platforms: Platforms,
                onSuccessRunnable: Runnable) {
        val encryptedContentBase64 = PublisherHandler
                .formatForPublishing(context, formattedContent)
        Log.d(javaClass.name, "Final content: ${encryptedContentBase64}")
        val gatewayClientMSISDN = GatewayClientsCommunications(context)
                .getDefaultGatewayClient()

        try {
            val sentIntent = SMSHandler.transferToDefaultSMSApp(context, gatewayClientMSISDN!!,
                    encryptedContentBase64).apply {
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(sentIntent)

            val encryptedContent = EncryptedContent()
            encryptedContent.encryptedContent = formattedContent
            encryptedContent.date = System.currentTimeMillis()
            encryptedContent.type = platforms.type
            encryptedContent.platformId = platforms.id
            encryptedContent.platformName = platforms.name

            ThreadExecutorPool.executorService.execute {
                Datastore.getDatastore(context).encryptedContentDAO()
                        .insert(encryptedContent)
                onSuccessRunnable.run()
            }
        } catch(e: Exception) {
            Log.e(javaClass.name, "Exception finding package", e)
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}