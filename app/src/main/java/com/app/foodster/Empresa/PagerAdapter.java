package com.app.foodster.Empresa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES = {"Empresas", "Eventos"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    public int getCount() {
        return TITLES.length;
    }


    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return new Empresas();
            case 1:

                return new Eventos();
        }

        return null;
    }
}
