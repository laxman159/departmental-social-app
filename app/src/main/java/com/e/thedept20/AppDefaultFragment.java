package com.e.thedept20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class AppDefaultFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    )
        {
        return inflater.inflate(layoutRes(), container, false);
        }

    @Override
    public void onDestroy()
        {
        super.onDestroy();
        }

    @LayoutRes
    protected abstract int layoutRes();
}
