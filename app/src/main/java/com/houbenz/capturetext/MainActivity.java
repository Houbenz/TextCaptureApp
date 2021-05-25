package com.houbenz.capturetext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.houbenz.capturetext.adapter.PageAdapter;
import com.houbenz.capturetext.viewmodel.TextViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {


    public static final int REQUEST_CODE = 15;
    public static final int HANDLE_WRITE_PER=5;
    public static final int CREATE_FILE_REQUEST_CODE=20;


    byte[] data;

    private Uri fileUri1;

    private InterstitialAd interstitialAd;

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager=findViewById(R.id.viewPager);
        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.open_navigation,R.string.close_navigation);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.afterscan_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd tInterstitialAd) {
                super.onAdLoaded(tInterstitialAd);
                interstitialAd=tInterstitialAd;
                addFullScreenCallBackToInterstitial();

                Log.i("RRR","Onloaded interstitial");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                interstitialAd=null;
                Log.i("RRR", "onAdFailedToLoad: ");
            }
        });


        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pageAdapter);

        TextViewModel viewModel = new ViewModelProvider(this).get(TextViewModel.class);
        //to show the ad  when the data is exported to the fragment
        viewModel.getTexts().observe(this, texts ->  {
            if(texts !=null && interstitialAd != null)
                interstitialAd.show(MainActivity.this);
            else
                Log.i("LOOL","didnt show yet");


        });

        viewModel.getCurrentPage().observe(this,page -> {
            viewPager.setCurrentItem(page);
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0: bottomNavigationView.getSelectedItemId(); break;

                    case 1: break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            menuItem.setChecked(false);

            switch (menuItem.getItemId())
            {
                case R.id.scan:
                    viewPager.setCurrentItem(0,true);
                    menuItem.setChecked(true);
                    break;

                case R.id.copyText:
                    viewPager.setCurrentItem(1,true);
                    menuItem.setChecked(true);
                    break;
            }

            return false;});

    }


    void addFullScreenCallBackToInterstitial(){
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                interstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });

    }

/*
    @OnClick(R.id.scanButton)
    public void scanText() {
        Intent intent = new Intent(getApplicationContext(), CaptureTextActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
  */

/*
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
*/

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
     *//*
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
*/

    /**
     * write to file created in OnActivityResult the data received
     * @param
     *//*
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
*/


    private void requestWritePermission(){
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(! ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this,permissions,HANDLE_WRITE_PER);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()){
            case R.id.review :
                    Uri uri = Uri.parse(getString(R.string.app_url));
                    Intent openStore=new Intent(Intent.ACTION_VIEW,uri);

                    openStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    try {
                        startActivity(openStore);
                    }catch (ActivityNotFoundException e){
                        Log.i("ActError",e.getMessage()+"");
                    }
                break;

            case R.id.remove_ad:
                break;

            case R.id.settings:
                    Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    startActivity(intent);
                break;
        }


        return false;
    }
}

