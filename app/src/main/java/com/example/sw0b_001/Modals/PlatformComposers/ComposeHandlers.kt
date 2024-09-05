package com.example.sw0b_001.Modals.PlatformComposers

import android.content.Context
import android.content.Intent
import android.os.Message
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.States
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.MessageComposer
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.PublisherHandler
import com.example.sw0b_001.Models.SMSHandler
import com.example.sw0b_001.Models.ThreadExecutorPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ComposeHandlers {

    fun compose(context: Context,
                formattedContent: String,
                platforms: AvailablePlatforms,
                storedPlatforms: StoredPlatformsEntity,
                onSuccessRunnable: Runnable) {
        val states = Datastore.getDatastore(context).ratchetStatesDAO().fetch()
        if(states.size > 1) {
            throw Exception("More than 1 states exist")
        }

        val state = if(states.isNotEmpty()) States(Base64.encodeToString(states.first().value,
            Base64.DEFAULT)) else States()
        val messageComposer = MessageComposer(context, state)
        val encryptedContentBase64 = messageComposer.compose(platforms, formattedContent)
        println("Final content: $encryptedContentBase64")

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
            encryptedContent.type = platforms.service_type
            encryptedContent.platformName = platforms.name
            encryptedContent.platformId = storedPlatforms.id

            Datastore.getDatastore(context).encryptedContentDAO()
                .insert(encryptedContent)
            onSuccessRunnable.run()
        } catch(e: Exception) {
            Log.e(javaClass.name, "Exception finding package", e)
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    data class DecomposedMessages(val body: String,
                                  val subject: String = "",
                                  val recipient: String = "")
    fun decompose(content: String, platforms: AvailablePlatforms) : DecomposedMessages {
        val split = content.split(":")
        return when(platforms.service_type) {
            Platforms.TYPE_EMAIL -> {
                DecomposedMessages(body = split[5], subject = split[4], recipient = split[1])
            }

            Platforms.TYPE_TEXT -> {
                DecomposedMessages(body = split[1])
            }

            else -> {
                DecomposedMessages(content)
            }
        }
    }
}