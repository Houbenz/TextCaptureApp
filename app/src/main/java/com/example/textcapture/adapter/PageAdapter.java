package com.example.textcapture.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.textcapture.CopyTextFragment;
import com.example.textcapture.ExportTextFragment;

public class PageAdapter extends FragmentStatePagerAdapter {


    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

       if(position == 0)
           return new ExportTextFragment();
       else
           return new CopyTextFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
