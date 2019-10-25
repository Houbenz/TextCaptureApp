package com.example.textcapture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.scanButton)
     Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



    }

    @OnClick(R.id.scanButton)
    public void scanText(){
        Intent intent =new Intent(getApplicationContext(),CaptureTextActivity.class);
        startActivity(intent);
    }

}
