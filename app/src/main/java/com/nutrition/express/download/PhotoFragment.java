package com.nutrition.express.download;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.imageviewer.PhotoViewActivity;
import com.nutrition.express.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 2/17/17.
 */

public class PhotoFragment extends Fragment {
    private final int DEFAULT_PHOTO_WIDTH = ExpressApplication.width / 2;
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;

    private List<Object> photos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        photos = new ArrayList<>();
        File photoDir = FileUtils.getImageDir();
        File[] files = photoDir.listFiles();
        if (files != null && files.length > 0) {
            LocalPhoto tmp;
            for (File file : photoDir.listFiles()) {
                tmp = new LocalPhoto(file);
                if (tmp.isValid()) {
                    photos.add(new LocalPhoto(file));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_photo, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = CommonRVAdapter.newBuilder()
                .addItemType(LocalPhoto.class, R.layout.item_download_photo,
                        new CommonRVAdapter.CreateViewHolder() {
                            @Override
                            public CommonViewHolder createVH(View view) {
                                return new PhotoViewHolder(view);
                            }
                        })
                .setData(photos)
                .build();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Fresco.getImagePipeline().resume();
                } else {
                    Fresco.getImagePipeline().pause();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_download_photo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_photo) {
            FileUtils.deleteFile(FileUtils.getImageDir());
            onAllPhotosDeleted();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    public void onAllPhotosDeleted() {
        adapter.resetData(null, false);
    }

    public void onPhotoDeleted(int pos) {
        adapter.remove(pos);
    }

    private final class PhotoViewHolder extends CommonViewHolder<LocalPhoto>
            implements View.OnClickListener, View.OnLongClickListener {
        private SimpleDraweeView photoView;

        private LocalPhoto photo;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.photoView);
            photoView.setOnClickListener(this);
            photoView.setOnLongClickListener(this);
        }

        @Override
        public void bindView(LocalPhoto localPhoto) {
            photo = localPhoto;
            int height = localPhoto.getHeight() * DEFAULT_PHOTO_WIDTH / localPhoto.getWidth();
            ViewGroup.LayoutParams params = photoView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(DEFAULT_PHOTO_WIDTH, height);
            }
            params.width = DEFAULT_PHOTO_WIDTH;
            params.height = height;
            photoView.setLayoutParams(params);
            photoView.setImageURI(localPhoto.getUri());
        }

        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), photoView, photo.getUri().getPath());
                intent.putExtra("transition_name", photo.getUri().getPath());
                intent.putExtra("photo_source", photo.getUri());
                startActivity(intent, options.toBundle());
            } else {
                Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                intent.putExtra("photo_source", photo.getUri());
                startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FileUtils.deleteFile(photo.getFile());
                    onPhotoDeleted(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.pic_cancel, null);
            builder.setTitle(R.string.download_delete_title);
            builder.show();
            return true;
        }
    }

}
