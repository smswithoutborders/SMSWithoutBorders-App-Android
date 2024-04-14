package com.example.sw0b_001.Data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sw0b_001.Data.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Data.EncryptedContent.EncryptedContentDAO;

import java.util.List;

public class RecentsViewModel extends ViewModel {
    private LiveData<List<EncryptedContent>> messagesList;

    public LiveData<List<EncryptedContent>> getMessages(EncryptedContentDAO encryptedContentDAO){
        if(messagesList == null) {
            messagesList = new MutableLiveData<>();
            loadEncryptedContents(encryptedContentDAO);
        }
        return messagesList;
    }

    public void informChanges(EncryptedContentDAO encryptedContentDAO) {
        loadEncryptedContents(encryptedContentDAO);
    }

    private void loadEncryptedContents(EncryptedContentDAO encryptedContentDAO) {
        messagesList = encryptedContentDAO.getAll();
    }
}
