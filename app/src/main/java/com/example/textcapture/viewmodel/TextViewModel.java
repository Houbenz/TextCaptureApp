package com.example.textcapture.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TextViewModel extends ViewModel {


    /**
     * texts
     */
    private MutableLiveData<ArrayList<String>> texts = new MutableLiveData<>();


    public void setTexts(ArrayList<String> texts){
        this.texts.setValue(texts);
    }

    public LiveData<ArrayList<String>> getTexts(){
        return texts;
    }


    /**
     * current page
     */

    private MutableLiveData<Integer> currentPage = new MutableLiveData<>();
    public void setCurrentPage(int page){
        currentPage.setValue(page);
    }
    public LiveData<Integer> getCurrentPage(){
        return currentPage;
    }

}
