package com.houbenz.capturetext.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.houbenz.capturetext.CopyTextFragment;
import com.houbenz.capturetext.ExportTextFragment;

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
