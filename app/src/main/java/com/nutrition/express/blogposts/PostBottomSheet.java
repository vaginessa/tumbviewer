package com.nutrition.express.blogposts;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.application.SystemDownload;
import com.nutrition.express.imageviewer.ImageViewerActivity;
import com.nutrition.express.util.Utils;
import com.nutrition.express.videoplayer.VideoPlayerActivity;

import java.util.ArrayList;

/**
 * Created by huang on 5/30/16.
 */
public class PostBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    BottomSheetBehavior behavior;
    private String videoUrl;
    private String imageUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        videoUrl = bundle.getString("video_url");
        imageUrl = bundle.getString("image_url");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.bottom_sheet_video_list, null);
        TextView textView = (TextView) view.findViewById(R.id.item_link);
        textView.setText(videoUrl);
        textView.setOnClickListener(this);
        view.findViewById(R.id.item_play).setOnClickListener(this);
        view.findViewById(R.id.item_view_image).setOnClickListener(this);
        view.findViewById(R.id.item_download).setOnClickListener(this);
        view.findViewById(R.id.item_copy).setOnClickListener(this);
        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_play:
                Intent playerIntent = new Intent(getContext(), VideoPlayerActivity.class);
                playerIntent.putExtra("url", videoUrl);
                startActivity(playerIntent);
                break;
            case R.id.item_view_image:
                ArrayList<String> urls = new ArrayList<>();
                urls.add(imageUrl);
                Intent intent = new Intent(getContext(), ImageViewerActivity.class);
                intent.putStringArrayListExtra("image_urls", urls);
                getContext().startActivity(intent);
                break;
            case R.id.item_download:
                SystemDownload.downloadVideo(getActivity(), videoUrl);
                break;
            case R.id.item_copy:
                Utils.copy2Clipboard(getActivity(), videoUrl);
                break;
            case R.id.item_link:
                if (!TextUtils.isEmpty(videoUrl)) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
                }
                break;
        }
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
