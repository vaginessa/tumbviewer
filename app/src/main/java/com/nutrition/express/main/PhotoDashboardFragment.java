package com.nutrition.express.main;

import android.view.View;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;
import com.nutrition.express.photolist.PhotoVH;

/**
 * Created by huang on 11/2/16.
 */

public class PhotoDashboardFragment extends DashboardFragment {
    @Override
    protected CommonRVAdapter getAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PostsItem.class, R.layout.item_photo,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new PhotoVH(view);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    @Override
    protected String getType() {
        return "photo";
    }
}
