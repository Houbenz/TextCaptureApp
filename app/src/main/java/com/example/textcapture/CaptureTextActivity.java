package com.example.textcapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.example.textcapture.ui.camera.CameraSource;
import com.example.textcapture.ui.camera.CameraSourcePreview;
import com.example.textcapture.ui.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaptureTextActivity extends AppCompatActivity {


     CameraSource cameraSource;
    @BindView(R.id.preview)
     CameraSourcePreview preview;
    @BindView(R.id.graphicOverlay)
     GraphicOverlay<OcrGraphic> graphicOverlay;


    private ArrayList<String> texts=new ArrayList<String>();

    private static final int RC_HANDLE_CAMERA_PER= 2;
    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = "OcrCaptureActivity";

    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_text);
        ButterKnife.bind(this);

        boolean autoFocus=true;
        boolean autoFlash=false;

        int rc= ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if(rc == PackageManager.PERMISSION_GRANTED){

            createCameraSource(autoFocus,autoFlash);
        }else {

                requestCameraPermission();
        }


        gestureDetector = new GestureDetector(this, new CaptureGestureDetector());

        Snackbar.make(graphicOverlay, "Tap to capture text, press return button to finsih",
                Snackbar.LENGTH_LONG)
                .show();


    }


    private void requestCameraPermission(){

        final String[]  permissions = new String[]{Manifest.permission.CAMERA};

        if( ! ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){

            ActivityCompat.requestPermissions(this ,permissions,RC_HANDLE_CAMERA_PER);

            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity,permissions,RC_HANDLE_CAMERA_PER);

        Snackbar.make(graphicOverlay,R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok,listener)
                .show();

    }

    private void createCameraSource(boolean autoFocus,boolean useFlash){

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        IntentFilter lowStorageFilter= new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);

        boolean hasLowStorage=registerReceiver(null,lowStorageFilter) != null ;

        if(hasLowStorage){
            Toast.makeText(this,R.string.low_storage_error,Toast.LENGTH_LONG).show();
            Log.w(TAG,getString(R.string.low_storage_error));

        }

        cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280,1024)
                .setRequestedFps(15.0f)
                .setFlashMode(useFlash ? (Camera.Parameters.FLASH_MODE_AUTO) : null)
                .setFocusMode(autoFocus ? (Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO):null)
                .build();


        /*cameraSource=new CameraSource.Builder(getApplicationContext(),textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280,1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(15.0f)
                .build();*/
        textRecognizer.setProcessor(new OcrProcessorDetector(graphicOverlay));

    }

    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PER) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean c = gestureDetector.onTouchEvent(event);
        return c || super.onTouchEvent(event);
    }

    private boolean onTap(float rawX, float rawY) {

     /*
        OcrGraphic graphic=graphicOverlay.getGraphicAtLocation(rawX,rawY);


        TextBlock text= null;

        if(graphic != null){

            text=graphic.getTextBlock();

            if (text != null && text.getValue() != null){
                Log.d(TAG,"Text data is being spoken! "+ text.getValue());

                //show the text inside the tapped box
                Toast.makeText(getApplicationContext(),text.getValue(),Toast.LENGTH_LONG).show();
                //transfers the tapped word to the main activity

                texts.add(text.getValue());
            }
            else {
                Log.d(TAG,"Text data is null");
            }

        }else {
            Log.d(TAG,"No Text detected");
        }


*/

        cameraSource.takePicture(() -> {

        }, data -> {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;

            Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length,options);

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            SparseArray<TextBlock>  items =textRecognizer.detect(frame);

            cameraSource.stop();

            Canvas tempCanvas = new Canvas(bitmap);

            tempCanvas.drawBitmap(bitmap,0,0,null);

            for (int i=0;i<items.size();i++){
                texts.add(items.get(i).getValue());
            }


        });


        return true;
    }


    @Override
    public void onBackPressed() {

        Intent intent = getIntent();
        intent.putStringArrayListExtra("texts",texts);
        intent.putExtra("page",1);
        setResult(RESULT_OK,intent);

        finish();
    }

    private class CaptureGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(),e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }


}
