package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.Sellers_Order_Cancelled_Fragment;
import Fragments.Sellers_Order_Completed_Fragment;
import Fragments.Sellers_Order_In_Transit_Fragment;

public class fragmentAdapterSellersOrder extends FragmentStateAdapter {
    public fragmentAdapterSellersOrder(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new Sellers_Order_Completed_Fragment();
            case 2:
                return new Sellers_Order_Cancelled_Fragment();
        }

        return new Sellers_Order_In_Transit_Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
