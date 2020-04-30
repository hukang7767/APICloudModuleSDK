package com.alpha.modulegnoga.measurement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class WaveChartFragmentPagerAdapter extends FragmentPagerAdapter {

    /** fragment list */
    private ArrayList<Fragment> fragmentArrayList;

    public WaveChartFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArrayList) {

        super(fm);
        this.fragmentArrayList = fragmentArrayList;
    }

    public void clear() {
        fragmentArrayList.clear();
        fragmentArrayList = null;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentArrayList.get(i);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
