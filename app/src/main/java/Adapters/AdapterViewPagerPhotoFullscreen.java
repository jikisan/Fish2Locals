package Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.fish2locals.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.Photos;

public class AdapterViewPagerPhotoFullscreen extends PagerAdapter {

    private Context context;
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private ProgressDialog progressDialog;
    private int currentPosition;
    private String category;


    LayoutInflater mLayoutInflater;

    public AdapterViewPagerPhotoFullscreen() {
    }

    public AdapterViewPagerPhotoFullscreen(Context context, List<Photos> arrUrl, int currentPosition, String category) {
        this.context = context;
        this.arrUrl = arrUrl;
        this.currentPosition = currentPosition;
        this.category = category;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_photo_fullscreen, container, false);


        // referencing the image view from the item.xml file
        ImageView iv_fullScreenPhoto = (ImageView) itemView.findViewById(R.id.iv_fullScreenPhoto);

        // setting the image in the imageView

        Photos photos = arrUrl.get(position);

        String imageUrl = photos.getLink();


        Picasso.get()
                .load(imageUrl)
                .into(iv_fullScreenPhoto);


        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
