package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.StoreInfoFragment;
import Fragments.StoreProductsFragment;
import Fragments.StoreReviewsFragment;
import Models.Products;

public class fragmentAdapterViewStoreTabs extends FragmentStateAdapter {
    public fragmentAdapterViewStoreTabs(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new StoreInfoFragment();
            case 2:
                return new StoreReviewsFragment();
        }

        return new  StoreProductsFragment();

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
