package com.example.textcapture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_CODE = 15;
    public static final int HANDLE_WRITE_PER=5;
    public static final int CREATE_FILE_REQUEST_CODE=20;


    byte[] data;

    private Uri fileUri;

    @BindView(R.id.scanButton)
    Button scanButton;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.saveFile)
    Button saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.scanButton)
    public void scanText() {
        Intent intent = new Intent(getApplicationContext(), CaptureTextActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.saveFile)
    public void saveFileOnClick(){
        int rc= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if(rc == PackageManager.PERMISSION_GRANTED){


                if(isExternalStorageAvailable())
                    saveFile("text/plain","File"+System.nanoTime()+".txt");

        }else{
            requestWritePermission();
        }
    }


    /**
     * Check if is storage is available
     * @return
     */
    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
            return true;
        else
            return false;

    }

    /**
     * start an activity result with filename and mimetype to save the file
     * @param memeType
     * @param filename
     */
    private void saveFile(String memeType,String filename){
        Intent intent =new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(memeType);
        intent.putExtra(Intent.EXTRA_TITLE,filename);
        startActivityForResult(intent,CREATE_FILE_REQUEST_CODE);
    }


    /**
     * Receive data from CaptureTextActivity, also used to store received data into files
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                //Received text from capture activity now is being displayed in textview
                ArrayList<String> texts = data.getStringArrayListExtra("texts");
                //this.data =data.getByteArrayExtra("data");

                if (texts != null && !texts.isEmpty()) {

                    for (String text : texts) {
                        textView.append(" " + text);
                    }
                }
            }
        }

        if (requestCode == CREATE_FILE_REQUEST_CODE ) {

            if (resultCode == RESULT_OK) {

                fileUri = data.getData();
                writeToFile(fileUri);
            }
        }
    }


    /**
     * write to file created in OnActivityResult the data received
     * @param uri
     */
    private void writeToFile(Uri uri){
        if(uri != null){
            try {

              ParcelFileDescriptor parcelFileDescriptor =  getApplicationContext().getContentResolver().openFileDescriptor(uri,"w");
              FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

              BufferedWriter bufferedWriter =new BufferedWriter(new FileWriter(fileDescriptor));
              bufferedWriter.write(textView.getText().toString());
              bufferedWriter.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }else
            Toast.makeText(getApplicationContext(),"An error occured" ,Toast.LENGTH_LONG).show();
    }



    private void requestWritePermission(){
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(! ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this,permissions,HANDLE_WRITE_PER);
        }
    }
}

