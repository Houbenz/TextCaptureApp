package com.houbenz.capturetext.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TextViewModel extends ViewModel {


    /**
     * texts
     */
    private MutableLiveData<ArrayList<String>> texts;


    public void setTexts(ArrayList<String> texts){
        this.texts.setValue(texts);
    }

    public LiveData<ArrayList<String>> getTexts(){
        if(texts == null )
            texts= new MutableLiveData<ArrayList<String>>();

        return texts;
    }


    /**
     * current page
     */

    private final MutableLiveData<Integer> currentPage = new MutableLiveData<Integer>();;


    public void setCurrentPage(int page){
        currentPage.setValue(page);
    }
    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }

}
