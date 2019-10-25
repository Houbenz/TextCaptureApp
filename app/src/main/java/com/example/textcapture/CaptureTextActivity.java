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
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.example.textcapture.ui.camera.CameraSourcePreview;
import com.example.textcapture.ui.camera.GraphicOverlay;
import com.example.textcapture.ui.camera.CameraSource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaptureTextActivity extends AppCompatActivity {


    private CameraSource cameraSource;
    @BindView(R.id.preview)
     CameraSourcePreview preview;
    @BindView(R.id.graphicOverlay)
     GraphicOverlay<OcrGraphic> graphicOverlay;

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

        Snackbar.make(graphicOverlay, "Tap to Speak. Pinch/Stretch to zoom",
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
}