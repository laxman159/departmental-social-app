package com.e.thedept20.AddPost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.Utils.FilePaths;
import com.e.thedept20.Utils.FileSearch;
import com.e.thedept20.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


/**
 * Created by User on 5/28/2017.
 */

public class GalleryFragment extends Fragment
{
    private static final String TAG = "GalleryFragment";


    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gridView);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");
        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                }
        });


        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Log.d(TAG, "onClick: navigating to the final share screen.");


                Intent intent = new Intent(getContext(), NextActivity.class);
                intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                startActivity(intent);


                //  intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                // intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                // startActivity(intent);
                // getActivity().finish();


                }
        });

        init();

        return view;
        }

    private boolean isRootTask()
        {
        return ((ShareActivity) getActivity()).getTask() == 0;
        }

    private void init()
        {
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        FileSearch.getDirectoryPaths(filePaths.PICTURES);
        directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++)
            {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
            }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
                {

                }
        });
        }


    private void setupGridView(String selectedDirectory)
        {
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try
            {
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
            } catch (ArrayIndexOutOfBoundsException e)
            {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
            }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
                }
        });

        }


    private void setImage(String imgURL, ImageView image, String append)
        {
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener()
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
        }
}































