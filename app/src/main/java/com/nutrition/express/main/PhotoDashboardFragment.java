package com.nutrition.express.main;

import android.view.View;

import com.nutrition.express.R;
import com.nutrition.express.blogposts.PhotoPostVH;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.data.bean.PhotoPostsItem;

/**
 * Created by huang on 11/2/16.
 */

public class PhotoDashboardFragment extends DashboardFragment {
    @Override
    protected CommonRVAdapter getAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(PhotoPostsItem.class, R.layout.item_post,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new PhotoPostVH(view);
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
