package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ViewAllStoreListViewFragment;
import Fragments.ViewAllStoreMapViewFragment;

public class fragmentAdapterViewAllStore extends FragmentStateAdapter {
    public fragmentAdapterViewAllStore(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 1)
        {
            return new ViewAllStoreMapViewFragment();
        }

        return new ViewAllStoreListViewFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
