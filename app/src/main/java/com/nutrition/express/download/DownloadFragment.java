package com.nutrition.express.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.downloadservice.DownloadService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 4/10/17.
 */

public class DownloadFragment extends Fragment {
    private List<Object> data = new ArrayList<>();
    private DownloadService downloadService;

    private CommonRVAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<String> list = bundle.getStringArrayList(DownloadService.DOWNLOAD_LIST);
            if (list != null) {
                data.addAll(list);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = CommonRVAdapter.newBuilder()
                .addItemType(String.class, R.layout.item_download, new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new DownloadVH(view);
                    }
                })
                .setData(data)
                .build();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.start);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DownloadService.class);
                getContext().startService(intent);
            }
        });
        return view;
    }

    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    private void cancelDownloadTarget(int pos) {
        if (downloadService != null) {
            downloadService.cancelDownloadTarget((String) data.get(pos));
            data.remove(pos);
            adapter.notifyItemRemoved(pos);
        }
    }

    private class DownloadVH extends CommonViewHolder<String> implements View.OnClickListener {
        private TextView textView;
        private ImageView cancelView;

        public DownloadVH(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            cancelView = (ImageView) itemView.findViewById(R.id.cancel);
            cancelView.setOnClickListener(this);
        }

        @Override
        public void bindView(String s) {
            textView.setText(s);
        }

        @Override
        public void onClick(View v) {
            cancelDownloadTarget(getAdapterPosition());
        }
    }
}
