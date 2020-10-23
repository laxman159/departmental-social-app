package com.e.thedept20.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.thedept20.R;
import com.e.thedept20.Utils.SquareImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import java.util.ArrayList;

public class GridImageApapter extends ArrayAdapter<String>
{
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Context mContex;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imageUrls;

    public GridImageApapter(Context mContex, int layoutResource, String mAppend, ArrayList<String> imageUrls)
        {
        super(mContex, layoutResource, imageUrls);
        this.mContex = mContex;
        mInflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imageUrls = imageUrls;

        }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {


        final ViewHolder holder;
        if (convertView == null)
            {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);
            } else
            {
            holder = (ViewHolder) convertView.getTag();
            }
        String imageURL = getItem(position);

        imageLoader.displayImage(mAppend + imageURL, holder.image, new ImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
                {

                }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                {

                }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                {

                }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
                {

                }
        });

        return convertView;
        }

    private static class ViewHolder
    {
        SquareImageView image;

    }


}
