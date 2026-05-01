package com.boop.app.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:  return OnboardingStepFragment.newInstance(2);
            case 2:  return OnboardingStepFragment.newInstance(3);
            default: return OnboardingStepFragment.newInstance(1);
        }
    }

    @Override
    public int getItemCount() { return 3; }
}
