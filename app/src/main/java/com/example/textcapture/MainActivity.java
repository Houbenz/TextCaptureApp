package com.example.textcapture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_CODE=15;


    @BindView(R.id.scanButton)
     Button scanButton;

    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



    }

    @OnClick(R.id.scanButton)
    public void scanText(){
        Intent intent =new Intent(getApplicationContext(),CaptureTextActivity.class);
        startActivityForResult(intent,REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){

             if(resultCode==RESULT_OK){
                 //Received text from capture activity now is being displayed in textview
                 ArrayList<String> texts= data.getStringArrayListExtra("texts");

                 if(texts != null && !texts.isEmpty()){

                     for(String text : texts){
                         textView.append(" "+text);
                     }

                 }
             }
        }
    }
}
