package com.e.thedept20.AddPost;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.e.thedept20.R;
import com.e.thedept20.Utils.Permissions;
import com.e.thedept20.Utils.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * Created by User on 5/28/2017.
 */

public class ShareActivity extends AppCompatActivity
{
    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;
    private TabLayout ShareTabLayout;


    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started.");

        if (checkPermissionsArray(Permissions.PERMISSIONS))
            {
            setupViewPager();
            } else
            {
            verifyPermissions(Permissions.PERMISSIONS);
            //startActivity(new Intent(getApplicationContext(),ShareActivity.class));
            }
        }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     *
     * @return
     */
    public int getCurrentTabNumber()
        {
        return mViewPager.getCurrentItem();
        }

    /**
     * setup viewpager for manager the tabs
     */

    private void setupViewPager()
        {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment()); //index 0
        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new TextShareFragment());
        mViewPager = findViewById(R.id.share_tabs_pager);//index 1
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        ShareTabLayout = findViewById(R.id.share_user_tablayout);
        ShareTabLayout.setupWithViewPager(mViewPager);

        ShareTabLayout.getTabAt(0).setText("gallery");
        ShareTabLayout.getTabAt(1).setText("Camera");
        ShareTabLayout.getTabAt(2).setText("Notice");


        }

    public int getTask()
        {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
        }

    /**
     * verifiy all the permissions passed to the array
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions)
        {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST

        );

        }

    /**
     * Check an array of permissions
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions)
        {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++)
            {
            String check = permissions[i];
            if (!checkPermissions(check))
                {
                return false;
                }
            }
        return true;
        }

    /**
     * Check a single permission is it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission)
        {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED)
            {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
            } else
            {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
            }
        }

    /**
     * BottomNavigationView setup
     */

}
