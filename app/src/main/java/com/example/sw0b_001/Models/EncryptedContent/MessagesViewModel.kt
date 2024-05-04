package com.example.sw0b_001.Models.EncryptedContent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore

class MessagesViewModel : ViewModel() {

    private lateinit var messagesList: LiveData<List<EncryptedContent>>

    private lateinit var datastore: Datastore
    fun getMessages(context: Context): LiveData<List<EncryptedContent>> {
        if (!::messagesList.isInitialized) {
            datastore = Datastore.getDatastore(context)
            messagesList = loadEncryptedContents()
        }
        return messagesList
    }

    private fun loadEncryptedContents() :
            LiveData<List<EncryptedContent>>{
        return datastore.encryptedContentDAO().all
    }

    fun insert(encryptedContent: EncryptedContent) : Long {
        return datastore.encryptedContentDAO().insert(encryptedContent)
    }
}
