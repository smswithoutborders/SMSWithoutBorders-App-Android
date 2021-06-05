package com.example.sw0b_001;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SMSSateViewModel extends ViewModel {
    private MutableLiveData<Boolean> stateChanged;

    public MutableLiveData<Boolean> getStateChanged() {
        if (stateChanged == null ){
            stateChanged = new MutableLiveData<>();
        }

        return stateChanged;
    }
}
