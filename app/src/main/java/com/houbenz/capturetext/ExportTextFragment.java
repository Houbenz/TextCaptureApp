package com.houbenz.capturetext;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.houbenz.capturetext.R;
import com.houbenz.capturetext.viewmodel.TextViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExportTextFragment extends Fragment {

    public static final int REQUEST_CODE = 15;
    public static final int HANDLE_WRITE_PER=5;

    public static final int EXPORT_PAGE=0;
    public static final int COPY_PAGE=1;


    public ExportTextFragment() {
        // Required empty public constructor
    }


    private TextViewModel viewModel;

    @BindView(R.id.scanBtn)
    Button scanBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_export_text, container, false);
        ButterKnife.bind(this,view);

        viewModel= ViewModelProviders.of(getActivity()).get(TextViewModel.class);

        return view;
    }

    @OnClick(R.id.scanBtn)
    public void scanText() {
        Intent intent = new Intent(getActivity(), CaptureTextActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        viewModel.setCurrentPage(COPY_PAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                //Received text from capture activity now is being displayed in textview
                ArrayList<String> texts = data.getStringArrayListExtra("texts");

                if (texts != null && !texts.isEmpty()) {


                    //transfer the data to activity and other fragments
                    viewModel.setTexts(texts);


                }
            }
        }


    }
}
