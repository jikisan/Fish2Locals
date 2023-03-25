package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.My_Order_Cancelled_Fragment;
import Fragments.My_Order_Completed_Fragment;
import Fragments.My_Order_In_Transit_Fragment;
import Fragments.StoreInfoFragment;
import Fragments.StoreProductsFragment;
import Fragments.StoreReviewsFragment;

public class fragmentAdapterMyOrder extends FragmentStateAdapter {
    public fragmentAdapterMyOrder(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new My_Order_Completed_Fragment();
            case 2:
                return new My_Order_Cancelled_Fragment();
        }

        return new My_Order_In_Transit_Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
