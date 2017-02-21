package com.nutrition.express.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nutrition.express.R;

/**
 * Created by huang on 2/17/17.
 */

public class PhotoFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_photo, container, false);
        return view;
    }

    public void scrollToTop() {

    }

}
