package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fish2locals.R;

public class Adapter_Spinner_Fish extends BaseAdapter {

    Context context;
    int images[];
    String[] fish;
    LayoutInflater inflter;

    public Adapter_Spinner_Fish() {
    }

    public Adapter_Spinner_Fish(Context context, int[] images, String[] fish) {
        this.context = context;
        this.images = images;
        this.fish = fish;
        inflter = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.item_custom_spinner, null);
        ImageView iv_itemFishPhoto = (ImageView) view.findViewById(R.id.iv_itemFishPhoto);
        TextView tv_fishName = (TextView) view.findViewById(R.id.tv_fishName);
        iv_itemFishPhoto.setImageResource(images[i]);
        tv_fishName.setText(fish[i]);
        return view;
    }
}
