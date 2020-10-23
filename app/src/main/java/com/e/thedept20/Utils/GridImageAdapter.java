package com.e.thedept20.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.e.thedept20.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by User on 6/4/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String>
{

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs)
        {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
        }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
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

        String imgURL = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(mAppend + imgURL, holder.image, new ImageLoadingListener()
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
        ProgressBar mProgressBar;
    }
}



















