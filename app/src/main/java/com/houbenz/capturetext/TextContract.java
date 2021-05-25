package com.houbenz.capturetext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class TextContract  extends ActivityResultContract<Integer, ArrayList<String>> {


    private final FragmentActivity fragmentActivity;
    private final Class<?> resultActivity;

    public TextContract(FragmentActivity fragmentActivity, Class<?> resultActivity) {
        this.fragmentActivity = fragmentActivity;
        this.resultActivity = resultActivity;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Integer input) {
        return new Intent(fragmentActivity,resultActivity);
    }

    @Override
    public ArrayList<String>  parseResult(int resultCode, @Nullable Intent result) {
        if (resultCode != Activity.RESULT_OK || result == null) {
            return null;
        }
        return  result.getStringArrayListExtra("texts");
    }
}
