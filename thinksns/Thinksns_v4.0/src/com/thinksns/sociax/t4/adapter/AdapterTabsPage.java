package com.thinksns.sociax.t4.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedong on 16/3/5.
 */
public class AdapterTabsPage extends FragmentPagerAdapter{
    List<String> pageTitles;
    List<Fragment> fragmentList;

    public AdapterTabsPage(FragmentManager fm) {
        super(fm);
        pageTitles = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
    }

    public AdapterTabsPage addTab(String title, Fragment fragment) {
        pageTitles.add(title);
        fragmentList.add(fragment);
        return this;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return pageTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }
}
