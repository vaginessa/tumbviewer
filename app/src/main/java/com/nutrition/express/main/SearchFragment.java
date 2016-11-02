package com.nutrition.express.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.helper.SearchHistoryHelper;
import com.nutrition.express.videolist.VideoListActivity;

/**
 * Created by huang on 11/2/16.
 */

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchHistoryHelper historyHelper;
    private CommonRVAdapter adapter;
    private boolean loaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.blog_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            showSearchHistory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            showSearchHistory();
        }
    }

    private void showSearchHistory() {
        if (loaded) {
            return;
        }
        historyHelper = new SearchHistoryHelper();

        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.setData(historyHelper.getHistories().toArray());
        builder.addItemType(String.class, R.layout.item_text, new CommonRVAdapter.CreateViewHolder() {
            @Override
            public CommonViewHolder createVH(View view) {
                return new BaseVH(view);
            }
        });
        adapter = builder.build();
        recyclerView.setAdapter(adapter);
        loaded = true;
    }


    public class BaseVH extends CommonViewHolder<String>
            implements View.OnClickListener, View.OnLongClickListener {
        String name;

        public BaseVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void bindView(String object) {
            ((TextView) itemView).setText(object);
            name = object;
        }

        @Override
        public void onClick(View v) {
            openPostsVideo(name);
        }

        @Override
        public boolean onLongClick(View v) {
            showDeleteDialog(name, getAdapterPosition());
            return true;
        }
    }
    public void openPostsVideo(String blogName) {
        Intent intent = new Intent(getActivity(), VideoListActivity.class);
        intent.putExtra("blog_name", blogName);
        startActivity(intent);
        historyHelper.add(blogName);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(final  String text, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("删除记录" + text + "?");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                historyHelper.remove(text);
                adapter.notifyItemRemoved(position);
            }
        });
        builder.create().show();
    }

}