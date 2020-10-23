package com.e.thedept20.Utils;

import android.content.Context;
import android.os.Environment;

import androidx.core.os.EnvironmentCompat;

/**
 * Created by User on 7/24/2017.
 */

public class FilePaths
{


    //
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();


    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";


    //public String FIREBASE_IMAGE_STORAGE = "photos/Users/";

}
