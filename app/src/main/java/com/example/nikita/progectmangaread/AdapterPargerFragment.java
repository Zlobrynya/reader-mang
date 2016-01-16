package com.example.nikita.progectmangaread;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Nikita on 16.01.2016.
 */
public class AdapterPargerFragment extends FragmentPagerAdapter {
    public AdapterPargerFragment(FragmentManager mgr) {
        super(mgr);
    }
    @Override
    public int getCount() {
        return(10);
    }
    @Override
    public Fragment getItem(int position) {
        return(fragmentTemplePase.newInstance(position));
    }
}
