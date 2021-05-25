package com.houbenz.capturetext;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.houbenz.capturetext.R;
import com.houbenz.capturetext.viewmodel.TextViewModel;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CopyTextFragment extends Fragment {





    @BindView(R.id.scannedtextLayout)
    TextInputLayout scannedTextlayout;

    @BindView(R.id.copyClipboard)
    ImageButton copyClipboard;

    private TextViewModel viewModel;


    public CopyTextFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_copy_text, container, false);

        ButterKnife.bind(this,view);

        viewModel= new ViewModelProvider(requireActivity()).get(TextViewModel.class);

        viewModel.getTexts().observe(getViewLifecycleOwner(),texts ->{

            Log.i("kkk", "onCreateView: copyText");
            scannedTextlayout.getEditText().setText("");
            for(String text : texts){
                scannedTextlayout.getEditText().append(text);
            }

        });

        return view;
    }


    @OnClick(R.id.copyClipboard)
    public void copyText(){
        ClipboardManager clipboardManager =(ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        String text =scannedTextlayout.getEditText().getText().toString();

        if(!text.equals("")){

            ClipData clipData =ClipData.newPlainText("scannedText",text);

            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(getContext(),"Text copied !",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(),"nothing to copy",Toast.LENGTH_LONG).show();

        }
    }

}
