package com.example.anurag.anurag_chat_app;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by anurag on 19/1/18.
 */

class SectionPageAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> mFragment =new ArrayList<>();
    ArrayList<String> mTitle=new ArrayList<>();
    public SectionPageAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    public void addFragments( Fragment fragment,String title)
    {
      mFragment.add(fragment);
      mTitle.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }
    public CharSequence getPageTitle(int position)
    {
        return mTitle.get(position);
    }
}
