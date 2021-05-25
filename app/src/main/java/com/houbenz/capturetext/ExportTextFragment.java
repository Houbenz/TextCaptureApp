package com.houbenz.capturetext;


import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.houbenz.capturetext.viewmodel.TextViewModel;
import butterknife.ButterKnife;

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


    Button scanBtn;
    ActivityResultLauncher<Integer> mGetContent ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_export_text, container, false);
        ButterKnife.bind(this,view);

        scanBtn = view.findViewById(R.id.scanBtn);

        TextViewModel viewModel = new ViewModelProvider(requireActivity()).get(TextViewModel.class);

        scanBtn.setOnClickListener( v  -> {

            /*Intent intent = new Intent(getActivity(), CaptureTextActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            viewModel.setCurrentPage(COPY_PAGE);*/

            mGetContent.launch(1);
        });


        mGetContent = registerForActivityResult(new TextContract(getActivity(),CaptureTextActivity.class), result ->{

            viewModel.setTexts(result);
            viewModel.setCurrentPage(COPY_PAGE);
        });

        return view;
    }

}
