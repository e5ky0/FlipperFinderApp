package com.pinmyballs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores Fragments for tabs
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter{
    private static final String TAG = "SectionsPagerAdapter";
    private final List<Fragment> mFragmentList = new ArrayList<Fragment>();

     public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
         mFragmentList.add(fragment);
    }
}
