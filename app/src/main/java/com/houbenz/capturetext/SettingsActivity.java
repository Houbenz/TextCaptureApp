package com.houbenz.capturetext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {



    @BindView(R.id.fpsSeekBar)
    SeekBar fpsSeekBar;

    @BindView(R.id.autoFocusSwitch)
    Switch autoFocusSwitch ;

    @BindView(R.id.saveSettings)
    Button saveSettings;

    @BindView(R.id.seekBarValue)
    TextView seekBarValue;


    private int fps;
    private boolean focusMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences =getApplicationContext().getSharedPreferences("file", Context.MODE_PRIVATE);

        fps=sharedPreferences.getInt("fps",15);
        focusMode=sharedPreferences.getBoolean("autofocus",true);

        if(focusMode){
            autoFocusSwitch.setText("ON");
            autoFocusSwitch.setChecked(true);
        }

        fpsSeekBar.setProgress(fps);
        seekBarValue.setText(fps+"");

        fpsSeekBar.setMax(30);

        fpsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int min =15;
                if(progress < min )
                    progress=min;

                fps=progress;

                seekBarValue.setText(fps+"");
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        autoFocusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

            if (compoundButton.getText().equals("ON")){
                compoundButton.setText("OFF");

            }else{
                compoundButton.setText("ON");
            }
            focusMode=b;
        });



        saveSettings.setOnClickListener(view -> {
            sharedPreferences.edit().putInt("fps",fps).putBoolean("autofocus",focusMode).apply();
        });


    }

}
