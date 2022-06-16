package com.entscz.krizovezasoby;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DonatePagerAdapter extends FragmentStateAdapter {

    public DonatePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            default:
            case 0:
                return new CharityListFragment();
            case 1:
                return new CharityMapFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
